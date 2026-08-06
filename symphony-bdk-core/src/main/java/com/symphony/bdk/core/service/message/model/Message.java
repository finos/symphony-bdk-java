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
import lombok.extern.slf4j.Slf4j;
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
   * @see <a href="https://docs.developers.symphony.com/building-bots-on-symphony/messages/overview-of-messageml/entities/structured-objects">Structured Objects</a> for a description of the format.
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
  /**
   * Optional boolean flag. Used in message update api only.
   * If true, the message is updated as read already, otherwise it is unread. The default value is true.
   * @since Agent 20.14
   */
  private final Boolean silent;
  /**
   * Optional boolean flag to enable beta features in MessageML.
   * When set to true, the messageML wrapper will include the beta="true" attribute,
   * enabling experimental features like sym-ai-context.
   */
  private final Boolean beta;

  Message(final MessageBuilder builder) {
    this.content = builder.content();
    this.version = builder.version();
    this.data = builder.data();
    this.attachments = builder.attachments();
    this.previews = builder.previews();
    this.silent = builder.silent();
    this.beta = builder.beta();
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
  @Slf4j
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
    private Boolean silent = Boolean.TRUE;
    private Boolean beta;
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

    public MessageBuilder silent(@Nonnull Boolean silent) {
      this.silent = silent;
      return this;
    }

    /**
     * Enable beta mode for the message.
     * When set to true, the messageML wrapper will include the beta="true" attribute,
     * enabling experimental features like sym-ai-context.
     *
     * @param beta true to enable beta features, false or null to disable.
     * @return this builder with beta configured.
     */
    public MessageBuilder beta(Boolean beta) {
      this.beta = beta;
      return this;
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
      // content is mandatory
      if (StringUtils.isEmpty(this.content)) {
        throw new MessageCreationException("Message content is mandatory.");
      }

      // check if content is encapsulated in <messageML/> node
      String trimmedContent = this.content.trim();
      boolean hasStartTag = trimmedContent.startsWith("<messageML");
      boolean hasEndTag = trimmedContent.endsWith("</messageML>");
      if (hasStartTag != hasEndTag) {
        throw new MessageCreationException(
            "Malformed <messageML> tag. Missing " + (hasStartTag ? "closing" : "opening") + " tag");
      }
      if (!hasStartTag) {
        log.trace("Processing content to prefix with <messageML> and suffix with </messageML>");
        if (Boolean.TRUE.equals(this.beta)) {
          this.content = "<messageML beta=\"true\">" + this.content + "</messageML>";
        } else {
          this.content = "<messageML>" + this.content + "</messageML>";
        }
      } else if (Boolean.TRUE.equals(this.beta) && this.content.startsWith("<messageML>")) {
        // beta=true but content is already wrapped without beta attribute
        throw new MessageCreationException(
            "Cannot set beta=true when content is already wrapped with <messageML> without the beta attribute. "
            + "Either remove the wrapper or include beta=\"true\" in your messageML tag.");
      } else {
        // content is already wrapped, but we can trim if needed
        this.content = trimmedContent;
      }

      // check done below because it will rejected by the agent otherwise
      if (!this.previews.isEmpty() && this.previews.size() != this.attachments().size()) {
        throw new MessageCreationException("Message should contain either no preview or as many previews as attachments");
      }

      return new Message(this);
    }
  }

  private boolean isAlreadyWrapped(String content) {
    boolean hasStart = content.startsWith("<messageML>");
    boolean hasEnd = content.endsWith("</messageML>");

    if (hasStart != hasEnd) {
      throw new IllegalStateException("Malformed MessageML: missing " + (hasStart ? "closing" : "opening") + " tag.");
    }

    return hasStart; // Since they are equal, if hasStart is true, both are true.
  }
}
