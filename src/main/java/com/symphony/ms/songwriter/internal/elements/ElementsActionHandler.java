package com.symphony.ms.songwriter.internal.elements;

import com.symphony.ms.songwriter.internal.event.EventHandler;
import com.symphony.ms.songwriter.internal.event.model.SymphonyElementsEvent;

/**
 * Symphony elements event handler
 *
 * @author Marcus Secato
 *
 */
public abstract class ElementsActionHandler extends
    EventHandler<SymphonyElementsEvent> {

  /**
   * Register this handler to listen to Symphony elements events for a
   * particular form.
   */
  @Override
  public void register() {
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
