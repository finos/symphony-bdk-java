package com.symphony.ms.bot.sdk.sse;

import com.symphony.ms.bot.sdk.internal.sse.SsePublishEventException;
import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.sse.model.SsePublisherQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample code. Simple SseBroadcastPublisher, which waits for incoming data to sends events to the
 * client application.
 *
 * @author Gabriel Berberian
 */
public class SpreadsheetPublisher extends SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetPublisher.class);
  private static final long WAIT_INTERVAL = 1000L;

  @Override
  public String getEventType() {
    return "spreadsheet";
  }

  @Override
  public void stream(SseSubscriber subscriber, SsePublisherQueue queue) {
    while (!subscriber.isCompleted()) {
      while (!queue.isEmpty()) {
        SseEvent event = queue.poll();
        LOGGER.debug("sending event with id {}", event.getId());
        try {
          subscriber.onEvent(event);
        } catch (SsePublishEventException spee) {
          LOGGER.warn("Failed to deliver event with ID: " + event.getId());
        }
      }
      waitForEvents(WAIT_INTERVAL);
    }
  }

  private void waitForEvents(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ie) {
      LOGGER.debug("Error waiting for next events");
    }
  }

}
