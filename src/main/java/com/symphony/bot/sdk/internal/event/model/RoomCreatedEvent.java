package com.symphony.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomCreated;

/**
 * Symphony Room created event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomCreatedEvent extends BaseEvent {

  private RoomDetails room;
  private StreamDetails stream;

  public RoomCreatedEvent(RoomCreated event) {
    this.streamId = event.getStream().getStreamId();
    this.room = new RoomDetails(event.getRoomProperties());
    this.stream = new StreamDetails(event.getStream());
  }

}
