package com.symphony.ms.bot.sdk.internal.sse;

import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.sse.model.SsePublisherQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
  private List<SsePublisherQueue> queues;

  public SsePublisher() {
    this.queues = new ArrayList<>();
  }

  /**
   * Initializes the SsePublisher dependencies. This method can be overridden by the child classes
   * if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * Gets the event type that this instance of SsePublisher handles. Client applications must
   * specify the event type they want to subscribe to in the request path.
   *
   * @return the event type name
   */
  public abstract String getEventType();

  /**
   * Starts streaming events to the specified {@link SseSubscriber}
   *
   * @param subscriber
   * @param queue
   */
  public abstract void stream(SseSubscriber subscriber, SsePublisherQueue queue);

  /**
   * Registers the SsePublisher to the {@link SsePublisherRouter} to be notified when new {@link
   * SseSubscriber} requests arrive.
   */
  public void register() {
    init();
    this.ssePublisherRouter.register(this);
  }

  /**
   * Binds the specified {@link SseSubscriber} to this instance of SsePublisher.
   *
   * @param subscriber
   */
  public void subscribe(SseSubscriber subscriber, List<String> streams) {
    try {
      stream(subscriber, streams);
    } catch (Exception e) {
      LOGGER.error("Error streaming SSE events to user {}",
          subscriber.getUserId(), e);

      subscriber.onError(e);
    }
    subscriber.onComplete();
  }

  public void stream(SseSubscriber subscriber, List<String> streams) {
    SsePublisherQueue queue = registerQueue(streams);
    stream(subscriber, queue);
    removeQueue(queue);
  }

  /**
   * Broadcasts a data to all publisher queues
   *
   * @param event the event to be broadcast
   */
  public void broadcast(SseEvent event) {
    queues.forEach(queue -> queue.addEvent(event));
  }

  /**
   * Broadcasts a data to the queues that relates with a specific stream
   *
   * @param event  the event to be broadcast
   * @param stream the stream that the event relates to
   */
  public void broadcast(SseEvent event, String stream) {
    if (stream == null) {
      broadcast(event);
    } else {
      queues.forEach(queue -> {
        if (queue.hasStream(stream)) {
          queue.addEvent(event);
        }
      });
    }
  }

  public void setSsePublisherRouter(SsePublisherRouter ssePublisherRouter) {
    this.ssePublisherRouter = ssePublisherRouter;
  }

  private SsePublisherQueue registerQueue(List<String> streams) {
    SsePublisherQueue queue = new SsePublisherQueue(streams);
    queues.add(queue);
    return queue;
  }

  private void removeQueue(SsePublisherQueue queue) {
    queues.remove(queue);
  }

}
