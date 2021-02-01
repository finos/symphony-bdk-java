package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * OBO Certificate authenticator implementation
 *
 * @see <a href="https://developers.symphony.com/restapi/docs/get-started-with-obo">Get Started with OBO</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class OboAuthenticatorCertImpl extends AbstractOboAuthenticator {

  private final CertificateAuthenticationApi authenticationApi;

  public OboAuthenticatorCertImpl(BdkRetryConfig retryConfig, String appId, ApiClient sessionAuthClient) {
    super(retryConfig, appId);
    this.authenticationApi = new CertificateAuthenticationApi(sessionAuthClient);
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public AuthSession authenticateByUsername(@Nonnull String username) throws AuthUnauthorizedException {
    AuthSession authSession = new AuthSessionOboCertImpl(this, username);
    authSession.refresh();
    return authSession;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public AuthSession authenticateByUserId(@Nonnull Long userId) throws AuthUnauthorizedException {
    AuthSession authSession = new AuthSessionOboCertImpl(this, userId);
    authSession.refresh();
    return authSession;
  }

  protected String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull Long userId) throws ApiException {
    return this.authenticationApi.v1AppUserUidAuthenticatePost(userId, appSessionToken).getSessionToken();
  }

  protected String authenticateAndRetrieveOboSessionToken(@Nonnull String appSessionToken,
      @Nonnull String username) throws ApiException {
    return this.authenticationApi.v1AppUsernameUsernameAuthenticatePost(username, appSessionToken).getSessionToken();
  }

  protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
    return this.authenticationApi.v1AppAuthenticatePost().getToken();
  }

  @Override
  protected String getBasePath() {
    return authenticationApi.getApiClient().getBasePath();
  }
}
