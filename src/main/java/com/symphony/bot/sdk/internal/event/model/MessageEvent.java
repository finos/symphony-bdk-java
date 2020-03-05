package com.symphony.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.InboundMessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Symphony message event
 *
 * @author Marcus Secato
 */
@Data
@NoArgsConstructor
public class MessageEvent extends BaseEvent {
  private String messageId;
  private Long timestamp;
  private String message;
  private String rawMessage;
  private String data;
  private List<MessageAttachment> attachments;
  private boolean externalRecipients;
  private String diagnostic;
  private String userAgent;
  private String originalFormat;
  private UserDetails user;
  private StreamDetails stream;

  public MessageEvent(InboundMessage message) {
    this.streamId = message.getStream().getStreamId();
    this.userId = message.getUser().getUserId();
    this.messageId = message.getMessageId();
    this.timestamp = message.getTimestamp();
    this.message = message.getMessageText().trim();
    this.rawMessage = message.getMessage();
    this.data = message.getData();
    this.attachments = message.getAttachments() == null ? Collections.emptyList() :
        message.getAttachments().stream().map(MessageAttachment::new).collect(Collectors.toList());
    this.externalRecipients = message.getExternalRecipients();
    this.diagnostic = message.getDiagnostic();
    this.userAgent = message.getUserAgent();
    this.originalFormat = message.getOriginalFormat();
    this.user = new UserDetails(message.getUser());
    this.stream = new StreamDetails(message.getStream());
  }

}
