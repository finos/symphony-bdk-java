package com.symphony.bdk.examples.command;

import com.symphony.bdk.core.command.BotCommand;
import com.symphony.bdk.core.command.BotCommandContext;
import com.symphony.bdk.core.command.BotCommandMatcher;
import com.symphony.bdk.core.command.BotMentionBotCommandMatcher;
import com.symphony.bdk.core.command.event.OnMessageSent;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: add description here
 */
@Slf4j
public class GifCommand implements BotCommand<OnMessageSent> {

  @Override
  public void onCommand(BotCommandContext context) {
    log.info("on bot mention!");
  }

  @Override
  public BotCommandMatcher matcher() {
    return new BotMentionBotCommandMatcher("@Thibault's Bot"); // TODO retrieve it from Bot session
  }

  @Override
  public Class<OnMessageSent> getCommandEventType() {
    return OnMessageSent.class;
  }
}
