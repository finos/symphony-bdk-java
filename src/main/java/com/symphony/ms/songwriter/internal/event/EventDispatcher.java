package com.symphony.ms.songwriter.internal.event;

import com.symphony.ms.songwriter.internal.event.model.BaseEvent;

public interface EventDispatcher {

  <E extends BaseEvent> void register(String channel, BaseEventHandler<E> handler);

  <E extends BaseEvent> void push(String channel, E event);

}
