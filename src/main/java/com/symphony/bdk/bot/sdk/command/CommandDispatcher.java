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
   * @param channel the channel to register to
   * @param handler the command handler for the specified channel
   */
  void register(String channel, BaseCommandHandler handler);

  /**
   * Dispatch the command pushed by the {@link CommandFilter} to the
   * corresponding {@link BaseCommandHandler}.
   *
   * @param channel the channel to push command to
   * @param command the command issued in chat room
   */
  void push(String channel, BotCommand command);

}
