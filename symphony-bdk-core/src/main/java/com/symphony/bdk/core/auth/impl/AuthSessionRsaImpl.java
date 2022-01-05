package com.symphony.bdk.core.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
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
  private Long authTokenExpirationDate;

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

  @Nullable
  @Override
  public Long getAuthTokenExpirationDate() {
    return this.authTokenExpirationDate;
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
    refreshExpirationDate();
    this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshAuthToken() throws AuthUnauthorizedException {
    Token authToken = authenticator.retrieveAuthToken();
    this.authorizationToken = authToken.getAuthorizationToken();
    refreshExpirationDate();
  }

  private void refreshExpirationDate() throws AuthUnauthorizedException {
    if (this.authorizationToken != null) {
      try {
        this.authTokenExpirationDate = JwtHelper.extractExpirationDate(authorizationToken);
      } catch (JsonProcessingException e) {
        throw new AuthUnauthorizedException("Unable to parse the Authorization token received.");
      }
    }
  }
  /**
   * This method is only visible for testing.
   */
  protected BotAuthenticatorRsaImpl getAuthenticator() {
    return this.authenticator;
  }
}
