package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.CertificateAuthenticationApi;
import com.symphony.bdk.gen.api.model.OboAuthResponse;
import com.symphony.bdk.gen.api.model.Token;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.net.HttpURLConnection;

import javax.annotation.Nonnull;

/**
 * OBO Certificate authenticator implementation
 *
 * @see <a href="https://developers.symphony.com/restapi/docs/get-started-with-obo">Get Started with OBO</a>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class OboAuthenticatorCertImpl implements OboAuthenticator {

  private final CertificateAuthenticationApi authenticationApi;
  private final String appId;

  public OboAuthenticatorCertImpl(String appId, ApiClient sessionAuthClient) {
    this.appId = appId;
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

  protected String retrieveOboSessionTokenByUserId(@NonNull Long userId) throws AuthUnauthorizedException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      OboAuthResponse oboAuthResponse = this.authenticationApi.v1AppUserUidAuthenticatePost(userId, appSessionToken);
      return oboAuthResponse.getSessionToken();
    } catch (ApiException e) {
      if (e.isUnauthorized()) {
        throw new AuthUnauthorizedException("Unable to authenticate on-behalf-of user with ID '" + userId + "'. "
            + "It usually happens when the user has not installed the app with ID : " + this.appId, e);
      } else {
        throw new ApiRuntimeException(e);
      }
    }
  }

  protected String retrieveOboSessionTokenByUsername(@NonNull String username) throws AuthUnauthorizedException {
    final String appSessionToken = this.retrieveAppSessionToken();
    try {
      OboAuthResponse oboAuthResponse = this.authenticationApi.v1AppUsernameUsernameAuthenticatePost(username, appSessionToken);
      return oboAuthResponse.getSessionToken();
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
    log.debug("Start authenticating app using certificate with app id : {} ...", this.appId);

    try {
      final Token token = this.authenticationApi.v1AppAuthenticatePost();
      log.debug("App with ID '{}' successfully authenticated.", this.appId);
      return token.getToken();
    } catch (ApiException e) {
      if (e.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
        // usually happens when the certificate is not correct
        throw new AuthUnauthorizedException(
            "Unable to authenticate app with ID : " + this.appId + ". "
                + "It usually happens when the app has not been configured or is not activated.", e);
      } else {
        // we don't know what to do, let's forward the ApiException
        throw new ApiRuntimeException(e);
      }
    }
  }
}
