package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * Bot authenticator RSA implementation.
 *
 * @see <a href="https://developers.symphony.com/symphony-developer/docs/rsa-bot-authentication-workflow">RSA Bot Authentication Workflow</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorRsaImpl extends AbstractBotAuthenticator {

  private final String username;
  private final PrivateKey privateKey;

  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  public BotAuthenticatorRsaImpl(
      @Nonnull BdkRetryConfig retryConfig,
      @Nonnull String username,
      @Nonnull PrivateKey privateKey,
      @Nonnull ApiClient loginApiClient,
      @Nonnull ApiClient relayApiClient
  ) {
    super(retryConfig);
    this.username = username;
    this.privateKey = privateKey;
    this.loginApiClient = loginApiClient;
    this.relayApiClient = relayApiClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull AuthSession authenticateBot() throws AuthUnauthorizedException {
    final AuthSessionRsaImpl authSession = new AuthSessionRsaImpl(this);
    authSession.refresh();
    return authSession;
  }

  protected String retrieveSessionToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving sessionToken using RSA authentication...");
    return this.retrieveToken(this.loginApiClient);
  }

  protected String retrieveKeyManagerToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving keyManagerToken using RSA authentication...");
    return this.retrieveToken(this.relayApiClient);
  }

  @Override
  protected String authenticateAndGetToken(ApiClient client) throws ApiException {
    final String jwt = JwtHelper.createSignedJwt(this.username, JwtHelper.JWT_EXPIRATION_MILLIS, this.privateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    final Token token = new AuthenticationApi(client).pubkeyAuthenticatePost(req);
    log.debug("{} successfully retrieved.", token.getName());
    return token.getToken();
  }

  @Override
  protected String getBotUsername() {
    return username;
  }

}
