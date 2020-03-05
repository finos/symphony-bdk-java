package com.symphony.bot.sdk.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.symphony.bot.sdk.internal.command.CommandHandler;
import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code. Simple hello message.
 */
public class HelloCommandHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /hello$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    Map<String, String> variables = new HashMap<>();
    variables.put("user", command.getUser().getDisplayName());

    response.setTemplateMessage("Hello, <b>{{user}}</b>", variables);
  }

}
