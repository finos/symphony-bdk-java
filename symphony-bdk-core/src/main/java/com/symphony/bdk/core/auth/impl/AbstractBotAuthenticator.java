package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Abstract class to factorize the {@link BotAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@API(status = API.Status.INTERNAL)
public abstract class AbstractBotAuthenticator implements BotAuthenticator {

  private final AuthenticationRetry<String> authenticationRetry;

  public AbstractBotAuthenticator(BdkRetryConfig retryConfig) {
    authenticationRetry = new AuthenticationRetry<>(retryConfig);
  }

  protected String retrieveToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = "Service account is not authorized to authenticate. "
        + "Check if credentials are valid.";

    return authenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveToken", client.getBasePath(),
        () -> authenticateAndGetToken(client), unauthorizedMessage);
  }

  protected abstract String authenticateAndGetToken(ApiClient client) throws ApiException;
}
