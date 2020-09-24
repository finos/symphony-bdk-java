package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.core.auth.jwt.UserClaim;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * Extension App Authentication session handle. The {@link AppAuthSession#refresh()} will trigger a re-auth against the API endpoints.
 * <p>
 * You should keep using the same Symphony token. This token will expire at {@link AppAuthSession#expireAt()},
 * at which you should re-authenticate and get a new Symphony token for a new session.
 * </p>
 */
@API(status = API.Status.STABLE)
public interface AppAuthSession {

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

  /**
   * Validates a jwt against the pod certificate
   *
   * @param jwt the jwt to be validated
   * @return the {@link UserClaim} containing all information in jwt claim "user" if jwt successfully validated
   * @throws AuthInitializationException if jwt cannot be validated or if jwt is invalid
   * @see <a href="https://developers.symphony.com/extension/docs/application-authentication#section-verifying-decoding-and-using-the-jwt">Verifying, Decoding and Using the JWT</a>
   */
  UserClaim validateJwt(String jwt) throws AuthInitializationException;
}
