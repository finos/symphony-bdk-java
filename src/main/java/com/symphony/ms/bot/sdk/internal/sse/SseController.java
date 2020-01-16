package com.symphony.ms.bot.sdk.internal.sse;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Server-sent Event Controller
 * Endpoint that offers all support required for client applications to receive
 * automatic updates from server.
 *
 * @author Marcus Secato
 *
 */
@RestController
// TODO: add /secure/ in path to enforce authentication
@RequestMapping("/events")
public class SseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseController.class);

  private SsePublisherRouter ssePublisherRouter;

  public SseController(SsePublisherRouter ssePublisherRouter) {
    this.ssePublisherRouter = ssePublisherRouter;
  }

  /**
   * SSE endpoint
   * @param streams list of streams that the client application wants to receive events from.
   * @param filterCriteria filtering options for the specified streams
   * @param lastEventId ID of last event client application received
   * @param userId ID of the user subscribing to the specified streams
   * @param response
   *
   * @return the {@link SseEmitter} representing the SSE connection
   */
  @GetMapping(value = "/{streams}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribeSse(@PathVariable List<String> streams,
      @RequestParam Map<String, String> filterCriteria,
      @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId,
      @RequestAttribute(name = "userId", required = false) String userId,
      HttpServletResponse response) {

    SseEmitter emitter = new SseEmitter();
    SseSubscriber subscriber = SseSubscriber.builder()
        .sseEmitter(emitter)
        .streams(streams)
        .userId(userId)
        .filters(filterCriteria)
        .lastEventId(lastEventId)
        .build();

    List<SsePublisher> pubs = ssePublisherRouter.findPublishers(subscriber);
    if (pubs.isEmpty()) {
      LOGGER.info("No SSE publisher found for streams {}",
          subscriber.getStreams());
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    pubs.stream().forEach(pub -> ssePublisherRouter.bind(subscriber, pub));

    return emitter;
  }

}
