package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.gen.api.model.Token;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apiguardian.api.API;

import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * {@link AuthSession} impl for regular authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class AuthSessionImpl implements AuthSession {

  public static final Duration LEEWAY = Duration.ofSeconds(5);
  private final AbstractBotAuthenticator authenticator;

  /**
   * Long-lived Session JWT Token (for pod APIs).
   */
  private String sessionToken;

  /**
   * Long-lived KM Token (for KM APIs).
   */
  private String keyManagerToken;

  /**
   * Short-lived access Token (for pod APIs).
   */
  private String authorizationToken;
  private Long authTokenExpirationDate;


  public AuthSessionImpl(@Nonnull AbstractBotAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable
  String getSessionToken() {
    return this.sessionToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable
  String getAuthorizationToken() throws AuthUnauthorizedException {
    if(this.authorizationToken == null || this.authTokenExpirationDate == null) {
      throw new UnsupportedOperationException("Common JWT feature is not available in your pod, "
          + "SBE version should be at least 20.14.");
    }
    if (Instant.now().plus(LEEWAY).isAfter(Instant.ofEpochSecond(authTokenExpirationDate))) {
      refresh();
    }
    return this.authorizationToken;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable
  String getKeyManagerToken() {
    return this.keyManagerToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() throws AuthUnauthorizedException {
    if (this.sessionToken == null || !authenticator.isCommonJwtEnabled()) {
      refreshAllTokens();
    } else {
      // as we are using a short-lived token and a refresh token, let's first try to refresh the short lived token
      // this way we avoid generating extra login events
      try {
        this.authorizationToken = authenticator.retrieveAuthorizationToken(sessionToken);
        refreshExpirationDate();
      } catch (AuthUnauthorizedException e) {
        refreshAllTokens();
      }
    }
  }

  private void refreshAllTokens() throws AuthUnauthorizedException {
    Token authToken = authenticator.retrieveSessionToken();
    this.authorizationToken = authToken.getAuthorizationToken();
    this.sessionToken = authToken.getToken();
    refreshExpirationDate();
    this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
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
  protected AbstractBotAuthenticator getAuthenticator() {
    return this.authenticator;
  }
}
