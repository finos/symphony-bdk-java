package com.symphony.ms.bot.sdk.internal.command;

import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;
import lombok.Setter;

/**
 * Base class for bot command handling. Has it child classes automatically registered to {@link
 * CommandDispatcher} and {@link CommandFilter}. Provides mechanism for developers to define a
 * response for the command room
 *
 * @author Marcus Secato
 */
@Setter
public abstract class CommandHandler implements BaseCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

  private CommandDispatcher commandDispatcher;

  private CommandFilter commandFilter;

  private MessageClientImpl messageClient;

  private FeatureManager featureManager;

  private UsersClient usersClient;

  private void register() {
    init();
    commandDispatcher.register(getCommandName(), this);
    commandFilter.addFilter(getCommandName(), getCommandMatcher());
  }

  private String getCommandName() {
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
    LOGGER.debug("Received command {}", command.getMessageEvent());

    final SymphonyMessage commandResponse = new SymphonyMessage();
    try {
      handle(command, commandResponse);
      if (commandResponse.hasContent() && featureManager.isCommandFeedbackEnabled()) {
        messageClient._sendMessage(command.getMessageEvent().getStreamId(), commandResponse);
      }

    } catch (Exception e) {
      LOGGER.error("Error processing command {}\n{}", getCommandName(), e);
      if (featureManager.unexpectedErrorResponse() != null) {
        messageClient._sendMessage(command.getMessageEvent().getStreamId(),
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

}
