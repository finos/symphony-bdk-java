package com.symphony.bdk.core.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.core.auth.jwt.JwtHelper;
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
  private Long authTokenExpirationDate;

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

  @Nullable
  @Override
  public Long getAuthTokenExpirationDate() {
    return this.authTokenExpirationDate;
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
    refreshExpirationDate();
    this.keyManagerToken = authenticator.retrieveKeyManagerToken();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshAuthToken() throws AuthUnauthorizedException {
    Token authToken = authenticator.retrieveAuthToken();
    this.sessionToken = authToken.getToken();
    this.authorizationToken = authToken.getAuthorizationToken();
    refreshExpirationDate();
  }

  private void refreshExpirationDate() throws AuthUnauthorizedException {
    if (this.authorizationToken != null) {
      try {
        this.authTokenExpirationDate = JwtHelper.extractExpirationDate(authorizationToken);
      } catch (JsonProcessingException | AuthUnauthorizedException e) {
        throw new AuthUnauthorizedException("Unable to parse the Authorization token received.");
      }
    }
  }

  /**
   * This method is only visible for testing.
   */
  protected BotAuthenticatorCertImpl getAuthenticator() {
    return authenticator;
  }
}
