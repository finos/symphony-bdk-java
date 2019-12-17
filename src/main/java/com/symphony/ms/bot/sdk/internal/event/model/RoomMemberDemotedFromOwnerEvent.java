package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomMemberDemotedFromOwner;

/**
 * Symphony Room member demoted event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomMemberDemotedFromOwnerEvent extends BaseEvent {

  public RoomMemberDemotedFromOwnerEvent(RoomMemberDemotedFromOwner event) {
    this.streamId = event.getStream().getStreamId();
    this.userId = event.getAffectedUser().getUserId().toString();
  }

}

