package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Stream;

/**
 * Symphony IM created event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class IMCreatedEvent extends BaseEvent {

  public IMCreatedEvent(Stream stream) {
    this.streamId = stream.getStreamId();
  }

}


