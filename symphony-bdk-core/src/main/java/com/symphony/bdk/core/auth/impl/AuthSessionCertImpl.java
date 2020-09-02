package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

/**
 * {@link AuthSession} impl for certificate authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionCertImpl implements AuthSession {

  private final BotAuthenticatorCertImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

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
    this.sessionToken = authenticator.retrieveSessionToken();
    this.keyManagerToken = authenticator.retrieveKeyManagerToken();
  }

  protected BotAuthenticatorCertImpl getAuthenticator() {
    return authenticator;
  }
}
