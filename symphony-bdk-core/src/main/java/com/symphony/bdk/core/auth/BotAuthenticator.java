package com.symphony.bdk.core.auth;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Bot authenticator service.
 */
@API(status = API.Status.STABLE)
public interface BotAuthenticator {

  /**
   *
   * @return
   */
  @Nonnull AuthSession authenticateBot();
}
