package com.symphony.bdk.core.service.message.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.EXPERIMENTAL)
public class Message {

  private String content;
  private String data;
  private Attachment attachment;

  protected Message(MessageBuilder builder) {
    this.content = builder.content();
    this.data = builder.data();
    this.attachment = builder.attachment();
  }

}
