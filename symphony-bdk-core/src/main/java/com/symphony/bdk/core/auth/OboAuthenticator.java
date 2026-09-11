package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import org.apiguardian.api.API;

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
  AuthSession authenticateByUsername(String username) throws AuthUnauthorizedException;

  /**
   * Authenticates on behalf of a particular user using his userId.
   *
   * @param userId Id of the user.
   * @return the authentication sessions.
   */
  AuthSession authenticateByUserId(Long userId) throws AuthUnauthorizedException;
}
