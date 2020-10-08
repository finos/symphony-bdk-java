package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.template.api.Template;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.EXPERIMENTAL)
public class Message {

  private String content;
  private String data;
  private Attachment attachment;

  private Message() {

  }

  public static Message fromMessageMl(String message) {
    return new Message().content(message);
  }

  public static Message fromTemplate(Template template, Object parameters) {
    return new Message().content(template.process(parameters));
  }

  public static Message fromTemplate(Template template) {
    return new Message().content(template.process(emptyMap()));
  }
}
