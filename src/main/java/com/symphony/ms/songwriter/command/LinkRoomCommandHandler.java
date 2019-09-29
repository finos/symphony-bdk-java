package com.symphony.ms.songwriter.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class LinkRoomCommandHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " link room$")
        .asPredicate();
  }

  @Override
  public void handle(MessageEvent command, SymphonyMessage commandResponse) {
    commandResponse.setMessage(featureManager.getLinkRoomBaseUrl() + "/" + command.getStreamId());
  }

}
