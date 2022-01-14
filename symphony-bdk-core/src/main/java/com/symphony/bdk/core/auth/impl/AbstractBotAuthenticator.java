package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.JwtToken;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Abstract class to factorize the {@link BotAuthenticator} logic between RSA and certificate,
 * especially the retry logic on top of HTTP calls.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractBotAuthenticator implements BotAuthenticator {

  protected final ApiClient loginApiClient;
  private final BdkCommonJwtConfig commonJwtConfig;
  private final AuthenticationRetry<String> kmAuthenticationRetry;
  private final AuthenticationRetry<Token> podAuthenticationRetry;
  private final AuthenticationRetry<JwtToken> bearerAuthenticationRetry;

  public AbstractBotAuthenticator(BdkRetryConfig retryConfig,
      @Nonnull BdkCommonJwtConfig commonJwtConfig, @Nonnull ApiClient loginApiClient) {
    kmAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    podAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    bearerAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    this.commonJwtConfig = commonJwtConfig;
    this.loginApiClient = loginApiClient;
  }

  protected String retrieveToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return kmAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveToken", client.getBasePath(),
        () -> authenticateAndGetToken(client), unauthorizedMessage);
  }

  protected JwtToken retrieveBearerToken(String sessionToken) throws AuthUnauthorizedException {
    log.debug("Start retrieving keyManagerToken using RSA authentication...");
    return this.retrieveBearerToken(this.loginApiClient, sessionToken);
  }


  protected JwtToken retrieveBearerToken(ApiClient client, String sessionToken) throws AuthUnauthorizedException {

    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return bearerAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveBearerToken", client.getBasePath(),
        () -> this.getBearerToken(client, sessionToken), unauthorizedMessage);
  }


  protected Token retrieveAuthToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return podAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveAuthToken", client.getBasePath(),
        () -> this.authenticateAndGetAuthToken(client), unauthorizedMessage);
  }

  protected abstract Token retrieveAuthToken() throws AuthUnauthorizedException;

  protected abstract String retrieveKeyManagerToken() throws AuthUnauthorizedException;

  protected abstract String authenticateAndGetToken(ApiClient client) throws ApiException;

  protected abstract Token authenticateAndGetAuthToken(ApiClient client) throws ApiException;

  protected JwtToken getBearerToken(ApiClient client, String sessionToken) throws ApiException {
    return new AuthenticationApi(client).idmTokensPost(sessionToken, "");
  }

  protected abstract String getBotUsername();

  public boolean isCommonJwtEnabled() {
    return commonJwtConfig.getEnabled();
  }
}
