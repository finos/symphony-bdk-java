package com.symphony.ms.songwriter.internal.sse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.symphony.ms.songwriter.internal.sse.model.SseEvent;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a client application subscribing for server-sent events
 *
 * @author Marcus Secato
 *
 */
@Getter
@Builder
public class SseSubscriber {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseSubscriber.class);

  private SseEmitter sseEmitter;

  private String userId;

  private Map<String, String> filters;

  private List<String> streams;

  private String lastEventId;

  /**
   * Sends events back to client application. Called by {@link SsePublisher}
   * when new event is available.
   *
   * @param sseEvent
   * @throws SsePublishEventException
   */
  public void onEvent(SseEvent sseEvent) throws SsePublishEventException {
    try {
      sseEmitter.send(sseEvent);
    } catch (IOException ioe) {
      LOGGER.error("Failed to publish SSE event to user {}", userId);
      throw new SsePublishEventException();
    }
  }

  /**
   * Informs client application that server is done publishing events
   */
  public void onComplete() {
    sseEmitter.complete();
  }

  /**
   * Informs client application that event streaming is done due to error
   * @param ex the error
   */
  public void onError(Throwable ex) {
    sseEmitter.completeWithError(ex);
  }

}
