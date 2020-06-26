package com.symphony.bdk.bot.sdk.event;

import com.symphony.bdk.bot.sdk.event.model.BaseEvent;

/**
 * Base interface for all EventHandlers
 *
 * @author Marcus Secato
 *
 * @param <E> - class of the event to handle
 */
public interface BaseEventHandler<E extends BaseEvent> {

  /**
   * Callback for when the event is received
   *
   * @param event the event triggered in Symphony
   */
  void onEvent(E event);
}
