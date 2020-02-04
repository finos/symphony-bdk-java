package com.symphony.ms.bot.sdk.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.bot.sdk.internal.command.DefaultCommandHandler;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code for DefaultCommandHandler. Returns a simple static message when bot is mentioned.
 */
public class DefaultBotMentionHandler extends DefaultCommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName())
        .asPredicate();
  }

  /**
   * Invoked when bot is mentioned but no command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    response.setMessage("Sorry, I could not understand");
  }

}
