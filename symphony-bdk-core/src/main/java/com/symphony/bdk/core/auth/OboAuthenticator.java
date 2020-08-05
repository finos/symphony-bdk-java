package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthenticationException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 *
 */
@API(status = API.Status.STABLE)
public interface OboAuthenticator {

  /**
   *
   * @param username
   * @return
   * @throws AuthenticationException
   */
  AuthSession authenticateByUsername(@Nonnull String username) throws AuthenticationException;

  /**
   *
   * @param userId
   * @return
   * @throws AuthenticationException
   */
  AuthSession authenticateByUserId(@Nonnull Long userId) throws AuthenticationException;
}
