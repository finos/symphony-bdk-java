package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberDemotedFromOwner;

/**
 * Symphony Room member demoted event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomMemberDemotedFromOwnerEvent extends BaseEvent {

  private StreamDetails stream;
  private UserDetails user;

  public RoomMemberDemotedFromOwnerEvent(RoomMemberDemotedFromOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId();
    this.stream = new StreamDetails(event.getStream());
    this.user = new UserDetails(event.getAffectedUser());
  }

}

