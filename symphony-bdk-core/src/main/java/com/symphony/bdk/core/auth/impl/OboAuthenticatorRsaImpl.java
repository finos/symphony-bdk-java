package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.model.AuthenticateRequest;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

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
public class OboAuthenticatorRsaImpl extends AbstractOboAuthenticator {

  private final AuthenticationApi authenticationApi;
  private final PrivateKey appPrivateKey;

  public OboAuthenticatorRsaImpl(BdkRetryConfig retryConfig, String appId, PrivateKey appPrivateKey, ApiClient loginApiClient) {
    super(retryConfig, appId);
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public AuthSession authenticateByUsername(@Nonnull String username) throws AuthUnauthorizedException {
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

  protected String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull Long userId) throws ApiException {
    return this.authenticationApi.pubkeyAppUserUserIdAuthenticatePost(appSessionToken, userId).getToken();
  }

  protected String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull String username) throws ApiException {
    return this.authenticationApi.pubkeyAppUsernameUsernameAuthenticatePost(appSessionToken, username).getToken();
  }

  protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
    final String jwt = JwtHelper.createSignedJwt(appId, JwtHelper.JWT_EXPIRATION_MILLIS, appPrivateKey);
    final AuthenticateRequest req = new AuthenticateRequest();
    req.setToken(jwt);

    return this.authenticationApi.pubkeyAppAuthenticatePost(req).getToken();
  }

  @Override
  protected String getBasePath() {
    return authenticationApi.getApiClient().getBasePath();
  }
}
