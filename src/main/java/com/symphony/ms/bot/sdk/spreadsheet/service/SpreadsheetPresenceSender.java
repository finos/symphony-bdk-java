package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Sends presence events
 */
@Builder
public class SpreadsheetPresenceSender {

  @Getter private Long userId;
  @Getter private String streamId;
  private AtomicLong eventId;
  private SseEvent presenceEvent;
  private SsePublisher publisher;
  @Getter private long timeLastEvent;

  /**
   * Sends a presence event
   */
  public void send() {
    this.presenceEvent.setId(Long.toString(eventId.getAndIncrement()));
    publisher.publishEvent(presenceEvent);
    timeLastEvent = System.currentTimeMillis();
  }

}
