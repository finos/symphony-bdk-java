package com.symphony.ms.songwriter.internal.elements;

import com.symphony.ms.songwriter.internal.event.EventHandler;
import com.symphony.ms.songwriter.internal.event.model.SymphonyElementsEvent;

public abstract class ElementsActionHandler extends
    EventHandler<SymphonyElementsEvent> {

  @Override
  public void register() {
    eventDispatcher.register(getElementsFormId(), this);
  }

  protected abstract String getElementsFormId();

}
