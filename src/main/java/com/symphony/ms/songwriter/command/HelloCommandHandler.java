package com.symphony.ms.songwriter.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class HelloCommandHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " hello$")
        .asPredicate();
  }

  @Override
  public void handle(MessageEvent command, SymphonyMessage response) {
    Map<String, String> variables = new HashMap<>();
    variables.put("user", command.getUserDisplayName());

    response.setTemplateMessage("Hello, <b>${user}</b>", variables);
  }

}
