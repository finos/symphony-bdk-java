package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomUpdated;

/**
 * Symphony Room updated event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomUpdatedEvent extends BaseEvent {

  public RoomUpdatedEvent(RoomUpdated event) {
    this.streamId = event.getStream().getStreamId();
  }

}

