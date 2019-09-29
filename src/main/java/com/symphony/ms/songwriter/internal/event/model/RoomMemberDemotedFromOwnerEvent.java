package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberDemotedFromOwner;

@Data
@NoArgsConstructor
public class RoomMemberDemotedFromOwnerEvent extends BaseEvent {

  public RoomMemberDemotedFromOwnerEvent(RoomMemberDemotedFromOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId().toString();
  }

}

