package com.symphony.bdk.core.service.message.model;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.Template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Message model to be used in {@link com.symphony.bdk.core.service.message.MessageService#send(V4Stream, Message)}
 */
@Getter
@API(status = API.Status.STABLE)
public class Message {

  /**
   * The content of the message in MessageML v2 format. Must contain at least one space.
   */
  private final String content;
  /**
   * JSON data representing the objects contained in the message.
   * @see <a href="https://developers.symphony.com/symphony-developer/docs/objects">Structured Objects</a> for a description of the format.
   */
  private final String data;
  /**
   * One or more files to be sent along with the message.
   */
  private final List<Attachment> attachments;
  /**
   * Optional attachment preview.
   */
  private final List<Attachment> previews;
  /**
   * Optional message version in the format "major.minor". If empty, defaults to the latest supported version.
   */
  private final String version;

  Message(final MessageBuilder builder) {
    this.content = builder.content();
    this.version = builder.version();
    this.data = builder.data();
    this.attachments = builder.attachments();
    this.previews = builder.previews();
  }

  /**
   * Returns a new {@link MessageBuilder} instance.
   *
   * @return new message builder.
   */
  public static MessageBuilder builder() {
    return new MessageBuilder();
  }

  /**
   * {@link Message} class builder. Accessible via {@link Message#builder()}.
   */
  @Getter
  @Setter
  @Accessors(fluent = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @API(status = API.Status.STABLE)
  public static class MessageBuilder {

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
        throw new MessageCreationException("Failed to serialize data (" + data.getClass() + ") to Json string", e);
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
     * @throws MessageCreationException if mandatory content is empty.
     */
    public Message build() {

      if (StringUtils.isEmpty(this.content)) {
        throw new MessageCreationException("Message content is mandatory.");
      }

      return new Message(this);
    }
  }
}
