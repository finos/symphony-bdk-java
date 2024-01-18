package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nullable;


@API(status = Status.EXPERIMENTAL)
public interface BotAuthSession extends AuthSession {
  default String sessionId() {
    return "botSession";
  }

  /**
   * Pod's authentication token.
   *
   * @return the Pod session token
   */
  @Nullable
  String getSessionToken();

  /**
   * Pod's Common JWT authentication token. When commonJwt.enabled is set to true in the configuration, an OAuth
   * authentication scheme is used where the session token acts as the refresh token and the authorization token is a
   * short lived access token.
   *
   * @return the Pod Authorization token
   */
  @Nullable default String getAuthorizationToken() throws AuthUnauthorizedException {
    return null;
  }

  /**
   * KeyManager's authentication token.
   *
   * @return the KeyManager token, null if OBO
   */
  @Nullable String getKeyManagerToken();
}
