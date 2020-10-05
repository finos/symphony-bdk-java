package com.symphony.bdk.core.service;


import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.pagination.PaginatedApi;
import com.symphony.bdk.core.service.pagination.PaginatedService;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MessageIdsFromStream;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.http.api.util.ApiUtils;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;
import com.symphony.bdk.template.api.TemplateResolver;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

/**
 * Service class for managing messages.
 * See the MESSAGES part of the <a href="https://developers.symphony.com/restapi/reference">REST API reference</a>.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class MessageService {

  private final MessagesApi messagesApi;
  private final MessageApi messageApi;
  private final MessageSuppressionApi messageSuppressionApi;
  private final StreamsApi streamsApi;
  private final PodApi podApi;
  private final AttachmentsApi attachmentsApi;
  private final DefaultApi defaultApi;
  private final AuthSession authSession;
  private final TemplateEngine templateEngine;
  private final TemplateResolver templateResolver;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public MessageService(
      final MessagesApi messagesApi,
      final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi,
      final StreamsApi streamsApi,
      final PodApi podApi,
      final AttachmentsApi attachmentsApi,
      final DefaultApi defaultApi,
      final AuthSession authSession,
      final TemplateEngine templateEngine,
      final RetryWithRecoveryBuilder<?> retryBuilder
  ) {
    this.messagesApi = messagesApi;
    this.messageApi = messageApi;
    this.messageSuppressionApi = messageSuppressionApi;
    this.streamsApi = streamsApi;
    this.podApi = podApi;
    this.attachmentsApi = attachmentsApi;
    this.authSession = authSession;
    this.templateEngine = templateEngine;
    this.templateResolver = new TemplateResolver(templateEngine);
    this.defaultApi = defaultApi;
    this.retryBuilder = retryBuilder;
  }

  /**
   * Returns the {@link TemplateEngine} that can be used to load templates from classpath or file system.
   *
   * @return the template engine
   */
  public TemplateEngine templates() {
    return templateEngine;
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param stream the stream where to look for messages
   * @param since  instant of the earliest possible date of the first message returned.
   * @param skip   number of messages to skip. Optional and defaults to 0.
   * @param limit  maximum number of messages to return. Optional and defaults to 50.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> getMessages(@Nonnull V4Stream stream, @Nonnull Instant since, Integer skip, Integer limit) {
    return getMessages(stream.getStreamId(), since, skip, limit);
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param streamId the streamID where to look for messages
   * @param since    instant of the earliest possible date of the first message returned.
   * @param skip     number of messages to skip. Optional and defaults to 0.
   * @param limit    maximum number of messages to return. Optional and defaults to 50.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> getMessages(@Nonnull String streamId, @Nonnull Instant since, Integer skip, Integer limit) {
    return executeAndRetry("getMessages", () -> messagesApi.v4StreamSidMessageGet(streamId, getEpochMillis(since),
        authSession.getSessionToken(), authSession.getKeyManagerToken(), skip, limit));
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param stream    the stream where to look for messages
   * @param since     instant of the earliest possible date of the first message returned.
   * @param chunkSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @param totalSize total maximum number of messages to return. Optional and defaults to 50.
   * @return a {@link Stream} of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public Stream<V4Message> getMessagesStream(@Nonnull V4Stream stream, @Nonnull Instant since, Integer chunkSize,
      Integer totalSize) {
    return getMessagesStream(stream.getStreamId(), since, chunkSize, totalSize);
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param streamId  the streamID where to look for messages
   * @param since     instant of the earliest possible date of the first message returned.
   * @param chunkSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @param totalSize total maximum number of messages to return. Optional and defaults to 50.
   * @return a {@link Stream} of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public Stream<V4Message> getMessagesStream(@Nonnull String streamId, @Nonnull Instant since, Integer chunkSize,
      Integer totalSize) {
    PaginatedApi<V4Message> api = ((offset, limit) -> getMessages(streamId, since, offset, limit));

    final int actualChunkSize = chunkSize == null ? 50 : chunkSize.intValue();
    final int actualTotalSize = totalSize == null ? 50 : totalSize.intValue();

    return new PaginatedService<>(api, actualChunkSize, actualTotalSize).stream();
  }

  /**
   * Sends a message to the stream ID of the passed {@link V4Stream} object.
   *
   * @param stream  the stream to send the message to
   * @param message the message payload in MessageML
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  public V4Message send(@Nonnull V4Stream stream, @Nonnull String message) {
    return send(stream.getStreamId(), message);
  }

  /**
   * Sends a templated to the stream ID of the passed {@link V4Stream} object.
   *
   * @param stream     the stream to send the message to
   * @param template   the template name to be used to produce the message
   * @param parameters the parameters to pass to the template to produce the message to be sent
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  public V4Message send(@Nonnull V4Stream stream, @Nonnull String template, Object parameters)
      throws TemplateException {
    return send(stream.getStreamId(), templateResolver.resolve(template).process(parameters));
  }

  /**
   * Sends a templated to the stream ID passed in parameter.
   *
   * @param streamId   the ID of the stream to send the message to
   * @param template   the template name to be used to produce the message
   * @param parameters the parameters to pass to the template to produce the message to be sent
   * @return a {@link V4Message} object containing the details of the sent message
   * @throws TemplateException
   */
  public V4Message send(@Nonnull String streamId, @Nonnull String template, Object parameters)
      throws TemplateException {
    return send(streamId, templateResolver.resolve(template).process(parameters));
  }

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId the ID of the stream to send the message to
   * @param message  the message payload in MessageML
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  public V4Message send(@Nonnull String streamId, @Nonnull String message) {
    return executeAndRetry("send", () -> messagesApi.v4StreamSidMessageCreatePost(
        streamId,
        authSession.getSessionToken(),
        authSession.getKeyManagerToken(),
        message,
        null,
        null,
        null,
        null
    ));
  }

  /**
   * Downloads the attachment body by the stream ID, message ID and attachment ID.
   *
   * @param streamId     the stream ID where to look for the attachment
   * @param messageId    the ID of the message containing the attachment
   * @param attachmentId the ID of the attachment
   * @return a byte array of attachment encoded in base 64
   * @see <a href="https://developers.symphony.com/restapi/reference#attachment">Attachment</a>
   */
  public byte[] getAttachment(@Nonnull String streamId, @Nonnull String messageId, @Nonnull String attachmentId) {
    return executeAndRetry("getAttachment",
        () -> attachmentsApi.v1StreamSidAttachmentGet(streamId, attachmentId, messageId,
            authSession.getSessionToken(), authSession.getKeyManagerToken()));
  }

  /**
   * Imports a list of messages to Symphony
   *
   * @param messages the list of messages to be imported
   * @return the list of imported messages
   * @see <a href="https://developers.symphony.com/restapi/reference#import-message-v4">Import Message</a>
   */
  public List<V4ImportResponse> importMessages(List<V4ImportedMessage> messages) {
    return executeAndRetry("importMessages", () -> messagesApi.v4MessageImportPost(authSession.getSessionToken(),
        authSession.getKeyManagerToken(), messages));
  }

  /**
   * Suppresses a message, preventing its contents from being displayed to users.
   *
   * @param messageId the ID of the message to suppress.
   * @return a {@link MessageSuppressionResponse} instance containing information about the message suppression.
   * @see <a href="https://developers.symphony.com/restapi/reference#suppress-message">Suppress Message</a>
   */
  public MessageSuppressionResponse suppressMessage(@Nonnull String messageId) {
    return executeAndRetry("suppressMessage", () ->
        messageSuppressionApi.v1AdminMessagesuppressionIdSuppressPost(messageId, authSession.getSessionToken()));
  }

  /**
   * Get the status of a particular message, i.e the list of users who the message was sent to, delivered to
   * and the list of users who read the message.
   *
   * @param messageId the ID of the message to be checked
   * @return a {@link MessageStatus} instance
   * @see <a href="https://developers.symphony.com/restapi/reference#message-status">Message Status</a>
   */
  public MessageStatus getMessageStatus(@Nonnull String messageId) {
    return executeAndRetry("getMessageStatus",
        () -> messageApi.v1MessageMidStatusGet(messageId, authSession.getSessionToken()));
  }

  /**
   * Retrieves a list of supported file extensions for attachments.
   *
   * @return a list of String containing all allowed file extensions for attachments
   * @see <a href="https://developers.symphony.com/restapi/reference#attachment-types">Attachment Types</a>
   */
  public List<String> getAttachmentTypes() {
    return executeAndRetry("getAttachmentTypes", () -> podApi.v1FilesAllowedTypesGet(authSession.getSessionToken()));
  }

  /**
   * Retrieves the details of a message given its message ID.
   *
   * @param messageId the ID of the message to be retrieved
   * @return a {@link V4Message} containing the message's details, null if the message was not found
   * @see <a href="https://developers.symphony.com/restapi/reference#get-message-v1">Get Message v1</a>
   */
  public V4Message getMessage(@Nonnull String messageId) {
    return executeAndRetry("getMessage", () -> messagesApi.v1MessageIdGet(authSession.getSessionToken(),
        authSession.getKeyManagerToken(), messageId));
  }

  /**
   * List attachments in a particular stream.
   *
   * @param streamId the stream ID where to look for the attachments
   * @param since    optional instant of the first required attachment.
   * @param to       optional instant of the last required attachment.
   * @param limit    maximum number of attachments to return. This optional value defaults to 50 and should be between 0 and 100.
   * @param sort     Attachment date sort direction : ASC or DESC (default to ASC)
   * @return the list of attachments in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-attachments">List Attachments</a>
   */
  public List<StreamAttachmentItem> listAttachments(@Nonnull String streamId, Instant since, Instant to, Integer limit,
      AttachmentSort sort) {
    final String sortDir = sort == null ? AttachmentSort.ASC.name() : sort.name();

    return executeAndRetry("listAttachments", () ->
        streamsApi.v1StreamsSidAttachmentsGet(streamId, authSession.getSessionToken(), getEpochMillis(since),
            getEpochMillis(to), limit, sortDir));
  }

  /**
   * Fetches message ids using timestamp.
   *
   * @param streamId the ID of the stream where to fetch messages.
   * @param since    optional instant of the first required messageId.
   * @param to       optional instant of the last required messageId.
   * @param limit    optional maximum number of messageIds to return.
   * @param skip     optional number of messageIds to skip.
   * @return a {@link MessageIdsFromStream} object containing the list of messageIds.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-message-ids-by-timestamp">Get Message IDs by Timestamp</a>
   */
  public MessageIdsFromStream getMessageIdsByTimestamp(@Nonnull String streamId, Instant since, Instant to,
      Integer limit, Integer skip) {
    return executeAndRetry("getMessageIdsByTimestamp", () ->
        defaultApi.v2AdminStreamsStreamIdMessageIdsGet(authSession.getSessionToken(), streamId, getEpochMillis(since),
            getEpochMillis(to), limit, skip));
  }

  /**
   * Fetches message ids using timestamp.
   *
   * @param streamId  the ID of the stream where to fetch messages.
   * @param since     optional instant of the first required messageId.
   * @param to        optional instant of the last required messageId.
   * @param chunkSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @param totalSize total maximum number of messages to return. Optional and defaults to 50.
   * @return a {@link Stream} containing the messageIds.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-message-ids-by-timestamp">Get Message IDs by Timestamp</a>
   */
  public Stream<String> getMessageIdsByTimestampStream(@Nonnull String streamId, Instant since, Instant to,
      Integer chunkSize, Integer totalSize) {
    PaginatedApi<String> api = ((offset, limit) ->
        getMessageIdsByTimestamp(streamId, since, to, limit, offset).getData());

    final int actualChunkSize = chunkSize == null ? 50 : chunkSize.intValue();
    final int actualTotalSize = totalSize == null ? 50 : totalSize.intValue();

    return new PaginatedService<>(api, actualChunkSize, actualTotalSize).stream();
  }


  /**
   * Fetches receipts details from a specific message.
   *
   * @param messageId the ID of the message to get receipt details from.
   * @return a {@link MessageReceiptDetailResponse} object holding all receipt information.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-message-receipts">List Message Receipts</a>
   */
  public MessageReceiptDetailResponse listMessageReceipts(@Nonnull String messageId) {
    return executeAndRetry("listMessageReceipts", () ->
        defaultApi.v1AdminMessagesMessageIdReceiptsGet(authSession.getSessionToken(), messageId, null, null));
  }

  /**
   * Gets the message metadata relationship.
   * This API allows users to track the relationship between a message and all the forwards and replies of that message.
   *
   * @param messageId the ID of the message to get relationships from.
   * @return a {@link MessageMetadataResponse} object holding information about the current message relationships
   * (parent, replies, forwards and form replies).
   * @see <a href="https://developers.symphony.com/restapi/reference#message-metadata-relationship">Message Metadata</a>
   */
  public MessageMetadataResponse getMessageRelationships(@Nonnull String messageId) {
    return executeAndRetry("getMessageRelationships", () -> defaultApi.v1AdminMessagesMessageIdMetadataRelationshipsGet(
        authSession.getSessionToken(), ApiUtils.getUserAgent(), messageId));
  }

  private static Long getEpochMillis(Instant instant) {
    return instant == null ? null : instant.toEpochMilli();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, supplier);
  }
}
