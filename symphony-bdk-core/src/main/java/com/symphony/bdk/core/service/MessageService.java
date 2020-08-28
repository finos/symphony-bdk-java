package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4Stream;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * PLEASE PLEASE don't review this class !!
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class MessageService {

  private final MessagesApi messagesApi;
  private final AuthSession authSession;

  public MessageService(MessagesApi messagesApi, AuthSession authSession) {
    this.messagesApi = messagesApi;
    this.authSession = authSession;
  }

  public V4Message send(V4Stream stream, String message) {
    return this.send(stream.getStreamId(), message);
  }

  public V4Message send(String streamId, String message) {
    try {
      return this.messagesApi.v4StreamSidMessageCreatePost(
          streamId,
          authSession.getSessionToken(),
          authSession.getKeyManagerToken(),
          message,
          null,
          null,
          null,
          null
      );
    } catch (ApiException e) {
      log.error("Cannot send message to stream {}", streamId, e);
      sleep(1_000);
      try {
        authSession.refresh();
      } catch (AuthUnauthorizedException exception) {
        log.error("Cannot authenticate", exception);
        return null;
      }
      return this.send(streamId, message);
    }
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ex) {
      // nothing to do
    }
  }
}
