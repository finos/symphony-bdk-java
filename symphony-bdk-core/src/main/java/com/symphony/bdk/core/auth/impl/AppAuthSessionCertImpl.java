package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.auth.jwt.UserClaim;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;

import org.apiguardian.api.API;

/**
 * {@link AppAuthSession} implementation for certificate extension app authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AppAuthSessionCertImpl implements AppAuthSession {

  private final ExtensionAppAuthenticatorCertImpl authenticator;
  private String symphonySessionToken;
  private String appToken;
  private Long expireAt;

  public AppAuthSessionCertImpl(ExtensionAppAuthenticatorCertImpl authenticator, String appToken) {
    this.authenticator = authenticator;
    this.appToken = appToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSymphonyToken() {
    return this.symphonySessionToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAppToken() {
    return this.appToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long expireAt() {
    return this.expireAt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() throws AuthUnauthorizedException {
    ExtensionAppTokens appTokens = this.authenticator.retrieveExtensionAppSession(this.appToken);
    this.symphonySessionToken = appTokens.getSymphonyToken();
    this.appToken = appTokens.getAppToken();
    this.expireAt = appTokens.getExpireAt();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserClaim validateJwt(String jwt) throws AuthInitializationException {
    return JwtHelper.validateJwt(jwt, authenticator.getPodCertificate().getCertificate());
  }

  /**
   * This method is only visible for testing.
   */
  protected ExtensionAppAuthenticator getAuthenticator() {
    return authenticator;
  }
}
