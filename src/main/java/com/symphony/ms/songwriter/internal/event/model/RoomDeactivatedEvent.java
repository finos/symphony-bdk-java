package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomDeactivated;

@Data
@NoArgsConstructor
public class RoomDeactivatedEvent extends BaseEvent {

  public RoomDeactivatedEvent(RoomDeactivated event) {
    this.streamId = event.getStream().getStreamId();
  }

}

