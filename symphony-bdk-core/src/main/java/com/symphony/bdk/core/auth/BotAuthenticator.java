package com.symphony.bdk.core.auth;

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
}
