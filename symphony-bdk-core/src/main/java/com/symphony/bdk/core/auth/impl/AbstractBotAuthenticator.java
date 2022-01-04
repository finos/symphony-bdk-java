package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Abstract class to factorize the {@link BotAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@API(status = API.Status.INTERNAL)
public abstract class AbstractBotAuthenticator implements BotAuthenticator {

  private final AuthenticationRetry<String> kmAuthenticationRetry;
  private final AuthenticationRetry<Token> podAuthenticationRetry;

  public AbstractBotAuthenticator(BdkRetryConfig retryConfig) {
    kmAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    podAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
  }

  protected String retrieveToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return kmAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveToken", client.getBasePath(),
        () -> authenticateAndGetToken(client), unauthorizedMessage);
  }

  protected Token retrieveAuthToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return podAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveAuthToken", client.getBasePath(),
        () -> this.authenticateAndGetAuthToken(client), unauthorizedMessage);
  }

  protected abstract String authenticateAndGetToken(ApiClient client) throws ApiException;

  protected abstract Token authenticateAndGetAuthToken(ApiClient client) throws ApiException;

  protected abstract String getBotUsername();

}
