package com.symphony.ms.songwriter.internal.command;

/**
 * Offers a default response for when bot does not receive a valid command
 *
 * @author Marcus Secato
 *
 */
public abstract class DefaultCommandHandler extends CommandHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public void register() {
    commandDispatcher.register(getCommandName(), this);
    commandFilter.setDefaultFilter(getCommandName(), getCommandMatcher());
  }

}
