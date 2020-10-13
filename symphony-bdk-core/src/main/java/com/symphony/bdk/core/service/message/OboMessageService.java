package com.symphony.bdk.core.service.message;

import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Service interface exposing OBO-enabled endpoints to manage messages.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#messages-v4">Message API</a>
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
  V4Message send(@Nonnull V4Stream stream, @Nonnull String message);

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
  V4Message send(@Nonnull String streamId, @Nonnull String message);

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param stream    the stream to send the message to
   * @param message   the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull V4Stream stream, @Nonnull Message message);

  /**
   * Sends a message to the stream ID passed in parameter.
   *
   * @param streamId    the ID of the stream to send the message to
   * @param message     the message to send to the stream
   * @return a {@link V4Message} object containing the details of the sent message
   * @see <a href="https://developers.symphony.com/restapi/reference#create-message-v4">Create Message v4</a>
   */
  V4Message send(@Nonnull String streamId, @Nonnull Message message);
}
