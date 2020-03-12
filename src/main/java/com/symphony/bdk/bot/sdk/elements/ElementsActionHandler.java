package com.symphony.bdk.bot.sdk.elements;

import com.symphony.bdk.bot.sdk.event.EventDispatcher;
import com.symphony.bdk.bot.sdk.event.EventHandler;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;

import lombok.Setter;

/**
 * Symphony elements event handler
 *
 * @author Marcus Secato
 *
 */
@Setter
public abstract class ElementsActionHandler extends
    EventHandler<SymphonyElementsEvent> {

  private EventDispatcher eventDispatcher;

  private void register() {
    eventDispatcher.register(getElementsFormId(), this);
  }

  /**
   * Specifies which Symphony elements form this handler should listen events
   * for.
   *
   * @return the Symphony elements formId
   */
  protected abstract String getElementsFormId();

}
