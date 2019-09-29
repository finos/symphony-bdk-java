package com.symphony.ms.songwriter.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

@Data
@NoArgsConstructor
public class IMCreatedEvent extends BaseEvent {

  public IMCreatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
  }

}


