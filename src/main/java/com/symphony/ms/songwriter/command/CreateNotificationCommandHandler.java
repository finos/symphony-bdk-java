package com.symphony.ms.songwriter.command;

import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Sample code for a CommandHandler that generates instructions on how to
 * receive notifications from external systems.
 *
 */
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

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    String notificationUrl = featureManager.getNotificationBaseUrl()
        + servletContext + NOTIFICATION_PATH + "/" + command.getStreamId();

    Map<String, String> data = new HashMap<>();
    data.put("notification_url", notificationUrl);

    commandResponse.setTemplateFile("create-notification", data);
  }

}
