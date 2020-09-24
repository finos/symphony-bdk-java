package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.gen.api.model.Token;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * OBO authenticator RSA implementation.
 *
 * @see <a href="https://developers.symphony.com/restapi/docs/get-started-with-obo">Get Started with OBO</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class OboAuthenticatorRsaImpl implements OboAuthenticator {

  private final AuthenticationApi authenticationApi;
  private final String appId;
  private final PrivateKey appPrivateKey;

  private final JwtHelper jwtHelper = new JwtHelper();

  public OboAuthenticatorRsaImpl(String appId, PrivateKey appPrivateKey, ApiClient loginApiClient) {
    this.appId = appId;
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull AuthSession authenticateByUsername(@Nonnull String username) throws AuthUnauthorizedException {
    AuthSession authSession = new AuthSessionOboImpl(this, username);
    authSession.refresh();
    return authSession;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull AuthSession authenticateByUserId(@Nonnull Long userId) throws AuthUnauthorizedException {
    AuthSession authSession = new AuthSessionOboImpl(this, userId);
    authSession.refresh();
    return authSession;
  }

  protected String retrieveOboSessionTokenByUserId(@Nonnull Long userId) throws AuthUnauthorizedException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      return this.authenticationApi.pubkeyAppUserUserIdAuthenticatePost(appSessionToken, userId).getToken();
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate on-behalf-of user with ID '" + userId + "'. "
            + "It usually happens when the user has not installed the app with ID : " + this.appId, e);
      } else {
         throw new ApiRuntimeException(e);
      }
    }
  }

  protected String retrieveOboSessionTokenByUsername(@Nonnull String username) throws AuthUnauthorizedException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      return this.authenticationApi.pubkeyAppUsernameUsernameAuthenticatePost(appSessionToken, username).getToken();
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate on-behalf-of user with username '" + username + "'. "
            + "It usually happens when the user has not installed the app with ID : " + this.appId, e);
      } else {
        throw new ApiRuntimeException(e);
      }
    }
  }

  protected String retrieveAppSessionToken() throws AuthUnauthorizedException {
    log.debug("Start authenticating app with id : {} ...", this.appId);

    final String jwt = this.jwtHelper.createSignedJwt(this.appId, JwtHelper.JWT_EXPIRATION_MILLIS, this.appPrivateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    try {
      final Token token = this.authenticationApi.pubkeyAppAuthenticatePost(req);
      log.debug("App with ID '{}' successfully authenticated.", this.appId);
      return token.getToken();
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate app with ID : " + this.appId + ". "
            + "It usually happens when the app has not been configured or is not activated.", e);
      } else {
        throw new ApiRuntimeException(e);
      }
    }
  }
}
