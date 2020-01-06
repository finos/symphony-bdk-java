package com.symphony.ms.bot.sdk.internal.event.features;

import com.symphony.ms.bot.sdk.internal.event.EventHandler;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link EventHandler} to check if the user joining the room is the configured
 * bot and react to that.
 */
public class BotJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BotJoinedEventHandler.class);

  private final SymphonyService symphonyService;
  private final FeatureManager featureManager;
  private final MessageService messageService;

  public BotJoinedEventHandler(SymphonyService symphonyService, FeatureManager featureManager,
      MessageService messageService) {
    this.symphonyService = symphonyService;
    this.featureManager = featureManager;
    this.messageService = messageService;
  }

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage eventResponse) {
    if (userIsAppBot(event)) {
      if (isPublicRoomNotAllowed() && isPublicRoom(event)) {
        sendPublicRoomNotAllowedMessage(event.getStreamId());
        removeBotFromRoom(event);
      }
    }
  }

  private boolean userIsAppBot(UserJoinedRoomEvent event) {
    return event.getUser().getDisplayName().equals(symphonyService.getBotDisplayName());
  }

  private boolean isPublicRoomNotAllowed() {
    return !featureManager.isPublicRoomAllowed();
  }

  private boolean isPublicRoom(UserJoinedRoomEvent event) {
    return symphonyService.getRoomInfo(event.getStreamId()).getRoomAttributes().getPublic();
  }

  private void sendPublicRoomNotAllowedMessage(String streamId) {
    SymphonyMessage symphonyMessage = new SymphonyMessage();
    if (isPublicRoomNotAllowedTemplateDefined()) {
      symphonyMessage.setTemplateFile(featureManager.getPublicRoomNotAllowedTemplate(),
              featureManager.getPublicRoomNotAllowedTemplateMap());
      messageService.sendMessage(streamId, symphonyMessage);
    } else if (isPublicRoomNotAllowedMessageDefined()) {
      symphonyMessage.setMessage(featureManager.getPublicRoomNotAllowedMessage());
      messageService.sendMessage(streamId, symphonyMessage);
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

  private void removeBotFromRoom(UserJoinedRoomEvent event) {
    LOGGER.debug("Removing bot from room (streamId={}). Bot in public room not allowed.",
            event.getStreamId());
    symphonyService.removeMemberFromRoom(event.getStreamId(), new Long(event.getUserId()));
  }

}
