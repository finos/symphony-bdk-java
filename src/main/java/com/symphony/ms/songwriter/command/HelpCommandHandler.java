package com.symphony.ms.songwriter.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

/**
 * Sample code. Help command to return the list of available commands
 *
 */
public class HelpCommandHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /help$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    String botMention = "<b>@" + getBotName() + "</b>";
    String[] commands = {
        botMention + " /hello - simple hello command",
        botMention + " /help - displays the list of commands",
        botMention + " /create notification - generates details on how to receive notification in this room",
        botMention + " /login - returns the HTTP authorization header required to talk to external system",
        botMention + " /quote BRL - returns quote for the specified currency (e.g. BRL)",
        botMention + " /register quote - displays the currency quote registration form"};

    String helpMessage = "<p style='margin-bottom:6px;'>Bot Commands</p><ul>";
    for (int i = 0; i < commands.length; i++) {
      helpMessage = helpMessage.concat("<li>" + commands[i] + "</li>");
    }
    helpMessage = helpMessage.concat("</ul>");

    Map<String, Object> data = new HashMap<>();
    data.put("title", "Bot Commands");
    data.put("content", commands);

    response.setEnrichedMessage(helpMessage, "com.symphony.ms.template.helpCommand", data, "1.0");
  }

}
