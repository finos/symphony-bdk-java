package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

/**
 * Symphony Room reactivated event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class RoomReactivatedEvent extends BaseEvent {

  public RoomReactivatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
  }

}

