package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nonnull;

@API(status = Status.EXPERIMENTAL)
public interface CustomEnhancedAuthAuthenticator {

  /**
   * Authenticates.
   *
   * @return the authentication session.
   */
  @Nonnull
  String authenticate() throws AuthUnauthorizedException;

  boolean isAuthTokenExpired(ApiException exception);
}
