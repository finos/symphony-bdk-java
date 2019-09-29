package com.symphony.ms.songwriter.event;

import com.symphony.ms.songwriter.internal.event.EventHandler;
import com.symphony.ms.songwriter.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class UserJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage response) {
    response.setMessage("HANDLE USER JOINED EVENT " + event.getUserDisplayName());
  }

}
