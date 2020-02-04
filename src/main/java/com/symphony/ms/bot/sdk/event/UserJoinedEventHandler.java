package com.symphony.ms.bot.sdk.event;

import com.symphony.ms.bot.sdk.internal.event.EventHandler;
import com.symphony.ms.bot.sdk.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code. Implementation of {@link EventHandler} to send greeting message
 * to users joining a room with the bot.
 *
 */
public class UserJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {

  /**
   * Invoked when event is triggered in Symphony
   */
  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage response) {
    response.setMessage("Hey, <mention uid=\"" + event.getUserId() +
        "\"/>. It is good to have you here!");
  }

}
