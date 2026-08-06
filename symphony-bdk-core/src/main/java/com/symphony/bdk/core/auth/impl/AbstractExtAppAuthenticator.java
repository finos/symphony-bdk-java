package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtAppAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Abstract class to factorize the {@link OboAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractExtAppAuthenticator implements ExtAppAuthenticator {

  protected final String appId;
  private final AuthenticationRetry<String> authenticationRetry;

  protected AbstractExtAppAuthenticator(BdkRetryConfig retryConfig, String appId) {
    this.appId = appId;
    this.authenticationRetry = new AuthenticationRetry<>(retryConfig);
  }

  protected String retrieveAppSessionToken() throws AuthUnauthorizedException {
    log.debug("Start authenticating app with id : {} ...", appId);

    final String unauthorizedErrorMessage = "Unable to authenticate app with ID : " + appId + ". "
        + "It usually happens when the app has not been configured or is not activated.";

    return authenticationRetry.executeAndRetry("AbstractExtAppAuthenticator.retrieveAppSessionToken", getBasePath(),
        this::authenticateAndRetrieveAppSessionToken, unauthorizedErrorMessage);
  }

  protected abstract String authenticateAndRetrieveAppSessionToken() throws ApiException;

  protected abstract String getBasePath();
}
