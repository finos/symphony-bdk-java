package com.symphony.ms.bot.sdk.internal.sse;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

/**
 * Base class for Server-sent events publishers. Provides mechanisms to automatically register child
 * classes to {@link SsePublisherRouter}.
 *
 * @author Marcus Secato
 */
public abstract class SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisher.class);

  private SsePublisherRouter ssePublisherRouter;
  protected ConcurrentHashMap<String, List<SseSubscriber>> subscribers;
  private boolean removed;

  /**
   * Initializes the SsePublisher dependencies. This method can be overridden by the child classes
   * if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * Registers the SsePublisher to the {@link SsePublisherRouter} to be notified when new {@link
   * SseSubscriber} requests arrive.
   */
  public void register() {
    init();
    this.ssePublisherRouter.register(this);
    subscribers = new ConcurrentHashMap<String, List<SseSubscriber>>();
  }

  /**
   * Binds the specified {@link SseSubscriber} to this instance of SsePublisher.
   *
   * @param subscriber
   */
  void addSubscriber(SseSubscriber subscriber) {
    subscriber.getEventTypes()
        .forEach(evt -> subscribers
            .computeIfAbsent(evt, key -> new LinkedList<>()).add(subscriber));

    onSubscriberAdded(subscriber);
  }

  /**
   * Removes the specified {@link SseSubscriber} from this instance of SsePublisher.
   *
   * @param subscriber
   */
  void removeSubscriber(SseSubscriber subscriber) {
    removed = false;
    subscriber.getEventTypes()
        .forEach(type -> subscribers
            .computeIfPresent(type, (key, subs) -> {
              removed = subs.remove(subscriber);
              return !subs.isEmpty() ? subs : null;
            }));

    if (removed) {
      onSubscriberRemoved(subscriber);
    }
  }

  /**
   * Notifies publisher to send the given event
   *
   * @param event
   */
  public void publishEvent(SseEvent event) {
    subscribers.computeIfPresent(event.getEvent(), (key, subs) -> {
      subs.forEach(sub -> handleEvent(sub, event));
      return subs;
    });
  }

  /**
   * Returns the event types that this instance of SsePublisher handles. Client applications must
   * specify the types they want to subscribe to in the request path.
   *
   * @return the event types list
   */
  public abstract List<String> getEventTypes();

  /**
   * Subscriber started listening. Subscription startup logic (if any) goes here.
   *
   * @param subscriber
   */
  protected void onSubscriberAdded(SseSubscriber subscriber) {
    LOGGER.debug("Subscriber {} added", subscriber.getUserId());
  }

  /**
   * Subscriber stopped listening. Subscription teardown logic (if any) goes here.
   *
   * @param subscriber
   */
  protected void onSubscriberRemoved(SseSubscriber subscriber) {
    LOGGER.debug("Subscriber {} removed", subscriber.getUserId());
  }

  /**
   * Process event targeted to the specified subscriber
   *
   * @param subscriber
   * @param event
   */
  protected abstract void handleEvent(SseSubscriber subscriber, SseEvent event);

  public void setSsePublisherRouter(SsePublisherRouter ssePublisherRouter) {
    this.ssePublisherRouter = ssePublisherRouter;
  }

}
