package com.symphony.ms.songwriter.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.songwriter.internal.command.DefaultCommandHandler;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class DefaultBotMentionHandler extends DefaultCommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName())
        .asPredicate();
  }

  @Override
  public void handle(MessageEvent command, SymphonyMessage response) {
    response.setMessage("HANDLING DEFAULT COMMAND");
  }

}
