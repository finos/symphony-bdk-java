package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

/**
 * Symphony IM created event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class IMCreatedEvent extends BaseEvent {

  private StreamDetails stream;

  public IMCreatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
    this.stream = new StreamDetails(stream);
  }

}


