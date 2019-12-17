package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomDeactivated;

/**
 * Symphony Room deactivated event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomDeactivatedEvent extends BaseEvent {

  public RoomDeactivatedEvent(RoomDeactivated event) {
    this.streamId = event.getStream().getStreamId();
  }

}

