package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.retry.RetryWithRecovery.networkIssueMessageError;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
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
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apiguardian.api.API;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class DatafeedLoopV2 extends AbstractDatafeedLoop {

  /**
   * DFv2 API authorizes a maximum length for the tag parameter.
   */
  private static final int DATAFEED_TAG_MAX_LENGTH = 100;

  /**
   * Based on the DFv2 default visibility timeout, after which an event is re-queued.
   */
  private static final int EVENT_PROCESSING_MAX_DURATION_SECONDS = 30;

  private final RetryWithRecovery<Void> readDatafeed;
  private final RetryWithRecovery<V5Datafeed> retrieveDatafeed;
  private final RetryWithRecovery<V5Datafeed> createDatafeed;
  private final RetryWithRecovery<Void> deleteDatafeed;
  private final String tag;

  @Getter(AccessLevel.PROTECTED)
  private AckId ackId;

  private V5Datafeed datafeed;

  public DatafeedLoopV2(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, UserV2 botInfo) {
    super(datafeedApi, authSession, config, botInfo);
    this.ackId = new AckId().ackId("");
    this.tag = StringUtils.truncate(bdkConfig.getBot().getUsername(), DATAFEED_TAG_MAX_LENGTH);

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

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() throws ApiException, AuthUnauthorizedException {
    if (this.started.get()) {
      throw new IllegalStateException("The datafeed service is already started");
    }

    if (!DistributedTracingContext.hasTraceId()) {
      DistributedTracingContext.setTraceId();
    }

    try {
      this.datafeed = this.retrieveDatafeed.execute();
      if (this.datafeed == null) {
        this.datafeed = this.createDatafeed.execute();
      }
      log.debug("Start reading events from datafeed {}", this.datafeed.getId());
      this.started.set(true);
      do {

        this.readDatafeed.execute();

      } while (this.started.get());
      log.info("Datafeed loop successfully stopped.");
    } catch (AuthUnauthorizedException | ApiException | NestedRetryException exception) {
      throw exception;
    } catch (Throwable throwable) {
      log.error("{}\n{}", networkIssueMessageError(throwable, datafeedApi.getApiClient().getBasePath()), throwable);
    } finally {
      DistributedTracingContext.clear();
    }
  }

  private V5Datafeed doCreateDatafeed() throws ApiException {
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

    return feeds.stream().findFirst().orElse(null);
  }

  private Void readAndHandleEvents() throws ApiException {
    final V5EventList v5EventList = this.datafeedApi.readDatafeed(
        datafeed.getId(),
        authSession.getSessionToken(),
        authSession.getKeyManagerToken(),
        ackId
    );
    try {

      final StopWatch stopWatch = StopWatch.createStarted();
      this.handleV4EventList(v5EventList.getEvents());
      stopWatch.stop();

      if (stopWatch.getTime(TimeUnit.SECONDS) > EVENT_PROCESSING_MAX_DURATION_SECONDS) {
        log.warn("Events processing took longer than {} seconds, "
                + "this might lead to events being re-queued in datafeed and re-dispatched."
                + " You might want to consider processing the event in a separated thread if needed.",
            EVENT_PROCESSING_MAX_DURATION_SECONDS);
      }

      // updates ack id so that on next call DFv2 knows that events have been processed
      this.ackId = new AckId();
      this.ackId.setAckId(v5EventList.getAckId());

    } catch (Exception e) {
      // can happen if developer explicitly raised a NoAckIdUpdateException in handleV4EventList
      // we also catch all exceptions just to be extra careful and never break the DF loop
      log.warn("Failed to process events, will not update ack id, events will be re-queued", e);
    }
    return null;
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
