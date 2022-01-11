package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * Authentication session handle. The {@link AuthSession#refresh()} will trigger a re-auth against the API endpoints.
 * <p>
 *   You should keep using the same token until you receive a HTTP 401, at which you should re-authenticate and
 *   get a new token for a new session.
 * </p>
 */
@API(status = API.Status.STABLE)
public interface AuthSession {

  /**
   * Pod's authentication token.
   *
   * @return the Pod session token
   */
  @Nullable String getSessionToken();

  /**
   * Pod's Common JWT authentication token.
   *
   * @return the Pod Authorization token
   */
  @Nullable default String getAuthorizationToken() {
    return null;
  }

  /**
   * Common jwt expiration date in seconds
   *
   * @return the expiration date found in the common jwt claims (in seconds)
   */
  @Nullable default Long getAuthTokenExpirationDate() {
    return null;
  }

  /**
   * KeyManager's authentication token.
   *
   * @return the KeyManager token, null if OBO
   */
  @Nullable String getKeyManagerToken();

  /**
   * Trigger re-authentication to refresh tokens.
   */
  void refresh() throws AuthUnauthorizedException;

  /**
   * Trigger re-authentication to refresh the Authorization token. Used when common jwt is enabled.
   */
  @Deprecated // TODO could be removed?
  default void refreshAuthToken() throws AuthUnauthorizedException {
  }
}
