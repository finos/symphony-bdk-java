package com.symphony.bdk.bot.sdk.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.symphony.bdk.bot.sdk.event.model.BaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventDispatcherImpl implements EventDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcherImpl.class);

  private Map<String, List<BaseEventHandler>> eventHandlers = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public <E extends BaseEvent> void register(String channel, BaseEventHandler<E> handler) {
    LOGGER.info("Registering handler for event: {}", channel);
    eventHandlers.computeIfAbsent(channel, handlers -> new ArrayList<>()).add(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Async("botTaskExecutor")
  public <E extends BaseEvent> void push(String channel, E event) {
    LOGGER.debug("Looking for handler for event: {}", channel);
    eventHandlers.computeIfPresent(channel, (ch, handlers) -> {
      handlers.forEach(handler -> {
        LOGGER.debug("Handler found");
        handler.onEvent(event);
      });
      return handlers;
    });
  }

}
