package com.symphony.ms.bot.sdk.internal.sse;

import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.sse.model.SubscriptionEvent;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Base class for Server-sent events publishers. Provides mechanisms to automatically register child
 * classes to {@link SsePublisherRouter}.
 *
 * @author Marcus Secato
 */
public abstract class SsePublisher {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisher.class);

  @Setter
  private SsePublisherRouter ssePublisherRouter;
  private ConcurrentHashMap<String, List<SseSubscriber>> subscribers;
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
    this.subscribers = new ConcurrentHashMap<>();
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
    onSubscriberAdded(new SubscriptionEvent(subscriber));
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
      onSubscriberRemoved(new SubscriptionEvent(subscriber));
    }
  }

  /**
   * Publishes events to subscribers
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
   * Notifies all subscribers there are no more events to be sent
   */
  public void complete() {
    subscribers.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toSet())
        .forEach(sub -> sub.complete(this));
  }

  /**
   * Notifies all subscribers there are no more events to be sent due to error
   *
   * @param t the error
   */
  public void completeWithError(Throwable t) {
    subscribers.values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toSet())
        .forEach(sub -> sub.completeWithError(this, t));
  }

  /**
   * Subscriber started listening. Subscription startup logic (if any) goes here.
   *
   * @param subscriberAddedEvent
   */
  protected void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent) {
    LOGGER.debug("Subscriber {} added", subscriberAddedEvent.getUserId());
  }

  /**
   * Subscriber stopped listening. Subscription teardown logic (if any) goes here.
   *
   * @param subscriberRemovedEvent
   */
  protected void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent) {
    LOGGER.debug("Subscriber {} removed", subscriberRemovedEvent.getUserId());
  }

  /**
   * Returns the event types that this instance of SsePublisher handles. Client applications must
   * specify the types they want to subscribe to in the request path.
   *
   * @return the event types list
   */
  public abstract List<String> getEventTypes();

  /**
   * Process event targeted to the specified subscriber
   *
   * @param subscriber
   * @param event
   */
  protected abstract void handleEvent(SseSubscriber subscriber, SseEvent event);

}
