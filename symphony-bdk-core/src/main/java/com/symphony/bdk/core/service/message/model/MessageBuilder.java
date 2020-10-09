package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.Template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

import java.io.InputStream;

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

  private final MessageService messageService;
  private String version = "2.0";
  private String content;
  private String data;
  private Attachment attachment;

  public MessageBuilder(MessageService messageService) {
    this.messageService = messageService;
  }

  /**
   * Add messageML content to the message.
   *
   * @param   message    messageML.
   * @return  this builder with the content configured.
   */
  public MessageBuilder messageML(String message) {
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
   * Add data to the message.
   * @param   data Data json node.
   * @return  this builder with the data configured.
   */
  public MessageBuilder data(JsonNode data) {
    try {
      this.data = MAPPER.writeValueAsString(data);
      return this;
    } catch (JsonProcessingException e) {
      throw new MessageCreationException("Failed to parse data to Json string", e);
    }
  }

  public MessageBuilder attachment(InputStream inputStream, String filename) {
    this.attachment = new Attachment().inputStream(inputStream).filename(filename);
    return this;
  }

  /**
   * Create a {@link Message} using the configuration within the builder.
   * @return  constructed {@link Message} using configuration within this builder.
   */
  public Message build() {
    return new Message(this);
  }

  /**
   * Build and send a {@link Message} using the {@link MessageService}.
   *
   * @param stream the stream where the message is sent to.
   * @return a {@link V4Message} object containing the details of the sent message.
   */
  public V4Message send(V4Stream stream) {
    return this.messageService.send(stream, this.build());
  }

  /**
   * Build and send a {@link Message} using the {@link MessageService}.
   *
   * @param streamId the id of the stream where the message is sent to.
   * @return a {@link V4Message} object containing the details of the sent message.
   */
  public V4Message send(String streamId) {
    return this.messageService.send(streamId, this.build());
  }

}
