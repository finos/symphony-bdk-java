package com.symphony.bdk.core.service;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.TemplateException;
import com.symphony.bdk.template.api.TemplateResolver;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

@API(status = API.Status.STABLE)
public class OboMessageService {

  protected final MessagesApi messagesApi;
  protected final AuthSession authSession;
  protected final RetryWithRecoveryBuilder<?> retryBuilder;
  protected final TemplateResolver templateResolver;

  public OboMessageService(MessagesApi messagesApi, AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder,
      TemplateResolver templateResolver) {
    this.messagesApi = messagesApi;
    this.authSession = authSession;
    this.retryBuilder = retryBuilder;
    this.templateResolver = templateResolver;
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

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, supplier);
  }
}
