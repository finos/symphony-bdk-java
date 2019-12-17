package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomCreated;

/**
 * Symphony Room created event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomCreatedEvent extends BaseEvent {

  public RoomCreatedEvent(RoomCreated event) {
    this.streamId = event.getStream().getStreamId();
  }

}
