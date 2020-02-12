package com.symphony.ms.bot.sdk.internal.sse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Represents a client application subscribing for real-time events
 *
 * @author Marcus Secato
 */
@Getter
public class SseSubscriber {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseSubscriber.class);
  private static final String COMPLETION_EVENT = "_publisher_completion";
  private static final String COMPLETION_WITH_ERROR_EVENT = "_publisher_completion_error";

  private List<String> eventTypes;
  private Map<String, String> metadata;
  private String lastEventId;
  private String userId;

  @Getter(AccessLevel.NONE)
  private SseEmitter sseEmitter;
  @Getter(AccessLevel.NONE)
  private List<SsePublisher> publishers;
  @Getter(AccessLevel.NONE)
  private BlockingQueue<SseEvent> eventQueue;
  @Getter(AccessLevel.NONE)
  private Throwable lastPublisherError;
  @Getter(AccessLevel.NONE)
  private boolean listening = false;

  public SseSubscriber(SseEmitter sseEmitter, List<String> eventTypes,
      Map<String, String> metadata, String lastEventId, String userId, int queueCapacity) {
    this.sseEmitter = sseEmitter;
    this.eventTypes = eventTypes;
    this.metadata = metadata;
    this.lastEventId = lastEventId;
    this.userId = userId;

    this.eventQueue = new LinkedBlockingQueue<SseEvent>(queueCapacity);
    this.sseEmitter.onCompletion(() -> forceComplete());
  }

  void bindPublishers(List<SsePublisher> publishers) {
    this.publishers = publishers;
    this.publishers.stream().forEach(pub -> pub.addSubscriber(this));
  }

  void startListening() {
    listening = true;
    listen();
  }

  private void listen() {
    while (listening) {
      try {
        SseEvent event = eventQueue.take();
        if (COMPLETION_EVENT.equals(event.getEvent())) {
          sseEmitter.complete();
          terminate();
        } else if (COMPLETION_WITH_ERROR_EVENT.equals(event.getEvent())) {
          sseEmitter.completeWithError(lastPublisherError);
          terminate();
        } else {
          sseEmitter.send(event);
        }
      } catch (Exception e) {
        LOGGER.info("Error handling event for user {}: {}", userId, e.getMessage());
        terminate();
      }
    }
  }

  /**
   * Sends event back to client application. Called by {@link SsePublisher} when new event is
   * available.
   *
   * @param sseEvent
   */
  public void sendEvent(SseEvent sseEvent) {
    try {
      eventQueue.put(sseEvent);
    } catch (InterruptedException ie) {
      LOGGER.debug("Queue interrupted error when adding event");
    }
  }

  /**
   * Notifies subscriber that the given publisher is done sending event
   *
   * @param publisher
   */
  public void complete(SsePublisher publisher) {
    internalComplete(publisher);
  }

  /**
   * Notifies subscriber that the given publisher is done sending event due to error
   *
   * @param publisher
   * @param ex
   */
  public void completeWithError(SsePublisher publisher, Throwable ex) {
    lastPublisherError = ex;
    internalComplete(publisher);
  }

  private void internalComplete(SsePublisher publisher) {
    LOGGER.debug("Handling publisher completion");
    publishers = publishers.stream()
        .filter(pub -> !pub.equals(publisher))
        .collect(Collectors.toList());

    if (publishers.size() == 0) {
      LOGGER.debug("No more publishers. Informing client that server is done.");
      if (lastPublisherError != null) {
        sendEvent(SseEvent.builder()
            .event(COMPLETION_WITH_ERROR_EVENT)
            .build());
      } else {
        sendEvent(SseEvent.builder()
            .event(COMPLETION_EVENT)
            .build());
      }
    }
  }

  private void terminate() {
    LOGGER.debug("Terminating SSE subscription for user {}", userId);
    listening = false;
    publishers.stream().forEach(pub -> pub.removeSubscriber(this));
  }

  private void forceComplete() {
    sendEvent(SseEvent.builder()
        .event(COMPLETION_EVENT)
        .build());
  }
}
