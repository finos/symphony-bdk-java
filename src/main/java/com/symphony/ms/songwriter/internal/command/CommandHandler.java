package com.symphony.ms.songwriter.internal.command;

import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.feature.FeatureManager;
import com.symphony.ms.songwriter.internal.message.MessageService;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;
import com.symphony.ms.songwriter.internal.symphony.SymphonyService;

public abstract class CommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

  protected CommandDispatcher commandDispatcher;

  protected CommandFilter commandFilter;

  private MessageService messageService;

  protected FeatureManager featureManager;

  private SymphonyService symphonyService;

  public void register() {
    commandDispatcher.register(getCommandName(), this);
    commandFilter.addFilter(getCommandName(), getCommandMatcher());
  }

  protected String getCommandName() {
    return this.getClass().getCanonicalName();
  }

  protected String getBotName() {
    return symphonyService.getBotDisplayName();
  }

  public void onCommand(MessageEvent command) {
    LOGGER.debug("Received command {}", command.getMessage());

    final SymphonyMessage commandResponse = new SymphonyMessage();
    try {
      handle(command, commandResponse);
      if (commandResponse.hasContent()
          && featureManager.isCommandFeedbackEnabled()) {
        messageService.sendMessage(command.getStreamId(), commandResponse);
      }

    } catch (Exception e) {
      LOGGER.error("Error processing command {}\n{}", getCommandName(), e);
      if (featureManager.unexpectedErrorResponse() != null) {
        messageService.sendMessage(command.getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }
  }

  // TODO: create a command matcher builder
  protected abstract Predicate<String> getCommandMatcher();

  public abstract void handle(MessageEvent command, final SymphonyMessage commandResponse);

  public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  public void setCommandFilter(CommandFilter commandFilter) {
    this.commandFilter = commandFilter;
  }

  public void setMessageService(MessageService messageService) {
    this.messageService = messageService;
  }

  public void setFeatureManager(FeatureManager featureManager) {
    this.featureManager = featureManager;
  }

  public void setSymphonyService(SymphonyService symphonyService) {
    this.symphonyService = symphonyService;
  }
}
