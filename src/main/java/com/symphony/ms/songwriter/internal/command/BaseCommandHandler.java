package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.command.model.BotCommand;

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
