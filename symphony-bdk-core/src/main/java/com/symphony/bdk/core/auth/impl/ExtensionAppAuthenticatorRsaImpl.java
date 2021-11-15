package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppTokensRepository;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.auth.jwt.UserClaim;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.model.AuthenticateExtensionAppRequest;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;
import com.symphony.bdk.gen.api.model.PodCertificate;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.security.PrivateKey;

import javax.annotation.Nonnull;

/**
 * Extension app authenticator RSA implementation.
 *
 * @see <a href="https://developers.symphony.com/extension/docs/application-authentication#section-verifying-decoding-and-using-the-jwt">Application Authentication</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class ExtensionAppAuthenticatorRsaImpl extends AbstractExtensionAppAuthenticator {

  private final PrivateKey appPrivateKey;
  private final AuthenticationApi authenticationApi;
  private final PodApi podApi;

  public ExtensionAppAuthenticatorRsaImpl(BdkRetryConfig retryConfig,
      String appId,
      PrivateKey appPrivateKey,
      ApiClient loginApiClient,
      ApiClient podApiClient) {
    this(retryConfig, appId, appPrivateKey, loginApiClient, podApiClient, new InMemoryTokensRepository());
  }

  public ExtensionAppAuthenticatorRsaImpl(BdkRetryConfig retryConfig,
      String appId,
      PrivateKey appPrivateKey,
      ApiClient loginApiClient,
      ApiClient podApiClient,
      ExtensionAppTokensRepository tokensRepository) {
    super(retryConfig, appId, tokensRepository);
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
    this.podApi = new PodApi(podApiClient);
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public AppAuthSession authenticateExtensionApp(@Nonnull String appToken) throws AuthUnauthorizedException {
    AppAuthSession authSession = new AppAuthSessionRsaImpl(this, appToken);
    authSession.refresh();
    return authSession;
  }

  @Override
  protected ExtensionAppTokens authenticateAndRetrieveTokens(String appToken) throws ApiException {
    final String jwt = JwtHelper.createSignedJwt(this.appId, JwtHelper.JWT_EXPIRATION_MILLIS, this.appPrivateKey);
    final AuthenticateExtensionAppRequest req = new AuthenticateExtensionAppRequest();
    req.authToken(jwt);
    req.appToken(appToken);

    return this.authenticationApi.v1PubkeyAppAuthenticateExtensionAppPost(req);
  }

  @Override
  protected String getBasePath() {
    return authenticationApi.getApiClient().getBasePath();
  }

  @Override
  protected PodCertificate callGetPodCertificate() throws ApiException {
    return this.podApi.v1PodcertGet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserClaim validateJwt(String jwt) throws AuthInitializationException {
    return JwtHelper.validateJwt(jwt, this.getPodCertificate().getCertificate());
  }
}
