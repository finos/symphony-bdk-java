package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthenticationException;

import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.STABLE)
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
