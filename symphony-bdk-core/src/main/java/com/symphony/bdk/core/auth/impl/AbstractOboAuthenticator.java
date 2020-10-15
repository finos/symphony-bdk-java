package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Abstract class to factorize the {@link OboAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractOboAuthenticator implements OboAuthenticator {

  protected String appId;
  private AuthenticationRetry<String> authenticationRetry;

  public AbstractOboAuthenticator(BdkRetryConfig retryConfig, String appId) {
    this.appId = appId;
    this.authenticationRetry = new AuthenticationRetry<>(retryConfig);
  }

  protected String retrieveOboSessionTokenByUserId(@Nonnull Long userId) throws AuthUnauthorizedException {
    final String appSessionToken = retrieveAppSessionToken();

    final String unauthorizedErrorMessage = "Unable to authenticate on-behalf-of user with ID '" + userId + "'. "
        + "It usually happens when the user has not installed the app with ID : " + appId;

    return authenticationRetry.executeAndRetry("AbstractOboAuthenticator.retrieveOboSessionTokenByUserId",
        () -> authenticateAndRetrieveOboSessionToken(appSessionToken, userId), unauthorizedErrorMessage);
  }

  protected String retrieveOboSessionTokenByUsername(@Nonnull String username) throws AuthUnauthorizedException {
    final String appSessionToken = retrieveAppSessionToken();

    final String unauthorizedErrorMessage =
        "Unable to authenticate on-behalf-of user with username '" + username + "'. "
            + "It usually happens when the user has not installed the app with ID : " + appId;

    return authenticationRetry.executeAndRetry("AbstractOboAuthenticator.retrieveOboSessionTokenByUsername",
        () -> authenticateAndRetrieveOboSessionToken(appSessionToken, username), unauthorizedErrorMessage);
  }

  protected String retrieveAppSessionToken() throws AuthUnauthorizedException {
    log.debug("Start authenticating app with id : {} ...", appId);

    final String unauthorizedErrorMessage = "Unable to authenticate app with ID : " + appId + ". "
        + "It usually happens when the app has not been configured or is not activated.";

    return authenticationRetry.executeAndRetry("AbstractOboAuthenticator.retrieveAppSessionToken",
        this::authenticateAndRetrieveAppSessionToken, unauthorizedErrorMessage);
  }

  protected abstract String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull Long userId) throws ApiException;

  protected abstract String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull String username) throws ApiException;

  protected abstract String authenticateAndRetrieveAppSessionToken() throws ApiException;
}
