package com.symphony.ms.songwriter.sse;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.songwriter.internal.sse.SsePublisher;
import com.symphony.ms.songwriter.internal.sse.SseSubscriber;
import com.symphony.ms.songwriter.internal.sse.model.SseEvent;

/**
 * Sample code. Simple SsePublisher which sends events every second to client
 * application.
 *
 */
public class MyEventPublisher extends SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyEventPublisher.class);

  @Override
  public List<String> getStreams() {
    return Stream.of("stream1", "stream2")
        .collect(Collectors.toList());
  }

  @Override
  public void stream(SseSubscriber subscriber) {
    try {
      for (int i = 0; true; i++) {
        SseEvent event = SseEvent.builder()
            .name("test_event")
            .data("SSE Test Event - " + LocalTime.now().toString())
            .id(String.valueOf(i))
            .retry(10000)
            .build();
        LOGGER.debug("sending event with id {}", event.getId());
        subscriber.onEvent(event);
        Thread.sleep(1000);
      }
    } catch (Exception e) {
      LOGGER.info("Connection closed");
      subscriber.onError(e);
    }
  }

}
