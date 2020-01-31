package com.symphony.ms.bot.sdk.sse;

import com.symphony.ms.bot.sdk.internal.sse.SsePublishEventException;
import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sample code. Simple SsePublisher which sends events every second to client application.
 */
public class MyEventPublisher extends SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyEventPublisher.class);

  private static final long WAIT_INTERVAL = 1000L;

  @Override
  public List<String> getStreams() {
    return Stream.of("stream1", "stream2")
        .collect(Collectors.toList());
  }

  @Override
  public void stream(SseSubscriber subscriber) {
    for (int i = 0; true; i++) {
      SseEvent event = SseEvent.builder()
          .name("test_event")
          .data("SSE Test Event - " + LocalTime.now().toString())
          .id(String.valueOf(i))
          .retry(WAIT_INTERVAL)
          .build();
      LOGGER.debug("sending event with id {}", event.getId());

      try {
        subscriber.onEvent(event);
      } catch (SsePublishEventException spee) {
        LOGGER.warn("Failed to deliver event with ID: " + event.getId());
        break;
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
