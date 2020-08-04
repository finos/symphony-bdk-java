package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthenticationException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
@API(status = API.Status.STABLE)
public interface AuthSession {

  /**
   *
   * @return
   */
  @Nonnull String getSessionToken();

  /**
   *
   * @return
   */
  @Nullable String getKeyManagerToken();

  /**
   *
   */
  void refresh() throws AuthenticationException;
}
