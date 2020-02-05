package com.symphony.ms.bot.sdk.internal.sse;

import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Represents a client application subscribing for server-sent events
 *
 * @author Marcus Secato
 */
@Getter
public class SseSubscriber {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseSubscriber.class);

  private SseEmitter sseEmitter;
  private String userId;
  private Map<String, String> filters;
  private String eventType;
  private String lastEventId;
  private boolean completed;

  public SseSubscriber(SseEmitter sseEmitter, String userId, Map<String, String> filters,
      String eventType, String lastEventId) {
    this.sseEmitter = sseEmitter;
    this.userId = userId;
    this.filters = filters;
    this.eventType = eventType;
    this.lastEventId = lastEventId;
    this.completed = false;

    this.sseEmitter.onCompletion(() -> completed = true);
    this.sseEmitter.onError((e) -> completed = true);
    this.sseEmitter.onTimeout(() -> completed = true);
  }

  /**
   * Sends events back to client application. Called by {@link SsePublisher} when new event is
   * available.
   *
   * @param sseEvent
   * @throws SsePublishEventException
   */
  public void onEvent(SseEvent sseEvent) throws SsePublishEventException {
    try {
      sseEmitter.send(sseEvent);
    } catch (IllegalStateException ise) {
      LOGGER.debug("Tried to send SSE event but subscriber is already completed");
      completed = true;
      throw new SsePublishEventException();
    } catch (Exception e) {
      LOGGER.warn("Error sending SSE event to user {}\n{}", userId, e.getMessage());
      throw new SsePublishEventException();
    }
  }

  /**
   * Informs client application that server is done publishing events
   */
  public void onComplete() {
    try {
      sseEmitter.complete();
    } catch (Exception e) {
      LOGGER.debug("Tried to signalize streaming complete but connection closed");
    }
    completed = true;
  }

  /**
   * Informs client application that event streaming is done due to error
   *
   * @param ex the error
   */
  public void onError(Throwable ex) {
    try {
      sseEmitter.completeWithError(ex);
    } catch (Exception e) {
      LOGGER.debug("Tried to signalize streaming complete with error but connection closed");
    }
    completed = true;
  }

}
