package com.symphony.bdk.bot.sdk.command;

import com.symphony.bdk.bot.sdk.command.model.BotCommand;

/**
 * Dispatches commands to corresponding {@link CommandHandler}
 *
 * @author Marcus Secato
 *
 */
public interface CommandDispatcher {

  /**
   * Registers a {@link BaseCommandHandler} for the given channel (aka command
   * name).
   *
   * @param channel
   * @param handler
   */
  void register(String channel, BaseCommandHandler handler);

  /**
   * Dispatch the command pushed by the {@link CommandFilter} to the
   * corresponding {@link BaseCommandHandler}.
   *
   * @param channel
   * @param command
   */
  void push(String channel, BotCommand command);

}
