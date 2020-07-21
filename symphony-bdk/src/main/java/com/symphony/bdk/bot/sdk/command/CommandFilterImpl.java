package com.symphony.bdk.bot.sdk.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;

@Service
public class CommandFilterImpl implements CommandFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandFilterImpl.class);

  private CommandDispatcher commandDispatcher;

  private Map<String, Predicate<String>> commandFilters = new HashMap<>();

  private String defaultCommandName;

  private Predicate<String> defaultCommandFilter;

  public CommandFilterImpl(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addFilter(String commandName, Predicate<String> filter) {
    LOGGER.info("Registering filter for command: {}", commandName);
    commandFilters.put(commandName, filter);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDefaultFilter(String commandName,
      Predicate<String> defaultFilter) {
    LOGGER.info("Registering default filter: {}", commandName);
    defaultCommandName = commandName;
    defaultCommandFilter = defaultFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void filter(MessageEvent messageEvent) {
    LOGGER.debug("Filtering message");
    Optional<String> command = commandFilters.entrySet().stream()
        .filter(e -> e.getValue().test(messageEvent.getMessage()))
        .map(Map.Entry::getKey)
        .findFirst();
    if (!command.isPresent()) {
      command = defaultFilter(messageEvent.getMessage());
    }

    command.ifPresent(cmd ->
      commandDispatcher.push(cmd, new BotCommand(
          cmd, messageEvent, commandDispatcher)));
  }

  private Optional<String> defaultFilter(String message) {
    LOGGER.debug("Looking for default filter");
    String defaultCommand = null;
    if (defaultCommandFilter != null
        && defaultCommandFilter.test(message.trim())) {
      defaultCommand = defaultCommandName;
    }

    if (defaultCommand == null) {
      LOGGER.debug("No default filter found");
      return Optional.empty();
    }

    return Optional.of(defaultCommand);
  }

}
