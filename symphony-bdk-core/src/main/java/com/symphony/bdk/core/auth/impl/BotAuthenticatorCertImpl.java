package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Bot authenticator certificate implementation.
 *
 * @see <a href="https://developers.symphony.com/symphony-developer/docs/bot-authentication-workflow-1">BotAuthentication Workflow</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorCertImpl extends AbstractBotAuthenticator {

  private final ApiClient sessionAuthClient;
  private final ApiClient keyAuthClient;
  private final String username;

  public BotAuthenticatorCertImpl(
      @Nonnull BdkRetryConfig retryConfig,
      @Nonnull String username,
      @Nonnull BdkCommonJwtConfig commonJwtConfig,
      @Nonnull ApiClient loginClient,
      @Nonnull ApiClient sessionAuthClient,
      @Nonnull ApiClient keyAuthClient) {
    super(retryConfig, commonJwtConfig, loginClient);
    this.sessionAuthClient = sessionAuthClient;
    this.keyAuthClient = keyAuthClient;
    this.username = username;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nonnull
  public AuthSession authenticateBot() throws AuthUnauthorizedException {
    AuthSessionImpl authSession = new AuthSessionImpl(this);
    authSession.refresh();
    return authSession;
  }

  @Override
  @Nonnull
  protected Token retrieveAuthToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving authentication tokens using certificate authentication...");
    return retrieveAuthToken(this.sessionAuthClient);
  }

  @Override
  @Nonnull
  protected String retrieveKeyManagerToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving keyManagerToken using certificate authentication...");
    return retrieveToken(this.keyAuthClient);
  }

  @Override
  protected String authenticateAndGetToken(ApiClient client) throws ApiException {
    final Token token = new CertificateAuthenticationApi(client).v1AuthenticatePost();
    log.debug("{} successfully retrieved.", token.getName());
    return token.getToken();
  }

  @Override
  protected Token authenticateAndGetAuthToken(ApiClient client) throws ApiException {
    final Token token = new CertificateAuthenticationApi(client).v1AuthenticatePost();
    log.debug("Authentication tokens successfully retrieved.");
    return token;
  }

  @Override
  protected String getBotUsername() {
    return username;
  }

}
