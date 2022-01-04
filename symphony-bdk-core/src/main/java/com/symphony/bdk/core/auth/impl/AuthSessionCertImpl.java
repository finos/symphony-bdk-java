package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.gen.api.model.Token;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * {@link AuthSession} impl for certificate authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionCertImpl implements AuthSession {

  private final BotAuthenticatorCertImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;
  private String authorizationToken;

  public AuthSessionCertImpl(BotAuthenticatorCertImpl authenticator) {
    this.authenticator = authenticator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSessionToken() {
    return this.sessionToken;
  }

  @Override
  public @Nullable
  String getAuthorizationToken() {
    return this.authorizationToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyManagerToken() {
    return this.keyManagerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() throws AuthUnauthorizedException {
    Token authToken = authenticator.retrieveAuthToken();
    this.sessionToken = authToken.getToken();
    this.authorizationToken = authToken.getAuthorizationToken();
    this.keyManagerToken = authenticator.retrieveKeyManagerToken();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshAuthToken() throws AuthUnauthorizedException {
    Token authToken = authenticator.retrieveAuthToken();
    this.authorizationToken = authToken.getAuthorizationToken();
  }

  /**
   * This method is only visible for testing.
   */
  protected BotAuthenticatorCertImpl getAuthenticator() {
    return authenticator;
  }
}
