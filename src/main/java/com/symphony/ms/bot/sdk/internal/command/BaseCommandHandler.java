package com.symphony.ms.bot.sdk.internal.command;

import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;

/**
 * Base interface for all CommandHandlers
 *
 * @author Marcus Secato
 *
 */
public interface BaseCommandHandler {

  /**
   * Callback for when a command is received
   *
   * @param command
   */
  void onCommand(BotCommand command);
}
