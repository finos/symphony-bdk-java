package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomCreated;

@Data
@NoArgsConstructor
public class RoomCreatedEvent extends BaseEvent {

  public RoomCreatedEvent(RoomCreated event) {
    this.streamId = event.getStream().getStreamId();
  }

}
