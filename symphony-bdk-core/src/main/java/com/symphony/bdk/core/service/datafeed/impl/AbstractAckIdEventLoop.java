package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.http.api.ApiException;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apiguardian.api.API;

import java.util.concurrent.TimeUnit;

@API(status = API.Status.INTERNAL)
@Slf4j
public abstract class AbstractAckIdEventLoop extends AbstractDatafeedLoop {

  /**
   * Initial ack ID value when starting the loop
   */
  protected static final String INITIAL_ACK_ID = "";

  /**
   * Based on the DFv2 default visibility timeout, after which an event is re-queued.
   */
  private static final int EVENT_PROCESSING_MAX_DURATION_SECONDS = 30;

  @Getter(AccessLevel.PROTECTED)
  protected String ackId;

  public AbstractAckIdEventLoop(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, UserV2 botInfo) {
    super(datafeedApi, authSession, config, botInfo);
    this.ackId = INITIAL_ACK_ID;
  }

  protected Void readAndHandleEvents() throws ApiException {
    V5EventList v5EventList = readEvents();
    try {

      StopWatch stopWatch = StopWatch.createStarted();
      this.handleV4EventList(v5EventList.getEvents());
      stopWatch.stop();

      checkProcessingTime(stopWatch);

      // updates ack id so that on next call DFv2 knows that events have been processed
      this.ackId = v5EventList.getAckId();
    } catch (Exception e) {
      // can happen if developer explicitly raised a RequeueEventException in handleV4EventList
      // we also catch all exceptions just to be extra careful and never break the DF loop
      log.warn("Failed to process events, will not update ack id, events will be re-queued", e);
    }
    return null;
  }

  @Generated // cannot be easily unit tested
  private void checkProcessingTime(StopWatch stopWatch) {
    if (stopWatch.getTime(TimeUnit.SECONDS) > EVENT_PROCESSING_MAX_DURATION_SECONDS) {
      log.warn("Events processing took longer than {} seconds, "
              + "this might lead to events being re-queued in datafeed and re-dispatched."
              + " You might want to consider processing the event in a separated thread if needed.",
          EVENT_PROCESSING_MAX_DURATION_SECONDS);
    }
  }

  protected abstract V5EventList readEvents() throws ApiException;
}
