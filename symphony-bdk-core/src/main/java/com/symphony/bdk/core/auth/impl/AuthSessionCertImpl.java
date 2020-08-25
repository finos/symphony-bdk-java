package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

public class AuthSessionCertImpl implements AuthSession {

  private final BotAuthenticatorCertImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

  public AuthSessionCertImpl(BotAuthenticatorCertImpl authenticator) {
    this.authenticator = authenticator;
  }

  @Override
  public String getSessionToken() {
    return this.sessionToken;
  }

  @Override
  public String getKeyManagerToken() {
    return this.keyManagerToken;
  }

  @Override
  public void refresh() throws AuthUnauthorizedException {
    this.sessionToken = authenticator.retrieveSessionToken();
    this.keyManagerToken = authenticator.retrieveKeyManagerToken();
  }

  protected BotAuthenticatorCertImpl getAuthenticator() {
    return authenticator;
  }
}
