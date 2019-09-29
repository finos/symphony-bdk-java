package com.symphony.ms.songwriter.internal.command;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;

@Service
public class CommandDispatcherImpl implements CommandDispatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcherImpl.class);

  private Map<String, CommandHandler> commandHandlers = new HashMap<>();

  @Override
  public void register(String channel, CommandHandler handler) {
    LOGGER.info("Registering command handler: {}", channel);
    commandHandlers.put(channel, handler);
  }

  @Override
  @Async
  public void push(String channel, MessageEvent command) {
    LOGGER.debug("Looking for command handler for {}", channel);
    CommandHandler handler = commandHandlers.get(channel);
    if (handler != null) {
      handler.onCommand(command);
    }
  }

}
