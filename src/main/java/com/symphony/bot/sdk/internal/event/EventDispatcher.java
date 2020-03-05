package com.symphony.bot.sdk.internal.event;

import com.symphony.bot.sdk.internal.event.model.BaseEvent;

/**
 * Dispatches events to corresponding {@link EventHandler}
 *
 * @author Marcus Secato
 *
 */
public interface EventDispatcher {

  /**
   * Registers a {@link BaseEventHandler} for the given channel (aka event
   * name).
   *
   * @param channel
   * @param handler
   */
  <E extends BaseEvent> void register(String channel,
      BaseEventHandler<E> handler);

  /**
   * Dispatch the event pushed by the {@link InternalEventListener} to the
   * corresponding {@link BaseEventHandler}.
   *
   * @param channel
   * @param event
   */
  <E extends BaseEvent> void push(String channel, E event);

}
