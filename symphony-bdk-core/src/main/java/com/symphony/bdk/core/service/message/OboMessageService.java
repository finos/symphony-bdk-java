package com.symphony.bdk.core.service.message;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.message.model.Attachment;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.util.TypeReference;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Service class for managing messages. This exposed OBO-enabled endpoints only.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Message API</a>
 */
@API(status = API.Status.STABLE)
public class OboMessageService {

  protected final MessagesApi messagesApi;
  protected final AuthSession authSession;
  private final TemplateEngine templateEngine;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public OboMessageService(MessagesApi messagesApi, AuthSession authSession, TemplateEngine templateEngine,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.messagesApi = messagesApi;
    this.authSession = authSession;
    this.templateEngine = templateEngine;
    this.retryBuilder = retryBuilder;
  }

  /**
   * Returns the {@link TemplateEngine} that can be used to load templates from classpath or file system.
   *
   * @return the template engine
   */
  public TemplateEngine templates() {
    return this.templateEngine;
  }

  /**
   * Sends a message to the stream ID of the passed {@link V4Stream} object.
   *
   * @param stream  the stream to send the message to
   * @param message the message payload in MessageML
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   * @deprecated this method will be replaced by {@link MessageService#send(V4Stream, Message)}
   */
  @Deprecated
  @API(status = API.Status.DEPRECATED)
  public V4Message send(@Nonnull V4Stream stream, @Nonnull String message) {
    return send(stream.getStreamId(), message);
  }

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId the ID of the stream to send the message to
   * @param message  the message payload in MessageML
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   * @deprecated this method will be replaced by {@link MessageService#send(String, Message)}
   */
  @Deprecated
  @API(status = API.Status.DEPRECATED)
  public V4Message send(@Nonnull String streamId, @Nonnull String message) {
    return this.send(streamId, Message.builder().content(message).build());
  }

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param stream    the stream to send the message to
   * @param message   the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  public V4Message send(@Nonnull V4Stream stream, @Nonnull Message message) {
    return this.send(stream.getStreamId(), message);
  }

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId    the ID of the stream to send the message to
   * @param message     the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  public V4Message send(@Nonnull String streamId, @Nonnull Message message) {
    return this.executeAndRetry("send", () ->
        this.doSend(streamId, message, this.authSession.getSessionToken(), this.authSession.getKeyManagerToken())
    );
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, supplier);
  }

  /**
   * The generated {@link MessagesApi#v4StreamSidMessageCreatePost(String, String, String, String, String, String, File, File)}
   * does not allow to send multiple attachments as well as in-memory files, so we have to "manually" process this call.
   */
  private V4Message doSend(String streamId, Message message, String sessionToken, String keyManagerToken) throws
      ApiException {
    final ApiClient apiClient = this.messagesApi.getApiClient();
    final Map<String, Object> form = new HashMap<>();
    form.put("message", message.getContent());
    form.put("data", message.getData());
    form.put("version", message.getVersion());
    form.put("attachment", toApiClientBodyParts(message.getAttachments()));
    form.put("preview", toApiClientBodyParts(message.getPreviews()));

    final Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", apiClient.parameterToString(sessionToken));
    headers.put("keyManagerToken", apiClient.parameterToString(keyManagerToken));

    return apiClient.invokeAPI(
        "/v4/stream/" + apiClient.escapeString(streamId) + "/message/create",
        "POST",
        emptyList(),
        null, // for 'multipart/form-data', body can be null
        headers,
        emptyMap(),
        form,
        apiClient.selectHeaderAccept("application/json"),
        apiClient.selectHeaderContentType("multipart/form-data"),
        new String[0],
        new TypeReference<V4Message>() {}
    ).getData();
  }

  private static ApiClientBodyPart[] toApiClientBodyParts(List<Attachment> attachments) {
    return attachments.stream()
        .map(a -> new ApiClientBodyPart(a.getContent(), a.getFilename()))
        .toArray(ApiClientBodyPart[]::new);
  }
}
