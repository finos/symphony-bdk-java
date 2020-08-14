package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import lombok.NonNull;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Bot authenticator service.
 */
@API(status = API.Status.STABLE)
public interface BotAuthenticator {

  /**
   * Authenticates a Bot's service account.
   *
   * @return the authentication session.
   */
  @Nonnull AuthSession authenticateBot();

  /**
   *
   * @return a new session token
   * @throws AuthUnauthorizedException in case of authentication failure
   */
  @NonNull String retrieveSessionToken() throws AuthUnauthorizedException;

  /**
   *
   * @return a new key manager token
   * @throws AuthUnauthorizedException in case of authentication failure
   */
  @NonNull String retrieveKeyManagerToken() throws AuthUnauthorizedException;
}
