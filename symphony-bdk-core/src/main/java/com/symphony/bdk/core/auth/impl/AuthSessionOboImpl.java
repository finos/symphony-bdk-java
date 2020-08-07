package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link AuthSession} impl for OBO authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionOboImpl implements AuthSession {

  private final OboAuthenticatorRSAImpl authenticator;
  private final Long userId;
  private final String username;

  private String sessionToken;

  /**
   *
   * @param authenticator
   * @param userId
   */
  public AuthSessionOboImpl(@Nonnull OboAuthenticatorRSAImpl authenticator, @Nonnull Long userId) {
    this.authenticator = authenticator;
    this.userId = userId;
    this.username = null;
  }

  /**
   *
   * @param authenticator
   * @param username
   */
  public AuthSessionOboImpl(@Nonnull OboAuthenticatorRSAImpl authenticator, @Nonnull String username) {
    this.authenticator = authenticator;
    this.userId = null;
    this.username = username;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nonnull String getSessionToken() {
    return this.sessionToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable String getKeyManagerToken() {
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


  protected OboAuthenticatorRSAImpl getAuthenticator() {
    return authenticator;
  }
}
