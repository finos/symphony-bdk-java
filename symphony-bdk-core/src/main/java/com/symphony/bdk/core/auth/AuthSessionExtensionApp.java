package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * Extension App Authentication session handle. The {@link AuthSessionExtensionApp#refresh()} will trigger a re-auth against the API endpoints.
 * <p>
 *   You should keep using the same Symphony token. This token will expire at {@link AuthSessionExtensionApp#expireAt()},
 *   at which you should re-authenticate and get a new Symphony token for a new session.
 * </p>
 */
@API(status = API.Status.STABLE)
public interface AuthSessionExtensionApp {

  /**
   * Symphony token for extension app session.
   *
   * @return Symphony token
   */
  @Nullable
  String getSymphonyToken();

  /**
   * Extension app authentication token.
   *
   * @return extension app authentication token
   */
  @Nullable
  String getAppToken();

  /**
   * Unix Timestamp in milliseconds of Symphony token expiration
   *
   * @return timestamp of Symphony token expiration
   */
  @Nullable
  Long expireAt();

  /**
   * Trigger re-authentication to refresh tokens.
   */
  void refresh() throws AuthUnauthorizedException;
}
