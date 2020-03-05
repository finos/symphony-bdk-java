package com.symphony.bot.sdk.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;

import com.symphony.bot.sdk.internal.command.CommandHandler;
import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.feature.FeatureManager;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code for a CommandHandler that generates instructions on how to receive notifications from
 * external systems.
 */
public class CreateNotificationCommandHandler extends CommandHandler {

  private static final String NOTIFICATION_PATH = "/notification";

  private FeatureManager featureManager;

  public CreateNotificationCommandHandler(FeatureManager featureManager) {
    this.featureManager = featureManager;
  }

  @Value("${server.servlet.context-path}")
  private String servletContext;

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /create notification$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    String notificationUrl = featureManager.getNotificationBaseUrl()
        + servletContext + NOTIFICATION_PATH + "/" + command.getMessageEvent().getStreamId();

    Map<String, String> data = new HashMap<>();
    data.put("notification_url", notificationUrl);

    commandResponse.setTemplateFile("create-notification", data);
  }

}
