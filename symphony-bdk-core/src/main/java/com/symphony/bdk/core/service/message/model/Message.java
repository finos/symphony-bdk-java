package com.symphony.bdk.core.service.message.model;

import com.symphony.bdk.gen.api.model.V4Stream;

import lombok.Getter;
import org.apiguardian.api.API;

/**
 * Message model to be used in {@link com.symphony.bdk.core.service.message.MessageService#send(V4Stream, Message)}
 */
@Getter
@API(status = API.Status.EXPERIMENTAL)
public class Message {

  // The version of the MessageML format
  private final String version;
  private final String content;
  private final String data;
  private final Attachment attachment;

  protected Message(MessageBuilder builder) {
    this.version = builder.version();
    this.content = builder.content();
    this.data = builder.data();
    this.attachment = builder.attachment();
  }

}
