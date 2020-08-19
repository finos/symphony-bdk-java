package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link AuthSession} impl for regular authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionImpl implements AuthSession {

  private final BotAuthenticatorRsaImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

  public AuthSessionImpl(@Nonnull BotAuthenticatorRsaImpl authenticator) throws AuthUnauthorizedException {
    this.authenticator = authenticator;
    this.sessionToken = authenticator.retrieveSessionToken();
    this.keyManagerToken = authenticator.retrieveKeyManagerToken();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable String getSessionToken() {
    return this.sessionToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable String getKeyManagerToken() {
    return this.keyManagerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() throws AuthUnauthorizedException {
    this.sessionToken = this.authenticator.retrieveSessionToken();
    this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
  }

  protected BotAuthenticatorRsaImpl getAuthenticator() {
    return this.authenticator;
  }
}
