package com.symphony.ms.bot.sdk.internal.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SsePublisherRouterImpl implements SsePublisherRouter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisherRouterImpl.class);

  private List<SsePublisher> ssePublishers;

  public SsePublisherRouterImpl() {
    this.ssePublishers = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void register(SsePublisher ssePublisher) {
    LOGGER.info("Registering sse publisher for event type: {}",
        ssePublisher.getEventType());
    ssePublishers.add(ssePublisher);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SsePublisher> findPublishers(SseSubscriber sseSubscriber) {
    return ssePublishers.stream()
        .filter(pub -> pub.getEventType().equals(sseSubscriber.getEventType()))
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Async("sseTaskExecutor")
  public void bind(SseSubscriber subscriber, SsePublisher publisher, List<String> streams) {
    publisher.subscribe(subscriber, streams);
  }

}
