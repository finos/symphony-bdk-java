package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.template.api.Template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link Message}.
 * Allows configuring the field of the messages before constructing them with build()
 */
@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.STABLE)
public class MessageBuilder {

  private static final ObjectMapper MAPPER = new JsonMapper();

  private String version = "2.0";
  private String content;
  private String data;
  private List<Attachment> attachments = new ArrayList<>();
  @Setter(value = AccessLevel.PRIVATE) private List<Attachment> previews = new ArrayList<>();

  /**
   * Add messageML content to the message.
   *
   * @param   message    messageML.
   * @return  this builder with the content configured.
   */
  public MessageBuilder content(String message) {
    this.content = message;
    return this;
  }

  /**
   * Add content from a template to the message.
   *
   * @param   template    a custom or built-in template.
   * @param   parameters  parameters to be used in the template.
   * @return  this builder with the content configured.
   */
  public MessageBuilder template(Template template, Object parameters) {
    this.content = template.process(parameters);
    return this;
  }

  /**
   * Add content from a static template to the message.
   *
   * @param   template a custom or built-in template.
   * @return  this builder with the content configured.
   */
  public MessageBuilder template(Template template) {
    this.content = template.process(emptyMap());
    return this;
  }

  /**
   * Add data to the message.
   * @param   data Serializable data object.
   * @return  this builder with the data configured.
   */
  public MessageBuilder data(Object data) {
    try {
      this.data = MAPPER.writeValueAsString(data);
      return this;
    } catch (JsonProcessingException e) {
      throw new MessageCreationException("Failed to parse data to Json string", e);
    }
  }

  /**
   * Add attachment to the message.
   * @param content Attachment content.
   * @param filename Filename of the attachment.
   * @return  this builder with the data configured.
   */
  public MessageBuilder addAttachment(InputStream content, String filename) {
    this.attachments.add(new Attachment(content, filename));
    return this;
  }

  /**
   * Add attachment to the message.
   * @param attachment Input stream of the attachment content.
   * @param preview Optional attachment preview.
   * @param filename Filename of the attachment.
   * @return  this builder with the data configured.
   */
  public MessageBuilder addAttachment(InputStream attachment, InputStream preview, String filename) {
    this.attachments.add(new Attachment(attachment, filename));
    this.previews.add(new Attachment(preview, "preview-" + filename));
    return this;
  }

  /**
   * Create a {@link Message} using the configuration within the builder.
   * @return  constructed {@link Message} using configuration within this builder.
   */
  public Message build() {
    return new Message(this);
  }
}
