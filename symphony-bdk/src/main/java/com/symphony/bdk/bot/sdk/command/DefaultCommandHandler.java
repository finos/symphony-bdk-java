package com.symphony.bdk.bot.sdk.command;

import lombok.Setter;

/**
 * Offers a default response for when bot does not receive a valid command
 *
 * @author Marcus Secato
 *
 */
@Setter
public abstract class DefaultCommandHandler extends CommandHandler {

  private CommandDispatcher commandDispatcher;

  private CommandFilter commandFilter;

  private void register() {
    init();
    commandDispatcher.register(getCommandName(), this);
    commandFilter.setDefaultFilter(getCommandName(), getCommandMatcher());
  }

  private String getCommandName() {
    return this.getClass().getCanonicalName();
  }

}
