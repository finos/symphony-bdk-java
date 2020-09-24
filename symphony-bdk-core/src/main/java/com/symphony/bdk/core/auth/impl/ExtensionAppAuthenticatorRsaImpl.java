package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.AuthenticationApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.model.AuthenticateExtensionAppRequest;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;

import com.symphony.bdk.gen.api.model.PodCertificate;

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
public class ExtensionAppAuthenticatorRsaImpl implements ExtensionAppAuthenticator {

  private final JwtHelper jwtHelper = new JwtHelper();

  private final String appId;
  private final PrivateKey appPrivateKey;
  private final AuthenticationApi authenticationApi;
  private final PodApi podApi;

  public ExtensionAppAuthenticatorRsaImpl(String appId, PrivateKey appPrivateKey, ApiClient loginApiClient, ApiClient podApiClient) {
    this.appId = appId;
    this.appPrivateKey = appPrivateKey;
    this.authenticationApi = new AuthenticationApi(loginApiClient);
    this.podApi = new PodApi(podApiClient);
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public AppAuthSession authenticateExtensionApp(@Nonnull String appToken)
      throws AuthUnauthorizedException {
    AppAuthSession authSession = new AppAuthSessionRsaImpl(this, appToken);
    authSession.refresh();
    return authSession;
  }

  protected ExtensionAppTokens retrieveExtensionAppSession(String appToken) throws AuthUnauthorizedException {
    log.debug("Start authenticating extension app with id : {} ...", this.appId);

    final String jwt = this.jwtHelper.createSignedJwt(this.appId, JwtHelper.JWT_EXPIRATION_MILLIS, this.appPrivateKey);
    final AuthenticateExtensionAppRequest req = new AuthenticateExtensionAppRequest();
    req.authToken(jwt);
    req.appToken(appToken);

    try {
      final ExtensionAppTokens extensionAppTokens = this.authenticationApi.v1PubkeyAppAuthenticateExtensionAppPost(req);
      log.debug("App with ID '{}' successfully authenticated.", this.appId);
      return extensionAppTokens;
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate app with ID : " + this.appId + ". "
            + "It usually happens when the app has not been configured or is not activated.", e);
      } else {
        throw new ApiRuntimeException(e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PodCertificate getPodCertificate() {
    try {
      return this.podApi.v1PodcertGet();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }
}
