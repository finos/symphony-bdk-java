package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.template.api.Template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

/**
 * Builder for {@link Message}.
 * Allows configuring the field of the messages before constructing them with build()
 */
@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.EXPERIMENTAL)
public class MessageBuilder {

  private static final ObjectMapper MAPPER = new JsonMapper();

  private String version = "2.0";
  private String content;
  private String data;
  private Attachment attachment;

  private MessageBuilder() {

  }

  /**
   * Create a builder from a MessageML content.
   * @param   message MessageML content
   * @return  MessageBuilder with the MessageML content
   */
  public static MessageBuilder fromMessageMl(String message) {
    return new MessageBuilder().content(message);
  }

  /**
   * Create a builder from a Template and parameters.
   * @param   template a custom or built-in message template.
   * @return  MessageBuilder with the MessageML content generated from the template with the passed parameters.
   */
  public static MessageBuilder fromTemplate(Template template, Object parameters) {
    return new MessageBuilder().content(template.process(parameters));
  }

  /**
   * Create a builder from a static Template with no parameter.
   * @param   template a custom or built-in message template.
   * @return  MessageBuilder with the MessageML content generated from the template.
   */
  public static MessageBuilder fromTemplate(Template template) {
    return new MessageBuilder().content(template.process(emptyMap()));
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
   * Create a {@link Message} using the configuration within the builder.
   * @return  constructed {@link Message} using configuration within this builder.
   */
  public Message build() {
    return new Message(this);
  }

}
