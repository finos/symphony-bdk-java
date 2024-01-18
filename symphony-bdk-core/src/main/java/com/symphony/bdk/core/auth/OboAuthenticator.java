package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
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
  @Nonnull BotAuthSession authenticateByUsername(@Nonnull String username) throws AuthUnauthorizedException;

  /**
   * Authenticates on behalf of a particular user using his userId.
   *
   * @param userId Id of the user.
   * @return the authentication sessions.
   */
  @Nonnull BotAuthSession authenticateByUserId(@Nonnull Long userId) throws AuthUnauthorizedException;
}
