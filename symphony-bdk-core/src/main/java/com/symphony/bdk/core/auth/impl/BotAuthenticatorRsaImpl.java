package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.version.AgentVersionService;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * Bot authenticator RSA implementation.
 *
 * @see <a href="https://docs.developers.symphony.com/building-bots-on-symphony/authentication/rsa-authentication">RSA Bot Authentication Workflow</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorRsaImpl extends AbstractBotAuthenticator {

  private final String username;
  private final PrivateKey privateKey;

  private final ApiClient relayApiClient;

  public BotAuthenticatorRsaImpl(
      @Nonnull BdkRetryConfig retryConfig,
      @Nonnull String username,
      @Nonnull BdkCommonJwtConfig commonJwtConfig,
      @Nonnull PrivateKey privateKey,
      @Nonnull ApiClient loginApiClient,
      @Nonnull ApiClient relayApiClient,
      @Nonnull AgentVersionService agentVersionService
  ) {
    super(retryConfig, commonJwtConfig, loginApiClient, agentVersionService);
    this.username = username;
    this.privateKey = privateKey;
    this.relayApiClient = relayApiClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull AuthSession authenticateBot() throws AuthUnauthorizedException {
    final AuthSessionImpl authSession = new AuthSessionImpl(this);
    authSession.refresh();
    return authSession;
  }

  protected Token retrieveSessionToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving authentication tokens using RSA authentication...");
    return this.retrieveSessionToken(this.loginApiClient);
  }

  protected String retrieveKeyManagerToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving keyManagerToken using RSA authentication...");
    return this.retrieveKeyManagerToken(this.relayApiClient);
  }

  @Override
  protected Token doRetrieveToken(ApiClient client) throws ApiException {
    final String jwt = JwtHelper.createSignedJwt(this.username, JwtHelper.JWT_EXPIRATION_MILLIS, this.privateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    final Token token = new AuthenticationApi(client).pubkeyAuthenticatePost(req);
    log.debug("{} successfully retrieved.", token.getName());
    return token;
  }

  @Override
  protected String getBotUsername() {
    return username;
  }

}
