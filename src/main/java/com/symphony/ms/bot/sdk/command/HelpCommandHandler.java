package com.symphony.ms.bot.sdk.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.bot.sdk.internal.command.CommandHandler;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;

/**
 * Sample code. Help command to return the list of available commands
 */
public class HelpCommandHandler extends CommandHandler {

  private static final String[] DESCRIPTIONS = {
      "/hello - simple hello command",
      "/help - displays the list of commands",
      "/create notification - generates details on how to receive notification in this room",
      "/login - returns the HTTP authorization header required to talk to external system",
      "/quote BRL - returns quote for the specified currency (e.g. BRL)",
      "/register quote - displays the currency quote registration form",
      "/template alert {\"title\": \"Title\", \"content\": \"Content\"} - displays a customized alert"
  };

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /help$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    Map<String, Object> data = new HashMap<>();
    data.put("bot_mention", "@" + getBotName());
    data.put("descriptions", DESCRIPTIONS);
    response.setTemplateFile("help-response", data);
  }

}
