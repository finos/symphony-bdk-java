package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.net.HttpURLConnection;
import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * Bot authenticator RSA implementation.
 *
 * @see <a href="https://developers.symphony.com/symphony-developer/docs/rsa-bot-authentication-workflow">RSA Bot Authentication Workflow</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class BotAuthenticatorRsaImpl implements BotAuthenticator {

  private final String username;
  private final PrivateKey privateKey;

  private final ApiClient loginApiClient;
  private final ApiClient relayApiClient;

  private JwtHelper jwtHelper = new JwtHelper();

  public BotAuthenticatorRsaImpl(
      @Nonnull String username,
      @Nonnull PrivateKey privateKey,
      @Nonnull ApiClient loginApiClient,
      @Nonnull ApiClient relayApiClient
  ) {
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
    return this.doRetrieveToken(this.loginApiClient);
  }

  protected String retrieveKeyManagerToken() throws AuthUnauthorizedException {
    log.debug("Start retrieving keyManagerToken using RSA authentication...");
    return this.doRetrieveToken(this.relayApiClient);
  }

  protected String doRetrieveToken(ApiClient client) throws AuthUnauthorizedException {
    final String jwt = this.jwtHelper.createSignedJwt(this.username, JwtHelper.JWT_EXPIRATION_MILLIS, this.privateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    try {
      final Token token = new AuthenticationApi(client).pubkeyAuthenticatePost(req);
      log.debug("{} successfully retrieved.", token.getName());
      return token.getToken();
    } catch (ApiException ex) {
      if (ex.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
        // usually happens when the public RSA is wrong or if the username is not correct
        throw new AuthUnauthorizedException("Service account with username '" + this.username + "' is not authorized to authenticate. "
            + "Check if the public RSA key is valid or if the username is correct.", ex);
      } else {
        // we don't know what to do, let's forward the ApiException
        throw new ApiRuntimeException(ex);
      }
    }
  }

  protected void setJwtHelper(JwtHelper jwtHelper) {
    this.jwtHelper = jwtHelper;
  }
}
