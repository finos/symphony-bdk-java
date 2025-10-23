package com.symphony.bdk.core.service.message;

import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.TemplateEngine;


import org.apiguardian.api.API;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Service interface exposing OBO-enabled endpoints to manage messages.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference/messages-v4">Message API</a>
 */
@API(status = API.Status.STABLE)
public interface OboMessageService {

  /**
   * Returns the {@link TemplateEngine} that can be used to load templates from classpath or file system.
   *
   * @return the template engine
   */
  TemplateEngine templates();

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param stream     the stream where to look for messages
   * @param since      instant of the earliest possible date of the first message returned.
   * @param pagination The skip and limit for pagination.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference/messages-v4">Messages</a>
   */
  List<V4Message> listMessages(@Nonnull V4Stream stream, @Nonnull Instant since, @Nonnull PaginationAttribute pagination);

  /**
   * Get messages from an existing stream with default limit equals 50.
   * Additionally returns any attachments associated with the message.
   *
   * @param stream the stream where to look for messages
   * @param since  instant of the earliest possible date of the first message returned.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference/messages-v4">Messages</a>
   */
  List<V4Message> listMessages(@Nonnull V4Stream stream, @Nonnull Instant since);

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param streamId   the streamID where to look for messages
   * @param since      instant of the earliest possible date of the first message returned.
   * @param pagination The skip and limit for pagination.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference/messages-v4">Messages</a>
   */
  List<V4Message> listMessages(@Nonnull String streamId, @Nonnull Instant since, @Nonnull PaginationAttribute pagination);

  /**
   * Get messages from an existing stream with default limit equals 50.
   * Additionally returns any attachments associated with the message.
   *
   * @param streamId the streamID where to look for messages
   * @param since    instant of the earliest possible date of the first message returned.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference/messages-v4">Messages</a>
   */
  List<V4Message> listMessages(@Nonnull String streamId, @Nonnull Instant since);

  /**
   * Sends a message to the stream ID of the passed {@link V4Stream} object.
   *
   * @param stream  the stream to send the message to
   * @param message the MessageML content. Note: <code>&lt;messageML&gt;&lt;/messageML&gt;</code> is automatically appended if not set.
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference/create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull V4Stream stream, @Nonnull String message);

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId the ID of the stream to send the message to
   * @param message the MessageML content. Note: <code>&lt;messageML&gt;&lt;/messageML&gt;</code> is automatically appended if not set.
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference/create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull String streamId, @Nonnull String message);

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param stream    the stream to send the message to
   * @param message   the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference/create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull V4Stream stream, @Nonnull Message message);

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId    the ID of the stream to send the message to
   * @param message     the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference/create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull String streamId, @Nonnull Message message);

  /**
   * Update an existing message. The existing message must be a valid social message, that has not been deleted.
   *
   * @param messageToUpdate the message to be updated
   * @param content the update content (attachments are not supported yet)
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#update-message-v4">Create Update v4</a>
   */
  V4Message update(@Nonnull V4Message messageToUpdate, @Nonnull Message content);

  /**
   * Update an existing message. The existing message must be a valid social message, that has not been deleted.
   *
   * @param streamId the ID of the stream where the message to be updated comes from
   * @param messageId the ID of the message to be updated
   * @param content the update content (attachments are not supported yet)
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#update-message-v4">Create Update v4</a>
   */
  V4Message update(@Nonnull String streamId, @Nonnull String messageId, @Nonnull Message content);

  /**
   * Suppresses a users message based on the messageID pass in parameter.
   *
   * @param messageId   the ID of the message to suppress
   * @return a {@link MessageSuppressionResponse} object containing the details of the suppressed message
   * @see <a href="https://developers.symphony.com/restapi/reference/suppress-message">Suppress Message</a>
   */
  MessageSuppressionResponse suppressMessage(@Nonnull String messageId);

  /**
   * Retrieves a list of supported file extensions for attachments.
   *
   * @return a list of String containing all allowed file extensions for attachments
   * @see <a href="https://developers.symphony.com/restapi/reference#attachment-types">Attachment Types</a>
   */
  List<String> getAttachmentTypes();

  /**
   * Retrieves the details of a message given its message ID.
   *
   * @param messageId the ID of the message to be retrieved
   * @return a {@link V4Message} containing the message's details, null if the message was not found
   * @see <a href="https://developers.symphony.com/restapi/reference#get-message-v1">Get Message v1</a>
   */
  V4Message getMessage(@Nonnull String messageId);
}
