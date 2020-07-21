package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

/**
 * Symphony Room reactivated event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomReactivatedEvent extends BaseEvent {

  public StreamDetails stream;

  public RoomReactivatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
    this.stream = new StreamDetails(stream);
  }

}

