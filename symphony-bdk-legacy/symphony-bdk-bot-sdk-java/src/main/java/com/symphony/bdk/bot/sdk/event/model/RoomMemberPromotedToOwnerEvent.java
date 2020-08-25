package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberPromotedToOwner;

/**
 * Symphony Room member promoted event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomMemberPromotedToOwnerEvent extends BaseEvent {

  private StreamDetails stream;
  private UserDetails user;

  public RoomMemberPromotedToOwnerEvent(RoomMemberPromotedToOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId();
    this.stream = new StreamDetails(event.getStream());
    this.user = new UserDetails(event.getAffectedUser());
  }

}
