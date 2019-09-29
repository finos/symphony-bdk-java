package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.UserJoinedRoom;


@Data
@NoArgsConstructor
public class UserJoinedRoomEvent extends BaseEvent {

  private String userDisplayName;
  private String roomName;

  public UserJoinedRoomEvent(UserJoinedRoom event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId().toString();
    this.userDisplayName = event.getAffectedUser().getDisplayName();
    this.roomName = event.getStream().getRoomName();
  }

}
