package com.symphony.ms.songwriter.internal.sse;

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

  private List<SsePublisher> ssePublishers;

  public SsePublisherRouterImpl () {
    this.ssePublishers = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void register(SsePublisher ssePublisher) {
    LOGGER.info("Registering sse publisher for streams: {}",
        ssePublisher.getStreams());
    ssePublishers.add(ssePublisher);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SsePublisher> findPublishers(SseSubscriber sseSubscriber) {
    List<SsePublisher> publishers = new ArrayList<>();
    ssePublishers.stream().forEach(pub -> {
      if (!Collections.disjoint(
          pub.getStreams(), sseSubscriber.getStreams())) {
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
  public void bind(SseSubscriber subscriber, SsePublisher publisher) {
    publisher.subscribe(subscriber);
  }

}
