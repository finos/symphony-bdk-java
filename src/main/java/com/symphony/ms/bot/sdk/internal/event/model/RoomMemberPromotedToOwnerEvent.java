package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberPromotedToOwner;

/**
 * Symphony Room member promoted event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomMemberPromotedToOwnerEvent extends BaseEvent {

  public RoomMemberPromotedToOwnerEvent(RoomMemberPromotedToOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId().toString();
  }

}
