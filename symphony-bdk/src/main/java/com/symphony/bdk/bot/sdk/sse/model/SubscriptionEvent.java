package com.symphony.bdk.bot.sdk.sse.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;

import com.symphony.bdk.bot.sdk.sse.SseSubscriber;

/**
 * SSE subscription event
 */
@Getter
public class SubscriptionEvent {

  private List<String> eventTypes;
  private Map<String, String> metadata;
  private String lastEventId;
  private Long userId;

  public SubscriptionEvent(SseSubscriber subscriber) {
    this.eventTypes = subscriber.getEventTypes();
    this.metadata = subscriber.getMetadata();
    this.lastEventId = subscriber.getLastEventId();
    this.userId = subscriber.getUserId();
  }

}
