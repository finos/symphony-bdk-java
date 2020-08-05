package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * PLEASE PLEASE don't review this class !!
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class V4MessageService {

  private final MessagesApi messagesApi;
  private final AuthSession authSession;

  public V4MessageService(ApiClient agentClient, AuthSession authSession) {
    this.messagesApi = new MessagesApi(agentClient);
    this.authSession = authSession;
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
