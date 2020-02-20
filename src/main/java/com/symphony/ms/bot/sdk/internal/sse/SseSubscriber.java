package com.symphony.ms.bot.sdk.internal.sse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import com.symphony.ms.bot.sdk.internal.sse.config.SseSubscriberProps;
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
  private static final String KEEPALIVE_EVENT = "keep-alive";

  private List<String> eventTypes;
  private Map<String, String> metadata;
  private String lastEventId;
  private Long userId;

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
  @Getter(AccessLevel.NONE)
  private SseSubscriberProps subscriberConfig;

  public SseSubscriber(SseEmitter sseEmitter, List<String> eventTypes, Map<String, String> metadata,
      String lastEventId, Long userId, SseSubscriberProps subscriberConfig) {
    this.sseEmitter = sseEmitter;
    this.eventTypes = eventTypes;
    this.metadata = metadata;
    this.lastEventId = lastEventId;
    this.userId = userId;
    this.subscriberConfig = subscriberConfig;

    this.eventQueue = new LinkedBlockingQueue<>(
        subscriberConfig.getQueueCapacity());
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
        SseEvent event = eventQueue.poll(
            subscriberConfig.getQueueTimeout(), TimeUnit.MILLISECONDS);

        if (event == null) {
          sseEmitter.send(SseEmitter.event()
              .name(KEEPALIVE_EVENT));
        } else {
          switch (event.getEvent()) {
            case COMPLETION_EVENT:
              sseEmitter.complete();
              terminate();
              break;
            case COMPLETION_WITH_ERROR_EVENT:
              sseEmitter.completeWithError(lastPublisherError);
              terminate();
              break;
            default:
              sseEmitter.send(buildEvent(event));
          }
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

  private SseEventBuilder buildEvent(SseEvent event) {
    SseEventBuilder builder = SseEmitter.event();
    if (event.getId() != null) {
      builder.id(event.getId());
    }

    if (event.getEvent() != null) {
      builder.name(event.getEvent());
    }

    if (event.getData() != null) {
      builder.data(event.getData());
    }

    if (event.getRetry() != null) {
      builder.reconnectTime(event.getRetry());
    }

    return builder;
  }
}
