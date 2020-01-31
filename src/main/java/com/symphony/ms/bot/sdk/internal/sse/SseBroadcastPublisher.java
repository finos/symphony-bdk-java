package com.symphony.ms.bot.sdk.internal.sse;

import com.symphony.ms.bot.sdk.internal.sse.model.SseBroadcastDataWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for Server-sent events broadcast publishers
 *
 * @author Gabriel Berberian
 */
public abstract class SseBroadcastPublisher extends SsePublisher {

  private final List<Queue<SseBroadcastDataWrapper>> queues;
  private final AtomicLong eventId;

  protected SseBroadcastPublisher() {
    this.queues = new ArrayList<>();
    this.eventId = new AtomicLong(0);
  }

  @Override
  public void stream(SseSubscriber subscriber) {
    Queue<SseBroadcastDataWrapper> queue = registerQueue();
    stream(subscriber, queue);
    removeQueue(queue);
  }

  /**
   * Broadcasts a data to the publisher queues
   *
   * @param data
   */
  public void broadcast(Object data) {
    SseBroadcastDataWrapper wrappedData =
        new SseBroadcastDataWrapper(data, eventId.getAndIncrement());
    queues.forEach(queue -> queue.add(wrappedData));
  }

  /**
   * Starts streaming events to the specified {@link SseSubscriber}
   *
   * @param subscriber
   * @param queue
   */
  public abstract void stream(SseSubscriber subscriber, Queue<SseBroadcastDataWrapper> queue);

  private Queue<SseBroadcastDataWrapper> registerQueue() {
    Queue<SseBroadcastDataWrapper> queue = new LinkedList<>();
    queues.add(queue);
    return queue;
  }

  private void removeQueue(Queue<SseBroadcastDataWrapper> queue) {
    queues.remove(queue);
  }

}
