package com.symphony.bot.sdk.internal.command;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.feature.FeatureManager;
import com.symphony.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

import lombok.Setter;

/**
 * Base class for bot command handling. Has it child classes automatically registered to {@link
 * CommandDispatcher} and {@link CommandFilter}. Provides mechanism for developers to define
 * responses for many rooms
 *
 * @author Gabriel Berberian
 */
@Setter
public abstract class MultiResponseCommandHandler extends CommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultiResponseCommandHandler.class);

  private MessageClientImpl messageClient;

  private FeatureManager featureManager;

  private String getCommandName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  public void handle(BotCommand command, final SymphonyMessage commandResponse) {
    try {
      MultiResponseComposerImpl multiResponseComposer =
          new MultiResponseComposerImpl();
      handle(command, multiResponseComposer);
      if (!multiResponseComposer.isComplete()) {
        LOGGER.error("Error processing command {}\nIncomplete multi response composer",
            getCommandName());
      } else if (multiResponseComposer.hasContent() && featureManager.isCommandFeedbackEnabled()) {
        sendContent(multiResponseComposer.getComposedResponse());
      }
    } catch (Exception e) {
      LOGGER.error("Error processing command {}\n{}", getCommandName(), e);
      if (featureManager.unexpectedErrorResponse() != null) {
        messageClient._sendMessage(command.getMessageEvent().getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }
  }

  private void sendContent(Map<SymphonyMessage, Set<String>> composedResponse) {
    composedResponse.forEach((message, streamIds) -> {
      sendMessageToStreams(message, streamIds);
    });
  }

  private void sendMessageToStreams(SymphonyMessage symphonyMessage, Set<String> streamIds) {
    for (String streamId : streamIds) {
      messageClient._sendMessage(streamId, symphonyMessage);
    }
  }

  /**
   * Handles a command issued to the bot
   *
   * @param command
   * @param multiResponseComposer the response composer in which the developer will define the
   *                              messages to be sent to Symphony
   */
  public abstract void handle(BotCommand command, MultiResponseComposer multiResponseComposer);

}
