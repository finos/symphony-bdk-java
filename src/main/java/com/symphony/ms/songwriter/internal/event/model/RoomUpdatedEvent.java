package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomUpdated;

@Data
@NoArgsConstructor
public class RoomUpdatedEvent extends BaseEvent {

  public RoomUpdatedEvent(RoomUpdated event) {
    this.streamId = event.getStream().getStreamId();
  }

}

