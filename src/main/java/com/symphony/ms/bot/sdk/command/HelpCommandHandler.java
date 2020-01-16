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
    response.setTemplateFile("help-response", data);
  }

}
