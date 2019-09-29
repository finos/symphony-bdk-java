package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberPromotedToOwner;

@Data
@NoArgsConstructor
public class RoomMemberPromotedToOwnerEvent extends BaseEvent {

  public RoomMemberPromotedToOwnerEvent(RoomMemberPromotedToOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId().toString();
  }

}
