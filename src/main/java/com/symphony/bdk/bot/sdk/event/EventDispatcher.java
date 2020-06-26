package com.symphony.bdk.bot.sdk.event;

import com.symphony.bdk.bot.sdk.event.model.BaseEvent;

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
   * @param <E> the event type
   * @param channel the channel name to register to
   * @param handler the handler for the specified channel
   */
  <E extends BaseEvent> void register(String channel,
      BaseEventHandler<E> handler);

  /**
   * Dispatch the event pushed by the {@link InternalEventListener} to the
   * corresponding {@link BaseEventHandler}.
   *
   * @param <E> the event type
   * @param channel the channel to push events to
   * @param event the event triggered in Symphony
   */
  <E extends BaseEvent> void push(String channel, E event);

}
