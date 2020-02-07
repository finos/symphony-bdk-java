package com.symphony.ms.bot.sdk.internal.sse;

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
   * @param ssePublisher
   */
  void register(SsePublisher ssePublisher);

  /**
   * Finds {@link SsePublisher} based on the specified event types
   *
   * @param eventTypes
   * @return list of publishers
   */
  List<SsePublisher> findPublishers(List<String> eventTypes);

  /**
   * Binds a {@link SseSubscriber} to a {@link SsePublisher}s
   *
   * @param subscriber
   * @param publishers
   */
  void bind(SseSubscriber subscriber, List<SsePublisher> publishers);
}
