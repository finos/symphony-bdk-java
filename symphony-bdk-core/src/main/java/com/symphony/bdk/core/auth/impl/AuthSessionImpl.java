package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthenticationException;

import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionImpl implements AuthSession {

  private final BotAuthenticatorRSAImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

  public AuthSessionImpl(BotAuthenticatorRSAImpl authenticator) {
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
  public void refresh() throws AuthenticationException {
    this.sessionToken = this.authenticator.retrieveSessionToken();
    this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
  }
}
