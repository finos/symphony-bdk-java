package com.symphony.ms.songwriter.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class CreateNotificationCommandHandler extends CommandHandler {

  private static final String NOTIFICATION_PATH = "/notification";

  @Value( "${server.servlet.context-path}" )
  private String servletContext;

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /create notification$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    String linkToRoom = featureManager.getLinkRoomBaseUrl()
        + servletContext + NOTIFICATION_PATH + "/" + command.getStreamId();

    Map<String, String> data = new HashMap<>();
    data.put("notification_url", linkToRoom);

    commandResponse.setTemplateFile("create-notification.ftl", data);
  }

}
