package com.symphony.bdk.bot.sdk.elements;

import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.bdk.bot.sdk.command.BaseCommandHandler;
import com.symphony.bdk.bot.sdk.command.CommandDispatcher;
import com.symphony.bdk.bot.sdk.command.CommandFilter;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.event.BaseEventHandler;
import com.symphony.bdk.bot.sdk.event.EventDispatcher;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;
import com.symphony.bdk.bot.sdk.feature.FeatureManager;
import com.symphony.bdk.bot.sdk.symphony.MessageClientImpl;
import com.symphony.bdk.bot.sdk.symphony.UsersClient;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

import lombok.Setter;

/**
 * Symphony Elements Handler
 * <p>
 * Offers all necessary support to handle Symphony elements, from the command to display the
 * Symphony elements in a chat room to the callback triggered when the Symphony elements form is
 * submitted.
 *
 * @author Marcus Secato
 */
@Setter
public abstract class ElementsHandler implements
    BaseCommandHandler, BaseEventHandler<SymphonyElementsEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ElementsHandler.class);

  private EventDispatcher eventDispatcher;
  private CommandDispatcher commandDispatcher;
  private CommandFilter commandFilter;
  private MessageClientImpl messageClient;
  private FeatureManager featureManager;
  private UsersClient usersClient;

  private void register() {
    init();
    commandDispatcher.register(getCommandName(), this);
    commandFilter.addFilter(getCommandName(), getCommandMatcher());
    eventDispatcher.register(getElementsFormId(), this);
  }

  /**
   * Initializes the EventHandler dependencies. This method can be overridden by the child classes
   * if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onCommand(BotCommand command) {
    LOGGER.debug("Received command to display elements form {}", command.getMessageEvent());

    final SymphonyMessage elementsResponse = new SymphonyMessage();
    try {
      displayElements(command, elementsResponse);

      if (elementsResponse.hasContent()) {
        messageClient._sendMessage(command.getMessageEvent().getStreamId(), elementsResponse);
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
   * {@inheritDoc}
   */
  @Override
  public void onEvent(SymphonyElementsEvent event) {
    LOGGER.debug("Received action for elements form: {}", event.getFormId());

    final SymphonyMessage eventResponse = new SymphonyMessage();
    try {
      handleAction(event, eventResponse);

      if (eventResponse.hasContent()
          && featureManager.isCommandFeedbackEnabled()) {
        messageClient._sendMessage(event.getStreamId(), eventResponse);
      }

    } catch (Exception e) {
      LOGGER.error("Error processing elements action {}", e);
      if (featureManager.unexpectedErrorResponse() != null) {
        messageClient._sendMessage(event.getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }
  }

  private String getCommandName() {
    return this.getClass().getCanonicalName();
  }

  protected String getBotName() {
    return usersClient.getBotDisplayName();
  }

  /**
   * Returns the pattern used by {@link CommandFilter} to filter out bot commands.
   *
   * @return the matcher object
   */
  protected abstract Predicate<String> getCommandMatcher();

  /**
   * Specifies which Symphony elements form this handler should listen events for.
   *
   * @return the Symphony elements formId
   */
  protected abstract String getElementsFormId();

  /**
   * Displays the Symphony elements form
   *
   * @param command the command issued in chat room
   * @param elementsResponse the response to be sent to Symphony
   */
  public abstract void displayElements(BotCommand command,
      final SymphonyMessage elementsResponse);

  /**
   * Handle the action triggered when Symphony elements form is submitted
   *
   * @param event the event triggered in Symphony
   * @param elementsResponse the response to be sent to Symphony
   */
  public abstract void handleAction(SymphonyElementsEvent event,
      final SymphonyMessage elementsResponse);

}
