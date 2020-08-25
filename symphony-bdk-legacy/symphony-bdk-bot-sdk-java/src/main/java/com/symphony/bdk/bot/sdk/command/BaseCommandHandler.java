package com.symphony.bdk.bot.sdk.command;

import com.symphony.bdk.bot.sdk.command.model.BotCommand;

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
   * @param command the received command.
   */
  void onCommand(BotCommand command);
}
