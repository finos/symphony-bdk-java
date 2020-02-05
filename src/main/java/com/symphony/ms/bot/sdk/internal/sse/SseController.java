package com.symphony.ms.bot.sdk.internal.sse;

import com.symphony.ms.bot.sdk.internal.symphony.ConfigClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

/**
 * Server-sent Event Controller Endpoint that offers all support required for client applications to
 * receive automatic updates from server.
 *
 * @author msecato
 */
@RestController
public class SseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseController.class);

  private static final String[] SSE_PATHS =
      {"v1/events/{eventType}", "v1/events/{eventType}/{streams}"};

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  private RequestMappingHandlerMapping handlerMapping;

  private SsePublisherRouter ssePublisherRouter;
  private String authPath;

  public SseController(SsePublisherRouter ssePublisherRouter, ConfigClient configClient) {
    this.ssePublisherRouter = ssePublisherRouter;
    this.authPath = configClient.getExtAppAuthPath();
  }

  @PostConstruct
  public void init() throws NoSuchMethodException {
    registerRoutes(SSE_PATHS);
//    registerRoute(authPath.concat(SSE_PATH));
  }

  /**
   * SSE endpoint
   *
   * @param eventType      type of event that the client application wants to listen to
   * @param streams        the list of streams that the client application wants to receive events
   *                       from
   * @param filterCriteria filtering options for the specified streams
   * @param lastEventId    ID of last event client application received
   * @param userId         ID of the user subscribing to the specified streams
   * @param response       the request response
   * @return the {@link SseEmitter} representing the SSE connection
   */
  public SseEmitter subscribeSse(@PathVariable String eventType,
      @PathVariable(required = false) List<String> streams,
      @RequestParam Map<String, String> filterCriteria,
      @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId,
      @RequestAttribute(name = "userId", required = false) String userId,
      HttpServletResponse response) {

    SseEmitter emitter = new SseEmitter();
    SseSubscriber subscriber =
        new SseSubscriber(emitter, userId, filterCriteria, eventType, lastEventId);

    List<SsePublisher> pubs = ssePublisherRouter.findPublishers(subscriber);
    if (pubs.isEmpty()) {
      LOGGER.info("No SSE publisher found for event type {}", subscriber.getEventType());
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    pubs.stream().forEach(pub -> ssePublisherRouter.bind(subscriber, pub, streams));

    return emitter;
  }

  private void registerRoutes(String... routes) throws NoSuchMethodException {
    handlerMapping.registerMapping(RequestMappingInfo.paths(routes)
            .methods(RequestMethod.GET)
            .produces(MediaType.TEXT_EVENT_STREAM_VALUE).build(),
        this,
        SseController.class.getDeclaredMethod("subscribeSse",
            String.class,
            List.class,
            Map.class,
            String.class,
            String.class,
            HttpServletResponse.class));
  }

}
