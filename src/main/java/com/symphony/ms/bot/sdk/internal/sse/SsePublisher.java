package com.symphony.ms.bot.sdk.internal.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base class for Server-sent events publishers. Provides mechanisms to automatically register child
 * classes to {@link SsePublisherRouter}.
 *
 * @author Marcus Secato
 */
public abstract class SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisher.class);

  private SsePublisherRouter ssePublisherRouter;

  /**
   * Registers the SsePublisher to the {@link SsePublisherRouter} to be notified when new {@link
   * SseSubscriber} requests arrive.
   */
  public void register() {
    init();
    this.ssePublisherRouter.register(this);
  }

  /**
   * Initializes the SsePublisher dependencies. This method can be overridden by the child classes
   * if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * Binds the specified {@link SseSubscriber} to this instance of SsePublisher.
   *
   * @param subscriber
   */
  public void subscribe(SseSubscriber subscriber) {
    try {
      stream(subscriber);
    } catch (Exception e) {
      LOGGER.error("Error streaming SSE events to user {}",
          subscriber.getUserId(), e);

      subscriber.onError(e);
    }
    subscriber.onComplete();
  }

  /**
   * Lists the streams that this instance of SsePublisher handles. Client applications must specify
   * the streams they want to subscribe to in the request path.
   *
   * @return list of stream names
   */
  public abstract List<String> getStreams();

  /**
   * Starts streaming events to the specified {@link SseSubscriber}
   *
   * @param subscriber
   */
  public abstract void stream(SseSubscriber subscriber);

  public void setSsePublisherRouter(SsePublisherRouter ssePublisherRouter) {
    this.ssePublisherRouter = ssePublisherRouter;
  }

}
