package com.symphony.ms.bot.sdk.internal.event.features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.event.EventHandler;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Implementation of {@link EventHandler} to check if the user joining the room is the configured
 * bot and react to that.
 */
public class BotJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotJoinedEventHandler.class);

  private final StreamsClient streamsClient;
  private final UsersClient usersClient;
  private final FeatureManager featureManager;
  private final MessageClientImpl messageClient;

  public BotJoinedEventHandler(StreamsClient streamsClient, UsersClient usersClient,
      FeatureManager featureManager, MessageClientImpl messageClient) {
    this.streamsClient = streamsClient;
    this.usersClient = usersClient;
    this.featureManager = featureManager;
    this.messageClient = messageClient;
  }

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage eventResponse) {
    if (userIsAppBot(event) && isPublicRoomNotAllowed()) {
      try {
        if (isPublicRoom(event)) {
          sendPublicRoomNotAllowedMessage(event.getStreamId());
          removeBotFromRoom(event);
        }
      } catch (SymphonyClientException sce) {
        LOGGER.error("Failed to process bot in public room", sce);
      }
    }
  }

  private boolean userIsAppBot(UserJoinedRoomEvent event) {
    return event.getUser().getDisplayName().equals(usersClient.getBotDisplayName());
  }

  private boolean isPublicRoomNotAllowed() {
    return !featureManager.isPublicRoomAllowed();
  }

  private boolean isPublicRoom(UserJoinedRoomEvent event) throws SymphonyClientException {
    LOGGER.debug("Checking if room is public");
    return streamsClient.getRoomInfo(event.getStreamId()).getPublicRoom();
  }

  private void sendPublicRoomNotAllowedMessage(String streamId) {
    SymphonyMessage symphonyMessage = new SymphonyMessage();
    if (isPublicRoomNotAllowedTemplateDefined()) {
      symphonyMessage.setTemplateFile(featureManager.getPublicRoomNotAllowedTemplate(),
              featureManager.getPublicRoomNotAllowedTemplateMap());
      messageClient._sendMessage(streamId, symphonyMessage);
    } else if (isPublicRoomNotAllowedMessageDefined()) {
      symphonyMessage.setMessage(featureManager.getPublicRoomNotAllowedMessage());
      messageClient._sendMessage(streamId, symphonyMessage);
    }
  }

  private boolean isPublicRoomNotAllowedTemplateDefined() {
    return featureManager.getPublicRoomNotAllowedTemplate() != null &&
            !featureManager.getPublicRoomNotAllowedTemplate().isEmpty();
  }

  private boolean isPublicRoomNotAllowedMessageDefined() {
    return featureManager.getPublicRoomNotAllowedMessage() != null &&
            !featureManager.getPublicRoomNotAllowedMessage().isEmpty();
  }

  private void removeBotFromRoom(UserJoinedRoomEvent event) throws SymphonyClientException {
    LOGGER.debug("Removing bot from room (streamId={}). Bot in public room not allowed.",
            event.getStreamId());
    streamsClient.removeMemberFromRoom(event.getStreamId(), new Long(event.getUserId()));
  }

}
