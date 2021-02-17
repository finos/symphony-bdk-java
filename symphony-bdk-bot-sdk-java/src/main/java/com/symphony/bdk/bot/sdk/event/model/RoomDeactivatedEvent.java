package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.events.RoomDeactivated;

/**
 * Symphony Room deactivated event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class RoomDeactivatedEvent extends BaseEvent {

  private StreamDetails stream;

  public RoomDeactivatedEvent(RoomDeactivated event) {
    this.streamId = event.getStream().getStreamId();
    this.stream = new StreamDetails(event.getStream());
  }

}

