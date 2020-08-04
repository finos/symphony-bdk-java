package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthenticationException;

/**
 *
 */
public interface AuthSession {

  /**
   *
   * @return
   */
  String getSessionToken();

  /**
   *
   * @return
   */
  String getKeyManagerToken();

  /**
   *
   */
  void refresh() throws AuthenticationException;
}
