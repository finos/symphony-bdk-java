package com.symphony.ms.songwriter.internal.command;

public abstract class DefaultCommandHandler extends CommandHandler {

  @Override
  public void register() {
    commandDispatcher.register(getCommandName(), this);
    commandFilter.setDefaultFilter(getCommandName(), getCommandMatcher());
  }

}
