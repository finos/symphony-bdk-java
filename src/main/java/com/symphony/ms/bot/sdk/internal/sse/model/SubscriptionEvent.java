package com.symphony.ms.bot.sdk.internal.sse.model;

import java.util.List;
import java.util.Map;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import lombok.Getter;

@Getter
public class SubscriptionEvent {

  private List<String> eventTypes;
  private Map<String, String> metadata;
  private String lastEventId;
  private String userId;

  public SubscriptionEvent(SseSubscriber subscriber) {
    this.eventTypes = subscriber.getEventTypes();
    this.metadata = subscriber.getMetadata();
    this.lastEventId = subscriber.getLastEventId();
    this.userId = subscriber.getUserId();
  }

}
