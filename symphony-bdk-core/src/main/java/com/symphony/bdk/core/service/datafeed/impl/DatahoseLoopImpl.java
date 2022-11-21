package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.datafeed.DatahoseLoop;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.gen.api.model.V5EventsReadBody;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.List;

@Slf4j
@API(status = API.Status.INTERNAL)
public class DatahoseLoopImpl extends AbstractAckIdEventLoop implements DatahoseLoop {

  /**
   * DFv2 API authorizes a maximum length for the tag parameter.
   */
  private static final int DATAHOSE_TAG_MAX_LENGTH = 80;
  private static final String DATAHOSE = "datahose";
  private final String tag;
  private final List<String> filters;
  private final RetryWithRecovery<Object> readEvents;

  public DatahoseLoopImpl(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, UserV2 botInfo) {
    super(datafeedApi, authSession, config, botInfo);

    String untruncatedTag = config.getDatahose().getTag();
    if (StringUtils.isEmpty(untruncatedTag)) {
      untruncatedTag = DATAHOSE + "-" + botInfo.getUsername();
    }
    this.tag = StringUtils.truncate(untruncatedTag, DATAHOSE_TAG_MAX_LENGTH);

    this.filters = config.getDatahose().getEventTypes();

    this.readEvents = new RetryWithRecoveryBuilder<>()
        .basePath(datafeedApi.getApiClient().getBasePath())
        .retryConfig(config.getDatahose().getRetry())
        .name("readEvents")
        .supplier(this::readAndHandleEvents)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkIssueOrMinorError)
        .recoveryStrategy(ApiException::isUnauthorized, this::refresh)
        .build();
  }

  @Override
  protected void runLoop() throws Throwable {
    log.info("Start reading events from datahose loop");
    this.started.set(true);

    do {
      this.readEvents.execute();
    } while (this.started.get());

    log.info("Datahose loop successfully stopped.");
  }

  @Override
  protected V5EventList readEvents() throws ApiException {
    return this.datafeedApi.readEvents(this.authSession.getSessionToken(), this.authSession.getKeyManagerToken(),
        new V5EventsReadBody().ackId(this.ackId).eventTypes(this.filters).tag(this.tag).type(DATAHOSE));
  }
}
