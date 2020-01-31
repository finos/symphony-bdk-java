package com.symphony.ms.bot.sdk.sse;

import com.symphony.ms.bot.sdk.internal.sse.SseBroadcastPublisher;
import com.symphony.ms.bot.sdk.internal.sse.SsePublishEventException;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import com.symphony.ms.bot.sdk.internal.sse.model.SseBroadcastDataWrapper;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sample code. Simple SseBroadcastPublisher, which waits for incoming data to sends events to the
 * client application.
 *
 * @author Gabriel Berberian
 */
public class SpreadsheetPublisher extends SseBroadcastPublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetPublisher.class);
  private static final long WAIT_INTERVAL = 1000L;
  private static final String SPREADSHEET_UPDATE_EVENT = "spreadSheetUpdateEvent";
  private static final String SPREADSHEET_RESET_EVENT = "spreadSheetRestEvent";

  @Override
  public List<String> getStreams() {
    return Stream.of("spreadsheet").collect(Collectors.toList());
  }

  @Override
  public void stream(SseSubscriber subscriber, Queue<SseBroadcastDataWrapper> queue) {
    while (!subscriber.isCompleted()) {
      while (!queue.isEmpty()) {
        SseBroadcastDataWrapper wrappedData = queue.poll();
        SseEvent event = buildEvent(wrappedData);
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

  private SseEvent buildEvent(SseBroadcastDataWrapper wrappedData) {
    Object data = wrappedData.getData();
    long eventId = wrappedData.getEventId();
    if (data instanceof SpreadsheetCell) {
      return buildUpdateEvent((SpreadsheetCell) data, eventId);
    }
    return buildResetEvent((String[][]) data, eventId);
  }

  private SseEvent buildUpdateEvent(SpreadsheetCell cell, long eventId) {
    return SseEvent.builder()
        .id(Long.toString(eventId))
        .data(cell)
        .name(SPREADSHEET_UPDATE_EVENT)
        .retry(WAIT_INTERVAL)
        .build();
  }

  private SseEvent buildResetEvent(String[][] newSpreadsheet, long eventId) {
    return SseEvent.builder()
        .id(Long.toString(eventId))
        .data(newSpreadsheet)
        .name(SPREADSHEET_RESET_EVENT)
        .retry(WAIT_INTERVAL)
        .build();
  }

  private void waitForEvents(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ie) {
      LOGGER.debug("Error waiting for next events");
    }
  }

}
