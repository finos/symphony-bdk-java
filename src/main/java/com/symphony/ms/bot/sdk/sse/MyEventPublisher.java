package com.symphony.ms.bot.sdk.sse;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.sse.model.SubscriptionEvent;

/**
 * Sample code. Simple SsePublisher which sends events every second to client application.
 */
public class MyEventPublisher extends SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyEventPublisher.class);
  private static final long WAIT_INTERVAL = 1000L;

  private boolean running = false;
  private List<SubscriptionEvent> subscribers;

  @Override
  public void init() {
    subscribers = new ArrayList<>();
  }

  @Override
  public List<String> getEventTypes() {
    return Stream.of("event1", "event2")
        .collect(Collectors.toList());
  }

  @Override
  protected void handleEvent(SseSubscriber subscriber, SseEvent event) {
    // For simplicity, just send the event to the client application. In real
    // scenarios you could rely on subscriber.getMetadata to check if client is
    // really interested in this particular event.
    subscriber.sendEvent(event);
  }

  @Override
  protected void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent) {
    subscribers.add(subscriberAddedEvent);

    // Start simulating event generation on first subscription
    if (!running) {
      running = true;
      simulateEvent();
    }
  }

  @Override
  protected void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent) {
    subscribers = subscribers.stream()
        .filter(sub -> sub.getUserId() != subscriberRemovedEvent.getUserId())
        .collect(Collectors.toList());

    // Stop simulation if no more subscriber
    if (subscribers.isEmpty()) {
      running = false;
    }
  }

  private void simulateEvent() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      int id = 0;
      while (running) {
        id++;

        // Simulate event alternation
        String eventType = (id % 2) != 0 ? "event1" : "event2";

        // Build sse event
        SseEvent event = SseEvent.builder()
            .event(eventType)
            .data("SSE Test Event - " + LocalTime.now().toString())
            .id(String.valueOf(id))
            .build();

        // Publish event
        LOGGER.debug("Sending event with id {}", event.getId());
        this.publishEvent(event);

        waitForEvents(WAIT_INTERVAL);
      }
    });

  }

  private void waitForEvents(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ie) {
      LOGGER.debug("Error waiting for next events");
    }
  }

}
