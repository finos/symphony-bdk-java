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
   * @return the Pod session token
   */
  @Nonnull String getSessionToken();

  /**
   *
   * @return the KeyManager token, null if OBO
   */
  @Nullable String getKeyManagerToken();

  /**
   *
   */
  void refresh() throws AuthenticationException;
}
