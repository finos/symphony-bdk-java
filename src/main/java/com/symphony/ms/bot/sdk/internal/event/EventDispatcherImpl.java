package com.symphony.ms.bot.sdk.internal.event;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.event.model.BaseEvent;

@Service
public class EventDispatcherImpl implements EventDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcherImpl.class);

  private Map<String, BaseEventHandler> eventHandlers = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public <E extends BaseEvent> void register(String channel,
      BaseEventHandler<E> handler) {
    LOGGER.info("Registering handler for event: {}", channel);
    eventHandlers.put(channel, handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Async("botTaskExecutor")
  public <E extends BaseEvent> void push(String channel, E event) {
    LOGGER.debug("Looking for handler for event: {}", channel);
    BaseEventHandler<E> handler = eventHandlers.get(channel);
    if (handler != null) {
      LOGGER.debug("Handler found");
      handler.onEvent(event);
    }
  }

}
