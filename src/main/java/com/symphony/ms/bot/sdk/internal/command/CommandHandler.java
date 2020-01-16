package com.symphony.ms.bot.sdk.internal.command;

import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;

/**
 * Base class for bot command handling. Provides mechanisms to automatically register child classes
 * to {@link CommandDispatcher} and {@link CommandFilter}.
 *
 * @author Marcus Secato
 */
public abstract class CommandHandler implements BaseCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

  protected CommandDispatcher commandDispatcher;

  protected CommandFilter commandFilter;

  private MessageService messageService;

  protected FeatureManager featureManager;

  protected UsersClient usersClient;

  /**
   * Registers the CommandHandler to {@link CommandDispatcher} and {@link CommandFilter} so that it
   * can listen to and handle commands.
   */
  public void register() {
    init();
    commandDispatcher.register(getCommandName(), this);
    commandFilter.addFilter(getCommandName(), getCommandMatcher());
  }

  protected String getCommandName() {
    return this.getClass().getCanonicalName();
  }

  protected String getBotName() {
    return usersClient.getBotDisplayName();
  }

  /**
   * Initializes the CommandHandler dependencies. This method can be overridden by the child classes
   * if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onCommand(BotCommand command) {
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

  /**
   * Returns the pattern used by {@link CommandFilter} to filter out bot commands.
   *
   * @return the matcher object
   */
  protected abstract Predicate<String> getCommandMatcher();

  /**
   * Handles a command issued to the bot
   *
   * @param command
   * @param commandResponse the response to be sent to Symphony chat
   */
  public abstract void handle(BotCommand command, final SymphonyMessage commandResponse);

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

  public void setUsersClient(UsersClient usersClient) {
    this.usersClient = usersClient;
  }

}
