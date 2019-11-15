package com.symphony.ms.songwriter.internal.event;

import com.symphony.ms.songwriter.internal.event.model.BaseEvent;

public interface BaseEventHandler<E extends BaseEvent> {
  void onEvent(E event);
}
