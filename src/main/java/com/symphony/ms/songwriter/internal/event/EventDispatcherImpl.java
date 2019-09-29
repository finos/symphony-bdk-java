package com.symphony.ms.songwriter.internal.event;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.symphony.ms.songwriter.internal.event.model.BaseEvent;

@Service
public class EventDispatcherImpl implements EventDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcherImpl.class);

  private Map<String, EventHandler> eventHandlers = new HashMap<>();

  @Override
  public <E extends BaseEvent> void register(String channel, EventHandler<E> handler) {
    LOGGER.info("Registering handler for event: {}", channel);
    eventHandlers.put(channel, handler);
  }

  @Override
  @Async
  public <E extends BaseEvent> void push(String channel, E event) {
    LOGGER.debug("Looking for handler for event: {}", channel);
    EventHandler<E> handler = eventHandlers.get(channel);
    if (handler != null) {
      handler.onEvent(event);
    }
  }

}
