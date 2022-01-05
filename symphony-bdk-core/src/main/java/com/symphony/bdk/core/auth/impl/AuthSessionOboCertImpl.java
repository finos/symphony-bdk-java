package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;

import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link AuthSession} impl for OBO Certificate authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionOboCertImpl implements AuthSession {

  private final OboAuthenticatorCertImpl authenticator;
  private final Long userId;
  private final String username;

  private String sessionToken;

  public AuthSessionOboCertImpl(@Nonnull OboAuthenticatorCertImpl authenticator, @Nonnull Long userId) {
    this.authenticator = authenticator;
    this.userId = userId;
    this.username = null;
  }

  public AuthSessionOboCertImpl(@Nonnull OboAuthenticatorCertImpl authenticator, @Nonnull String username) {
    this.authenticator = authenticator;
    this.username = username;
    this.userId = null;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public String getSessionToken() {
    return this.sessionToken;
  }

  @Nullable
  @Override
  public String getAuthorizationToken() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public String getKeyManagerToken() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() throws AuthUnauthorizedException {
    if (this.userId != null) {
      this.sessionToken = this.authenticator.retrieveOboSessionTokenByUserId(this.userId);
    } else if (this.username != null) {
      this.sessionToken = this.authenticator.retrieveOboSessionTokenByUsername(this.username);
    } else {
      throw new IllegalStateException("Both userId and username are null. One of them is mandatory to perform OBO authentication refresh.");
    }
  }

  @Override
  public void refreshAuthToken() {
  }

  /**
   * This method is only visible for testing.
   */
  protected OboAuthenticator getAuthenticator() {
    return authenticator;
  }
}
