package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.command.model.BotCommand;

public interface CommandDispatcher {

  void register(String channel, BaseCommandHandler handler);

  void push(String channel, BotCommand command);

}
