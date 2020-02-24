package com.symphony.ms.bot.sdk.internal.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SsePublisherRouterImpl implements SsePublisherRouter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisherRouterImpl.class);

  private List<SsePublisher<?>> ssePublishers;

  public SsePublisherRouterImpl() {
    this.ssePublishers = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void register(SsePublisher<?> ssePublisher) {
    LOGGER.info("Registering sse publisher for event type: {}",
        ssePublisher.getEventTypes());
    ssePublishers.add(ssePublisher);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SsePublisher<?>> findPublishers(List<String> eventTypes) {
    List<SsePublisher<?>> publishers = new ArrayList<>();
    ssePublishers.stream().forEach(pub -> {
      if (!Collections.disjoint(
          pub.getEventTypes(), eventTypes)) {
        publishers.add(pub);
      }
    });

    return publishers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Async("sseTaskExecutor")
  public void bind(SseSubscriber subscriber, List<SsePublisher<?>> publishers) {
    LOGGER.debug("Binding subscriber to corresponding publishers");
    subscriber.bindPublishers(publishers);
    subscriber.startListening();
  }
}
