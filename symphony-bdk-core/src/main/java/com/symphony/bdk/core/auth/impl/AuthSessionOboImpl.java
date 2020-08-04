package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthenticationException;

import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionOboImpl implements AuthSession {

  private final OboAuthenticatorRSAImpl authenticator;
  private final Long userId;
  private final String username;

  private String sessionToken;

  public AuthSessionOboImpl(OboAuthenticatorRSAImpl authenticator, Long userId) {
    this.authenticator = authenticator;
    this.userId = userId;
    this.username = null;
  }

  public AuthSessionOboImpl(OboAuthenticatorRSAImpl authenticator, String username) {
    this.authenticator = authenticator;
    this.userId = null;
    this.username = username;
  }

  @Override
  public String getSessionToken() {
    return this.sessionToken;
  }

  @Override
  public String getKeyManagerToken() {
    return null;
  }

  @Override
  public void refresh() throws AuthenticationException {
    if (this.userId != null) {
      this.sessionToken = this.authenticator.retrieveOboSessionTokenByUserID(this.userId);
    } else if (this.username != null) {
      this.sessionToken = this.authenticator.retrieveOboSessionTokenByUsername(this.username);
    } else {
      throw new IllegalStateException("Both userId and username are null. One of them is mandatory to perform OBO authentication refresh.");
    }
  }
}
