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
  private final AuthenticationRetry<JwtToken> idmAuthenticationRetry;

  protected AbstractBotAuthenticator(BdkRetryConfig retryConfig,
      @Nonnull BdkCommonJwtConfig commonJwtConfig, @Nonnull ApiClient loginApiClient) {
    kmAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    podAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    idmAuthenticationRetry = new AuthenticationRetry<>(retryConfig);
    this.commonJwtConfig = commonJwtConfig;
    this.loginApiClient = loginApiClient;
  }

  protected abstract String retrieveKeyManagerToken() throws AuthUnauthorizedException;

  protected String retrieveKeyManagerToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return kmAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveKeyManagerToken",
        client.getBasePath(), () -> doRetrieveToken(client).getToken(), unauthorizedMessage);
  }

  protected abstract Token retrieveSessionToken() throws AuthUnauthorizedException;

  protected Token retrieveSessionToken(ApiClient client) throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    return podAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveSessionToken",
        client.getBasePath(), () -> this.doRetrieveToken(client), unauthorizedMessage);
  }

  /**
   * Login API to retrieve a token is the same for KM and pod.
   */
  protected abstract Token doRetrieveToken(ApiClient client) throws ApiException;

  protected String retrieveAuthorizationToken(String sessionToken) throws AuthUnauthorizedException {
    log.debug("Start retrieving authorizationToken using RSA authentication...");
    return this.doRetrieveAuthorizationToken(this.loginApiClient, sessionToken).getAccessToken();
  }

  protected JwtToken doRetrieveAuthorizationToken(ApiClient client, String sessionToken)
      throws AuthUnauthorizedException {
    final String unauthorizedMessage = String.format("Service account \"%s\" is not authorized to authenticate. "
        + "Check if credentials are valid.", getBotUsername());

    // we are not using any scopes for now when calling pod APIs
    return idmAuthenticationRetry.executeAndRetry("AbstractBotAuthenticator.retrieveAuthorizationToken",
        client.getBasePath(), () -> new AuthenticationApi(client).idmTokensPost(sessionToken, ""), unauthorizedMessage);
  }

  protected abstract String getBotUsername();

  public boolean isCommonJwtEnabled() {
    return commonJwtConfig.getEnabled();
  }
}
