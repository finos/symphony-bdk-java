package com.symphony.ms.bot.sdk.sse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.SseSubscriber;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

/**
 * Sample code. Simple SsePublisher which waits for spreadsheet update events to
 * send to the clients.
 *
 * @author Gabriel Berberian
 */
public class SpreadsheetPublisher extends SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetPublisher.class);

  @Override
  public List<String> getEventTypes() {
    return Stream.of("spreadsheetUpdateEvent")
        .collect(Collectors.toList());
  }

  @Override
  public void handleEvent(SseSubscriber subscriber, SseEvent event) {
    Map<String, String> eventMetadata = event.getMetadata();
    String streamId = subscriber.getMetadata().get("streamId");
    if (streamId == null || eventMetadata.get("streamId").equals(streamId)) {
      LOGGER.debug("Sending updates to user {}", subscriber.getUserId());
      subscriber.sendEvent(event);
    }
  }

}
