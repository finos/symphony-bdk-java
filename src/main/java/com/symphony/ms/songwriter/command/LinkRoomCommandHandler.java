package com.symphony.ms.songwriter.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class LinkRoomCommandHandler extends CommandHandler {

  private static final String NOTIFICATION_PATH = "/notification";

  @Value( "${server.servlet.context-path}" )
  private String servletContext;

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " link room$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    commandResponse.setMessage(featureManager.getLinkRoomBaseUrl()
        + servletContext + NOTIFICATION_PATH + "/" + command.getStreamId());
  }

}
