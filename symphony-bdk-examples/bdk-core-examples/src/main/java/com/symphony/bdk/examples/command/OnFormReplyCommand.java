package com.symphony.bdk.examples.command;

import com.symphony.bdk.core.command.BotCommand;
import com.symphony.bdk.core.command.BotCommandContext;
import com.symphony.bdk.core.command.BotCommandMatcher;
import com.symphony.bdk.core.command.event.OnFormReply;

/**
 * TODO: add description here
 */
public class OnFormReplyCommand  implements BotCommand<OnFormReply> {

  @Override
  public void onCommand(BotCommandContext context) {

  }

  @Override
  public BotCommandMatcher matcher() {
    return null;
  }

  @Override
  public Class<OnFormReply> getCommandEventType() {
    return null;
  }
}
