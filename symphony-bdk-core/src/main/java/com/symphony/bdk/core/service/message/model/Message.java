package com.symphony.bdk.core.service.message.model;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;
import com.symphony.bdk.gen.api.model.V4Stream;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.List;

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

    if (StringUtils.isEmpty(builder.content())) {
      throw new MessageCreationException("Message content is mandatory.");
    }

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
}
