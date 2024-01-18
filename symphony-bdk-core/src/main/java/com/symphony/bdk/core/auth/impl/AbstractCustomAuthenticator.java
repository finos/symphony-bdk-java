package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.CustomEnhancedAuthAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.http.api.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nonnull;


@RequiredArgsConstructor
@Slf4j
@API(status = Status.EXPERIMENTAL)
public abstract class AbstractCustomAuthenticator implements CustomEnhancedAuthAuthenticator {
  private final AuthenticationRetry<String> authenticationRetry;

  public AbstractCustomAuthenticator(BdkConfig bdkConfig) {
    authenticationRetry = new AuthenticationRetry<>(bdkConfig.getRetry());
  }

  @Override
  public @Nonnull String authenticate() throws AuthUnauthorizedException {
    final String unauthorizedMessage = "Cannot retrieve the custom enhanced authentication token";
    return authenticationRetry.executeAndRetry("CustomAuthenticator.authenticate",
        "enhanced custom authentication server address", this::doRetrieveToken, unauthorizedMessage);
  }

  /**
   * map custom auth request failure exception to ApiException
   *
   * @return
   * @throws ApiException
   */
  protected abstract @Nonnull String doRetrieveToken() throws ApiException;
}
