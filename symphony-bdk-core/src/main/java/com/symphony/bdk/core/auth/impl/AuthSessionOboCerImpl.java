package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;

import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import lombok.NonNull;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * {@link AuthSession} impl for OBO Certificate authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionOboCerImpl implements AuthSession {

  private final OboAuthenticatorCertImpl authenticator;
  private final Long userId;
  private final String username;

  private String sessionToken;

  public AuthSessionOboCerImpl(@NonNull OboAuthenticatorCertImpl authenticator, @NonNull Long userId) {
    this.authenticator = authenticator;
    this.userId = userId;
    this.username = null;
  }

  public AuthSessionOboCerImpl(@NonNull OboAuthenticatorCertImpl authenticator, @NonNull String username) {
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
      this.sessionToken = this.authenticator.retrieveOboSessionTokenByUsername(username);
    } else {
      throw new IllegalStateException("Both userId and username are null. One of them is mandatory to perform OBO authentication refresh.");
    }
  }

  protected OboAuthenticator getAuthenticator() {
    return authenticator;
  }
}
