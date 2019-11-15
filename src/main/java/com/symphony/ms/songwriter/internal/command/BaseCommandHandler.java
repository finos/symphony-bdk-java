package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.command.model.BotCommand;

public interface BaseCommandHandler {
  void onCommand(BotCommand command);
}
