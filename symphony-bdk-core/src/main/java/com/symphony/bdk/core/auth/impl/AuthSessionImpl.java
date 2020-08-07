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

  private final BotAuthenticatorRSAImpl authenticator;

  private String sessionToken;
  private String keyManagerToken;

  public AuthSessionImpl(@Nonnull BotAuthenticatorRSAImpl authenticator) {
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

  protected BotAuthenticatorRSAImpl getAuthenticator() {
    return this.authenticator;
  }
}
