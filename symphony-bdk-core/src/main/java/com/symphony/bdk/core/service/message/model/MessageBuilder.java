package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.template.api.Template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * {@link Message} class builder. Accessible via {@link Message#builder()}.
 */
@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
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
  public MessageBuilder content(@Nonnull String message) {
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
  public MessageBuilder template(@Nonnull Template template, @Nonnull Object parameters) {
    this.content = template.process(parameters);
    return this;
  }

  /**
   * Add content from a static template to the message.
   *
   * @param   template a custom or built-in template.
   * @return  this builder with the content configured.
   */
  public MessageBuilder template(@Nonnull Template template) {
    return this.template(template, emptyMap());
  }

  /**
   * Add data to the message.
   * @param   data Serializable data object.
   * @return  this builder with the data configured.
   */
  public MessageBuilder data(@Nonnull Object data) {
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
  public MessageBuilder addAttachment(@Nonnull InputStream content, @Nonnull String filename) {
    this.attachments.add(new Attachment(content, filename));
    return this;
  }

  /**
   * Add attachment (with preview) to the message.
   * @param attachment Input stream of the attachment content.
   * @param preview Optional attachment preview.
   * @param filename Filename of the attachment.
   * @return  this builder with the data configured.
   */
  public MessageBuilder addAttachment(@Nonnull InputStream attachment, @Nonnull InputStream preview, @Nonnull String filename) {
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
