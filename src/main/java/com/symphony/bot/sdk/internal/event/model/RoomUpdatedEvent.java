package com.symphony.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomUpdated;

/**
 * Symphony Room updated event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomUpdatedEvent extends BaseEvent {

  private RoomDetails room;
  private StreamDetails stream;

  public RoomUpdatedEvent(RoomUpdated event) {
    this.streamId = event.getStream().getStreamId();
    this.room = new RoomDetails(event.getNewRoomProperties());
    this.stream = new StreamDetails(event.getStream());
  }

}

