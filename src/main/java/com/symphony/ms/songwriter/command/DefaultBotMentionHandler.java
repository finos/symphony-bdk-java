package com.symphony.ms.songwriter.command;

import com.symphony.ms.songwriter.internal.command.DefaultCommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

import java.util.function.Predicate;
import java.util.regex.Pattern;

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
