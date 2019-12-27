package com.symphony.ms.bot.sdk.event;

import com.symphony.ms.bot.sdk.internal.event.EventHandler;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;

/**
 * Sample code. Implementation of {@link EventHandler} to check if the user joining the room is the
 * configured bot and react to that.
 */
public class BotJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {

  private final String botDisplayName;

  public BotJoinedEventHandler(SymphonyService symphonyService) {
    this.botDisplayName = symphonyService.getBotDisplayName();
  }

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage eventResponse) {
    if (event.getUser().getDisplayName().equals(botDisplayName)) {
      eventResponse.setMessage("<mention uid=\"" + event.getUserId()
          + "\"/> was added to the room. For details on how to use it, please type: @"
          + botDisplayName + " /help");
    }
  }
}
