package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Extension app authenticator RSA implementation.
 *
 * @see <a href="https://developers.symphony.com/extension/docs/application-authentication#section-verifying-decoding-and-using-the-jwt">Application Authentication</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class ExtensionAppAuthenticatorCertImpl implements ExtensionAppAuthenticator {

  private final String appId;
  private final CertificateAuthenticationApi certificateAuthenticationApi;

  public ExtensionAppAuthenticatorCertImpl(String appId, ApiClient sessionAuthClient) {
    this.appId = appId;
    this.certificateAuthenticationApi = new CertificateAuthenticationApi(sessionAuthClient);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AppAuthSession authenticateExtensionApp(String appToken) throws AuthUnauthorizedException {
    AppAuthSession authSession = new AppAuthSessionCertImpl(this, appToken);
    authSession.refresh();
    return authSession;
  }

  protected ExtensionAppTokens retrieveExtensionAppSession(String appToken) throws AuthUnauthorizedException {
    log.debug("Start certificate authentication of extension app with id : {} ...", this.appId);

    final ExtensionAppAuthenticateRequest authRequest = new ExtensionAppAuthenticateRequest().appToken(appToken);

    try {
      return this.certificateAuthenticationApi.v1AuthenticateExtensionAppPost(authRequest);
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
