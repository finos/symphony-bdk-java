package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5DatafeedCreateBody;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A class for implementing the datafeed v2 loop service.
 * <p>
 * This service will be started by calling {@link DatafeedLoopV2#start()}
 * <p>
 * At the beginning, the BDK bot will try to retrieve the list of datafeed to which it is listening. Since each bot
 * should only listening to just one datafeed, the first datafeed in the list will be used by the bot to be listened to.
 * If the retrieved list is empty, the BDK bot will create a new datafeed to listen.
 * <p>
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 * <p>
 * If this datafeed becomes stale or faulty, the BDK bot will create the new one for listening.
 * <p>
 * This service will be stopped by calling {@link DatafeedLoopV2#stop()}
 * <p>
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedLoopV2 extends AbstractAckIdEventLoop {

  /**
   * DFv2 API authorizes a maximum length for the tag parameter.
   */
  private static final int DATAFEED_TAG_MAX_LENGTH = 100;

  /**
   * Regex pattern of fanout feeds, e.g. "e766c498eece0d113f035270ad6c3c63_f_f8001".
   * We must exclude potential datahose feeds when we are reusing a datafeed.
   * Datahose feeds are in the format *_p_*, e.g. "d25098517ec62f1fc65cd111667a8386_p_be940".
   */
  private static final Pattern FANOUT_FEED_PATTERN = Pattern.compile("^[^\\s_]+_f(_[^\\s_]+)?$");

  private final RetryWithRecoveryBuilder<?> retryWithRecoveryBuilder;
  private final RetryWithRecovery<Void> readDatafeed;
  private final RetryWithRecovery<V5Datafeed> retrieveDatafeed;
  private final RetryWithRecovery<V5Datafeed> createDatafeed;
  private final RetryWithRecovery<Void> deleteDatafeed;
  private final String tag;

  private V5Datafeed datafeed;

  public DatafeedLoopV2(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, UserV2 botInfo) {
    super(datafeedApi, authSession, config, botInfo);
    this.tag = StringUtils.truncate(config.getBot().getUsername(), DATAFEED_TAG_MAX_LENGTH);

    this.retryWithRecoveryBuilder = new RetryWithRecoveryBuilder<>()
        .basePath(datafeedApi.getApiClient().getBasePath())
        .retryConfig(config.getDatafeedRetryConfig())
        .recoveryStrategy(ApiException::isUnauthorized, this::refresh);

    this.readDatafeed = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Read Datafeed V2")
        .supplier(this::readAndHandleEvents)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkIssueOrMinorErrorOrClientError)
        .recoveryStrategy(ApiException::isClientError, this::recreateDatafeed)
        .build();

    this.retrieveDatafeed = RetryWithRecoveryBuilder.<V5Datafeed>from(retryWithRecoveryBuilder)
        .name("Retrieve Datafeed V2")
        .supplier(this::doRetrieveDatafeed)
        .build();

    this.createDatafeed = RetryWithRecoveryBuilder.<V5Datafeed>from(retryWithRecoveryBuilder)
        .name("Create Datafeed V2")
        .supplier(this::doCreateDatafeed)
        .build();

    this.deleteDatafeed = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Delete Datafeed V2")
        .supplier(this::doDeleteDatafeed)
        .ignoreException(ApiException::isClientError)
        .build();
  }

  @Override
  protected void runLoop() throws Throwable {
    this.datafeed = this.retrieveDatafeed.execute();
    if (this.datafeed == null) {
      this.datafeed = this.createDatafeed.execute();
    }

    log.info("Start reading events from datafeed {}", this.datafeed.getId());
    this.started.set(true);
    do {

      this.readDatafeed.execute();

    } while (this.started.get());
    log.info("Datafeed loop successfully stopped.");
  }

  private V5Datafeed doCreateDatafeed() throws ApiException {
    this.ackId = INITIAL_ACK_ID;
    return this.datafeedApi.createDatafeed(
        this.authSession.getSessionToken(),
        this.authSession.getKeyManagerToken(),
        new V5DatafeedCreateBody().tag(this.tag)
    );
  }

  private V5Datafeed doRetrieveDatafeed() throws ApiException {
    final List<V5Datafeed> feeds = this.datafeedApi.listDatafeed(
        this.authSession.getSessionToken(),
        this.authSession.getKeyManagerToken(),
        this.tag
    );
    return feeds.stream().filter(this::isFanoutFeed).findFirst().orElse(null);
  }

  private boolean isFanoutFeed(V5Datafeed d) {
    final String datafeedId = d.getId();
    return datafeedId != null && FANOUT_FEED_PATTERN.matcher(datafeedId).matches();
  }

  @Override
  protected V5EventList readEvents() throws ApiException {
    return this.datafeedApi.readDatafeed(
        this.datafeed.getId(),
        this.authSession.getSessionToken(),
        this.authSession.getKeyManagerToken(),
        new AckId().ackId(this.ackId)
    );
  }

  private void recreateDatafeed() {
    try {
      log.info("Try to delete the stale datafeed");
      this.deleteDatafeed.execute();
      log.info("Recreate a new datafeed and try again");
      this.datafeed = this.createDatafeed.execute();
    } catch (Throwable throwable) {
      throw new NestedRetryException("Recreation of datafeed failed", throwable);
    }
  }

  private Void doDeleteDatafeed() throws ApiException {
    this.datafeedApi.deleteDatafeed(datafeed.getId(), authSession.getSessionToken(), authSession.getKeyManagerToken());
    this.datafeed = null;
    return null;
  }
}
