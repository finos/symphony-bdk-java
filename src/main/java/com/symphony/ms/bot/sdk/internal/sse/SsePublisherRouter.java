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
   * Finds {@link SsePublisher} based on the event type specified by {@link SseSubscriber}
   *
   * @param sseSubscriber
   * @return list of publishers
   */
  List<SsePublisher> findPublishers(SseSubscriber sseSubscriber);

  /**
   * Binds a {@link SseSubscriber} to a {@link SsePublisher} starting the event streams.
   *
   * @param subscriber
   * @param publisher
   * @param streams
   */
  void bind(SseSubscriber subscriber, SsePublisher publisher, List<String> streams);

}
