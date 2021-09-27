package com.symphony.bdk.core.service.message;

import static com.symphony.bdk.core.util.IdUtil.toUrlSafeIdIfNeeded;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.equalsAny;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.message.model.Attachment;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.message.model.SortDir;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MessageMetadataResponse;
import com.symphony.bdk.gen.api.model.MessageReceiptDetailResponse;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.MessageStatus;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageBlastResponse;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.util.ApiUtils;
import com.symphony.bdk.http.api.util.TypeReference;
import com.symphony.bdk.template.api.TemplateEngine;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service class for managing messages.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Message API</a>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class MessageService implements OboMessageService, OboService<OboMessageService> {

  private final MessagesApi messagesApi;
  private final MessageApi messageApi;
  private final MessageSuppressionApi messageSuppressionApi;
  private final StreamsApi streamsApi;
  private final PodApi podApi;
  private final AttachmentsApi attachmentsApi;
  private final DefaultApi defaultApi;
  private final AuthSession authSession;
  private final TemplateEngine templateEngine;
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
    this.defaultApi = defaultApi;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public MessageService(
      final MessagesApi messagesApi,
      final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi,
      final StreamsApi streamsApi,
      final PodApi podApi,
      final AttachmentsApi attachmentsApi,
      final DefaultApi defaultApi,
      final TemplateEngine templateEngine,
      final RetryWithRecoveryBuilder<?> retryBuilder
  ) {
    this.messagesApi = messagesApi;
    this.messageApi = messageApi;
    this.messageSuppressionApi = messageSuppressionApi;
    this.streamsApi = streamsApi;
    this.podApi = podApi;
    this.attachmentsApi = attachmentsApi;
    this.authSession = null;
    this.templateEngine = templateEngine;
    this.defaultApi = defaultApi;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  @Override
  public OboMessageService obo(AuthSession oboSession) {
    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, oboSession, templateEngine, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TemplateEngine templates() {
    return this.templateEngine;
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param stream     the stream where to look for messages
   * @param since      instant of the earliest possible date of the first message returned.
   * @param pagination The skip and limit for pagination.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> listMessages(@Nonnull V4Stream stream, @Nonnull Instant since,
      @Nonnull PaginationAttribute pagination) {
    return listMessages(stream.getStreamId(), since, pagination);
  }

  /**
   * Get messages from an existing stream with default limit equals 50.
   * Additionally returns any attachments associated with the message.
   *
   * @param stream the stream where to look for messages
   * @param since  instant of the earliest possible date of the first message returned.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> listMessages(@Nonnull V4Stream stream, @Nonnull Instant since) {
    return listMessages(stream.getStreamId(), since);
  }

  /**
   * Get messages from an existing stream. Additionally returns any attachments associated with the message.
   *
   * @param streamId   the streamID where to look for messages
   * @param since      instant of the earliest possible date of the first message returned.
   * @param pagination The skip and limit for pagination.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> listMessages(@Nonnull String streamId, @Nonnull Instant since,
      @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("getMessages", messageApi.getApiClient().getBasePath(),
        () -> messagesApi.v4StreamSidMessageGet(toUrlSafeIdIfNeeded(streamId), getEpochMillis(since),
            authSession.getSessionToken(), authSession.getKeyManagerToken(), pagination.getSkip(),
            pagination.getLimit()));
  }

  /**
   * Get messages from an existing stream with default limit equals 50.
   * Additionally returns any attachments associated with the message.
   *
   * @param streamId the streamID where to look for messages
   * @param since    instant of the earliest possible date of the first message returned.
   * @return the list of matching messages in the stream.
   * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Messages</a>
   */
  public List<V4Message> listMessages(@Nonnull String streamId, @Nonnull Instant since) {
    return executeAndRetry("getMessages", messageApi.getApiClient().getBasePath(),
        () -> messagesApi.v4StreamSidMessageGet(toUrlSafeIdIfNeeded(streamId), getEpochMillis(since),
            authSession.getSessionToken(), authSession.getKeyManagerToken(), null, null));
  }

  /**
   * Searches for messages in the context of a specified user, given an argument-based query.
   *
   * @param query the search query arguments
   * @return the list of matching messages
   * @see <a href="https://developers.symphony.com/restapi/reference#message-search-post">Message Search (using POST)</a>
   */
  public List<V4Message> searchMessages(@Nonnull MessageSearchQuery query) {
    return this.searchMessages(query, null);
  }

  /**
   * Searches for messages in the context of a specified user, given an argument-based query.
   *
   * @param query      The search query arguments
   * @param pagination The skip and limit for pagination. The maximum limit value is 1000;
   * @return the list of matching messages
   * @see <a href="https://developers.symphony.com/restapi/reference#message-search-post">Message Search (using POST)</a>
   */
  public List<V4Message> searchMessages(@Nonnull MessageSearchQuery query, @Nullable PaginationAttribute pagination) {
    return this.searchMessages(query, pagination, null);
  }

  /**
   * Searches for messages in the context of a specified user, given an argument-based query.
   *
   * @param query      The search query arguments
   * @param pagination The skip and limit for pagination. The maximum limit value is 1000.
   * @param sortDir    Sorting direction for response. Possible values are desc (default) and asc.
   * @return the list of matching messages
   * @see <a href="https://developers.symphony.com/restapi/reference#message-search-post">Message Search (using POST)</a>
   */
  public List<V4Message> searchMessages(@Nonnull MessageSearchQuery query, @Nullable PaginationAttribute pagination,
      @Nullable SortDir sortDir) {
    validateMessageSearchQuery(query);
    return executeAndRetry("searchMessages", messageApi.getApiClient().getBasePath(),
        () -> messagesApi.v1MessageSearchPost(authSession.getSessionToken(), authSession.getKeyManagerToken(), query,
            pagination != null ? pagination.getSkip() : null,
            pagination != null ? pagination.getLimit() : null,
            // 'scope' argument is not documented for POST search, but is for GET:
            // "Specifies against which connector to perform the search, either Symphony content or one connector.".
            // We will assume (for now) that it as no real usage.
            null,
            sortDir != null ? sortDir.name().toLowerCase() : null)
    );
  }

  private static void validateMessageSearchQuery(@Nonnull MessageSearchQuery query) {

    // checks streamType value among accepted ones
    if (query.getStreamType() != null && !equalsAny(query.getStreamType(), "CHAT", "IM", "MIM", "ROOM", "POST")) {
      String errorMsg = "Wrong stream type '" + query.getStreamType() +
          "'. Accepted values are: CHAT (1-1 instant messages and multi-party instant messages), "
          + "IM (1-1 instant message), MIM (multi-party instant message), ROOM or POST (user profile wall posts)";
      throw new IllegalArgumentException(errorMsg);
    }

    // text queries require streamId to be provided
    if (query.getText() != null && query.getStreamId() == null) {
      throw new IllegalArgumentException("Message text queries require a streamId to be provided.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V4Message send(@Nonnull V4Stream stream, @Nonnull String message) {
    return send(stream.getStreamId(), message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V4Message send(@Nonnull String streamId, @Nonnull String message) {
    return this.send(toUrlSafeIdIfNeeded(streamId), Message.builder().content(message).build());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V4Message send(@Nonnull V4Stream stream, @Nonnull Message message) {
    return this.send(stream.getStreamId(), message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V4Message send(@Nonnull String streamId, @Nonnull Message message) {
    return this.executeAndRetry("send", messagesApi.getApiClient().getBasePath(),
        () -> this.doSendMessage(streamId, message));
  }

  /**
   * Update an existing message. The existing message must be a valid social message, that has not been deleted.
   *
   * @param streamId the ID of the stream to send the message to
   * @param messageId the ID of the message to be
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#update-message-v4">Create Message v4</a>
   */
  public V4Message update(@Nonnull String streamId, @Nonnull String messageId, @Nonnull Message message) {
    return this.executeAndRetry("update", messagesApi.getApiClient().getBasePath(), () ->  {
      final String path = String.format(
          "/v4/stream/%s/message/%s/update",
          this.messagesApi.getApiClient().escapeString(toUrlSafeIdIfNeeded(streamId)),
          this.messagesApi.getApiClient().escapeString(toUrlSafeIdIfNeeded(messageId))
      );
      return doSendFormData(path, getForm(message), new TypeReference<V4Message>() {});
    });
  }

  /**
   * Sends a message to multiple existing streams.
   *
   * @param streamIds the list of stream IDs to send the message to
   * @param message   the message to be sent
   * @return a {@link V4MessageBlastResponse} object containing the details of the sent messages
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#blast-message">Blast Message</a>
   */
  public V4MessageBlastResponse send(@Nonnull List<String> streamIds, @Nonnull Message message) {
    return this.executeAndRetry("sendBlast", messagesApi.getApiClient().getBasePath(),
        () -> doSendBlast(streamIds, message));
  }

  /**
   * The generated {@link MessagesApi#v4StreamSidMessageCreatePost(String, String, String, String, String, String, File, File)}
   * does not allow to send multiple attachments as well as in-memory files, so we have to "manually" process this call.
   */
  private V4Message doSendMessage(@Nonnull String streamId, @Nonnull Message message) throws ApiException {
    final String path =
        "/v4/stream/" + this.messagesApi.getApiClient().escapeString(toUrlSafeIdIfNeeded(streamId)) + "/message/create";

    return doSendFormData(path, getForm(message), new TypeReference<V4Message>() {});
  }

  /**
   * The generated {@link MessagesApi#v4MessageBlastPost(String, List, String, String, String, String, File, File)}
   * does not allow to send multiple attachments as well as in-memory files, so we have to "manually" process this call.
   */
  private V4MessageBlastResponse doSendBlast(@Nonnull List<String> streamIds, @Nonnull Message message)
      throws ApiException {
    final Map<String, Object> form = getForm(message);
    form.put("sids", String.join(",", streamIds));

    return doSendFormData("/v4/message/blast", form, new TypeReference<V4MessageBlastResponse>() {});
  }

  private Map<String, Object> getForm(Message message) {
    final Map<String, Object> form = new HashMap<>();
    form.put("message", message.getContent());
    form.put("data", message.getData());
    form.put("version", message.getVersion());
    form.put("attachment", toApiClientBodyParts(message.getAttachments()));
    form.put("preview", toApiClientBodyParts(message.getPreviews()));
    return form;
  }

  private static ApiClientBodyPart[] toApiClientBodyParts(List<Attachment> attachments) {
    return attachments.stream()
        .map(a -> new ApiClientBodyPart(a.getContent(), a.getFilename()))
        .toArray(ApiClientBodyPart[]::new);
  }

  private <T> T doSendFormData(String path, Map<String, Object> form, TypeReference<T> typeReference)
      throws ApiException {
    final ApiClient apiClient = this.messagesApi.getApiClient();

    final Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", apiClient.parameterToString(this.authSession.getSessionToken()));
    headers.put("keyManagerToken", apiClient.parameterToString(this.authSession.getKeyManagerToken()));

    return apiClient.invokeAPI(
        path,
        "POST",
        emptyList(),
        null, // for 'multipart/form-data', body can be null
        headers,
        emptyMap(),
        form,
        apiClient.selectHeaderAccept("application/json"),
        apiClient.selectHeaderContentType("multipart/form-data"),
        new String[0],
        typeReference
    ).getData();
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
    return executeAndRetry("getAttachment", attachmentsApi.getApiClient().getBasePath(),
        () -> attachmentsApi.v1StreamSidAttachmentGet(toUrlSafeIdIfNeeded(streamId), attachmentId, messageId,
            authSession.getSessionToken(), authSession.getKeyManagerToken()));
  }

  /**
   * Imports a list of messages to Symphony
   *
   * @param messages the list of messages to be imported
   * @return the list of imported messages
   * @see <a href="https://developers.symphony.com/restapi/reference#import-message-v4">Import Message</a>
   */
  public List<V4ImportResponse> importMessages(@Nonnull List<V4ImportedMessage> messages) {
    return executeAndRetry("importMessages", messagesApi.getApiClient().getBasePath(),
        () -> messagesApi.v4MessageImportPost(authSession.getSessionToken(), authSession.getKeyManagerToken(),
            messages));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MessageSuppressionResponse suppressMessage(@Nonnull String messageId) {
    return executeAndRetry("suppressMessage", messageSuppressionApi.getApiClient().getBasePath(),
        () -> messageSuppressionApi.v1AdminMessagesuppressionIdSuppressPost(messageId, authSession.getSessionToken()));
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
    return executeAndRetry("getMessageStatus", messageApi.getApiClient().getBasePath(),
        () -> messageApi.v1MessageMidStatusGet(toUrlSafeIdIfNeeded(messageId), authSession.getSessionToken()));
  }

  /**
   * Retrieves a list of supported file extensions for attachments.
   *
   * @return a list of String containing all allowed file extensions for attachments
   * @see <a href="https://developers.symphony.com/restapi/reference#attachment-types">Attachment Types</a>
   */
  public List<String> getAttachmentTypes() {
    return executeAndRetry("getAttachmentTypes", podApi.getApiClient().getBasePath(),
        () -> podApi.v1FilesAllowedTypesGet(authSession.getSessionToken()));
  }

  /**
   * Retrieves the details of a message given its message ID.
   *
   * @param messageId the ID of the message to be retrieved
   * @return a {@link V4Message} containing the message's details, null if the message was not found
   * @see <a href="https://developers.symphony.com/restapi/reference#get-message-v1">Get Message v1</a>
   */
  public V4Message getMessage(@Nonnull String messageId) {
    return executeAndRetry("getMessage", messagesApi.getApiClient().getBasePath(),
        () -> messagesApi.v1MessageIdGet(authSession.getSessionToken(), authSession.getKeyManagerToken(),
            toUrlSafeIdIfNeeded(messageId)));
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
  public List<StreamAttachmentItem> listAttachments(@Nonnull String streamId, @Nullable Instant since,
      @Nullable Instant to, @Nullable Integer limit, @Nullable AttachmentSort sort) {
    final String sortDir = sort == null ? AttachmentSort.ASC.name() : sort.name();

    return executeAndRetry("listAttachments", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1StreamsSidAttachmentsGet(toUrlSafeIdIfNeeded(streamId), authSession.getSessionToken(),
            getEpochMillis(since), getEpochMillis(to), limit, sortDir));
  }

  /**
   * Fetches receipts details from a specific message.
   *
   * @param messageId the ID of the message to get receipt details from.
   * @return a {@link MessageReceiptDetailResponse} object holding all receipt information.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-message-receipts">List Message Receipts</a>
   */
  public MessageReceiptDetailResponse listMessageReceipts(@Nonnull String messageId) {
    return executeAndRetry("listMessageReceipts", defaultApi.getApiClient().getBasePath(), () ->
        defaultApi.v1AdminMessagesMessageIdReceiptsGet(authSession.getSessionToken(), toUrlSafeIdIfNeeded(messageId), null,
            null));
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
    return executeAndRetry("getMessageRelationships", defaultApi.getApiClient().getBasePath(),
        () -> defaultApi.v1AdminMessagesMessageIdMetadataRelationshipsGet(
            authSession.getSessionToken(), ApiUtils.getUserAgent(), toUrlSafeIdIfNeeded(messageId)));
  }

  private static Long getEpochMillis(Instant instant) {
    return instant == null ? null : instant.toEpochMilli();
  }

  private <T> T executeAndRetry(String name, String address, SupplierWithApiException<T> supplier) {
    checkAuthSession(authSession);
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, address, supplier);
  }
}
