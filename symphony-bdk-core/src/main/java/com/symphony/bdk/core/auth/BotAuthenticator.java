package com.symphony.bdk.core.auth;

import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.STABLE)
public interface BotAuthenticator {

  /**
   *
   * @return
   */
  AuthSession authenticateBot();
}
