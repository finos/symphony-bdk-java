package com.symphony.ms.bot.sdk.sse;

import java.time.LocalTime;
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
import com.symphony.ms.bot.sdk.internal.sse.model.SsePublishable;
import com.symphony.ms.bot.sdk.internal.sse.model.SubscriptionEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sample code. Simple SsePublisher which sends events every second to client application.
 */
public class MyEventPublisher extends SsePublisher<MyEventPublisher.SimpleEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyEventPublisher.class);
  private static final long WAIT_INTERVAL = 1000L;

  private boolean running = false;
  private int subscribers;

  @Override
  public List<String> getEventTypes() {
    return Stream.of("event1", "event2")
        .collect(Collectors.toList());
  }

  @Override
  protected void handleEvent(SseSubscriber subscriber, SimpleEvent event) {
    // For simplicity, just send the event to the client application. In real
    // scenarios you could rely on event properties to check if client is
    // really interested in this particular event.

    // Build sse event
    SseEvent sseEvent = SseEvent.builder()
        .event(event.getType())
        .data(event.getPayload())
        .id(event.getId())
        .build();

    LOGGER.debug("Sending event with id {}", sseEvent.getId());
    subscriber.sendEvent(sseEvent);
  }

  @Override
  protected void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent) {
    subscribers++;

    // Start simulating event generation on first subscription
    if (!running) {
      running = true;
      simulateEvent();
    }
  }

  @Override
  protected void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent) {
    subscribers--;

    // Stop simulation if no more subscriber
    if (subscribers == 0) {
      running = false;
    }
  }

  private void simulateEvent() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      int id = 0;
      while (running) {
        id++;

        // Create sse publishable event with payload
        SimpleEvent event = new SimpleEvent();
        event.setId(Integer.toString(id));
        event.setPayload("SSE Test Event - " + LocalTime.now().toString());

        // Simulate event alternation
        event.setType((id % 2) != 0 ? "event1" : "event2");

        // Publish event
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

  @Data
  @NoArgsConstructor
  public class SimpleEvent implements SsePublishable {
    private String payload;
    private String type;
    private String id;
  }

}
