package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.Token;

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
  private String authorizationToken;

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
  public @Nullable String getAuthorizationToken() {
    return this.authorizationToken;
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
    Token authToken = authenticator.retrieveAuthToken();
    this.authorizationToken = authToken.getAuthorizationToken();
    this.sessionToken = authToken.getToken();
    this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
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
  protected BotAuthenticatorRsaImpl getAuthenticator() {
    return this.authenticator;
  }
}
