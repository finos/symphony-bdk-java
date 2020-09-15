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
public class AuthSessionRsaImpl implements AuthSession {

  private final BotAuthenticatorRsaImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

  public AuthSessionRsaImpl(@Nonnull BotAuthenticatorRsaImpl authenticator) {
    this.authenticator = authenticator;
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

  /**
   * This method is only visible for testing.
   */
  protected BotAuthenticatorRsaImpl getAuthenticator() {
    return this.authenticator;
  }
}
