package com.symphony.bdk.core.command;

import com.symphony.bdk.core.command.event.CommandEventType;

/**
 * TODO: add description here
 */
public interface BotCommand<T extends CommandEventType<?>> {

  void onCommand(BotCommandContext context);

  BotCommandMatcher matcher();

  Class<T> getCommandEventType();
}
