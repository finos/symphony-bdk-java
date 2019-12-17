package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.InboundMessage;

/**
 * Symphony message event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class MessageEvent extends BaseEvent {
  private String message;
  private String userDisplayName;

  public MessageEvent(InboundMessage message) {
    this.message = message.getMessageText().trim();
    this.userId = message.getUser().getUserId().toString();
    this.userDisplayName = message.getUser().getDisplayName();
    this.streamId = message.getStream().getStreamId();
  }

}
