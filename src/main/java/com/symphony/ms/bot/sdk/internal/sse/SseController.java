package com.symphony.ms.bot.sdk.internal.sse;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.symphony.ms.bot.sdk.internal.sse.config.SseSubscriberProps;
import com.symphony.ms.bot.sdk.internal.symphony.ConfigClient;

/**
 * Server-sent Event Controller Endpoint that offers all support required for client applications to
 * receive real-time updates from server.
 *
 * @author Marcus Secato
 */
@RestController
public class SseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseController.class);
  private static final String SSE_PATH = "events/{eventTypes}";

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  private RequestMappingHandlerMapping handlerMapping;

  private final SseSubscriberProps subscriberConfig;
  private SsePublisherRouter ssePublisherRouter;
  private String authPath;

  public SseController(SsePublisherRouter ssePublisherRouter,
      ConfigClient configClient, SseSubscriberProps subscriberConfig) {
    this.ssePublisherRouter = ssePublisherRouter;
    this.authPath = configClient.getExtAppAuthPath();
    this.subscriberConfig = subscriberConfig;
  }

  @PostConstruct
  public void init() throws NoSuchMethodException {
    registerRoute(authPath.concat(SSE_PATH));
  }

  /**
   * SSE endpoint
   *
   * @param eventTypes  the event types that the client wants to listen to
   * @param metadata    metadata to be sent to publishers (e.g. filtering criteria)
   * @param lastEventId ID of last event client application received
   * @param userId      ID of the user subscribing to the specified category
   * @param response    the request response
   * @return the {@link SseEmitter} representing the SSE connection
   */
  public SseEmitter subscribeSse(@PathVariable List<String> eventTypes,
      @RequestParam Map<String, String> metadata,
      @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId,
      @RequestAttribute(name = "userId", required = false) String userId,
      HttpServletResponse response) {

    List<SsePublisher<?>> publishers = ssePublisherRouter.findPublishers(eventTypes);

    if (publishers.isEmpty()) {
      LOGGER.info("No SSE publisher found for event types {}", eventTypes);
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    SseEmitter emitter = new SseEmitter();
    SseSubscriber subscriber = new SseSubscriber(emitter, eventTypes,
        metadata, lastEventId, parseUserId(userId), subscriberConfig);

    try {
      ssePublisherRouter.bind(subscriber, publishers);
    } catch (TaskRejectedException tre) {
      LOGGER.info("Rejecting subscription. No more threads in SSE pool.");
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    return emitter;
  }

  private Long parseUserId(String userId) {
    Long uid = null;
    if (userId != null) {
      try {
        uid = new Long(userId);
      } catch (NumberFormatException nfe) {
        LOGGER.info("Rejecting subscription. Failed to parse user id {}", userId);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }
    return uid;
  }

  private void registerRoute(String route) throws NoSuchMethodException {
    handlerMapping.registerMapping(RequestMappingInfo.paths(route)
            .methods(RequestMethod.GET)
            .produces(MediaType.TEXT_EVENT_STREAM_VALUE).build(),
        this,
        SseController.class.getDeclaredMethod("subscribeSse",
            List.class,
            Map.class,
            String.class,
            String.class,
            HttpServletResponse.class));
  }

}
