package com.symphony.bdk.bot.sdk.sse;

import java.util.List;

/**
 * Routes {@link SseSubscriber} to the corresponding {@link SsePublisher} based on the event type
 * specified by subscriber.
 *
 * @author Marcus Secato
 */
public interface SsePublisherRouter {

  /**
   * Registers a {@link SsePublisher} to the router
   *
   * @param ssePublisher the SSE publisher
   */
  void register(SsePublisher<?> ssePublisher);

  /**
   * Finds {@link SsePublisher} based on the specified event types
   *
   * @param eventTypes the event types to look for
   * @return list of publishers
   */
  List<SsePublisher<?>> findPublishers(List<String> eventTypes);

  /**
   * Binds a {@link SseSubscriber} to a {@link SsePublisher}s
   *
   * @param subscriber the SSE subscriber
   * @param publishers the list of SSE publishers
   */
  void bind(SseSubscriber subscriber, List<SsePublisher<?>> publishers);
}
