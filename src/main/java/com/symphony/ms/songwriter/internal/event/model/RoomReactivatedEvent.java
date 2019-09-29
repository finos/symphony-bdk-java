package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

@Data
@NoArgsConstructor
public class RoomReactivatedEvent extends BaseEvent {

  public RoomReactivatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
  }

}

