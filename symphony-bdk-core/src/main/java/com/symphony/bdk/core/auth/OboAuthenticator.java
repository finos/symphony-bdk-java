package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthenticationException;

import javax.annotation.Nonnull;

/**
 *
 */
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
  AuthSession authenticateByUserID(@Nonnull Long userId) throws AuthenticationException;
}
