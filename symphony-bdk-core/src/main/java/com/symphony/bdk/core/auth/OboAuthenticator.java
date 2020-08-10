package com.symphony.bdk.core.auth;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * On-behalf-of authenticator service.
 */
@API(status = API.Status.STABLE)
public interface OboAuthenticator {

  /**
   * Authenticates on-behalf-of a particular user using his username.
   *
   * @param username Username of the user.
   * @return the authentication session.
   */
  @Nonnull AuthSession authenticateByUsername(@Nonnull String username);

  /**
   * Authenticates on behalf of a particular user using his userId.
   *
   * @param userId Id of the user.
   * @return the authentication sessions.
   */
  @Nonnull AuthSession authenticateByUserId(@Nonnull Long userId);
}
