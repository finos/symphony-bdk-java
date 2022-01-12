package com.symphony.bdk.core.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.Test;

import java.time.Instant;

class OAuthSessionTest {
  private final AuthSession authSession = mock(AuthSession.class);

  public static final String JWT = "Bearer eyJraWQiOiJGNG5Xak9WbTRBZU9JYUtEL2JCUWNleXI5MW89IiwiYWxnIjoiUlMyNTYifQ."
      + "eyJleHAiOjE2NDEzMDgyNzgsInN1YiI6IjEzMDU2NzAwNTgwOTE1IiwiZXh0X3BvZF9pZCI6MTkwLCJwb2xpY3lfaWQiOiJhcHAiLCJlbnRpdGx"
      + "lbWVudHMiOiIifQ.signature";

  @Test
  void testRefreshExpiredToken() throws AuthUnauthorizedException, ApiException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    when(authSession.getAuthTokenExpirationDate()).thenReturn(Instant.now().getEpochSecond() - 20);

    OAuthSession oAuthSession = new OAuthSession(authSession);
    oAuthSession.getBearerToken();

    verify(authSession).refreshAuthToken();
  }

  @Test
  void testRefreshWhenTokenNotExpired() throws AuthUnauthorizedException, ApiException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    when(authSession.getAuthTokenExpirationDate()).thenReturn(Instant.now().getEpochSecond() + 20);

    OAuthSession oAuthSession = new OAuthSession(authSession);
    oAuthSession.getBearerToken();

    verify(authSession, never()).refreshAuthToken();
  }

  @Test
  void testRefreshWhenNoAuthToken() throws AuthUnauthorizedException, ApiException {
    when(authSession.getAuthorizationToken()).thenReturn(null);

    OAuthSession oAuthSession = new OAuthSession(authSession);
    assertThrows(UnsupportedOperationException.class, oAuthSession::getBearerToken);
  }

  @Test
  void testRefreshThrowsException() throws AuthUnauthorizedException {
    doThrow(AuthUnauthorizedException.class).when(authSession).refreshAuthToken();
    when(authSession.getAuthorizationToken()).thenReturn(JWT);

    OAuthSession oAuthSession = new OAuthSession(authSession);

    assertThrows(ApiException.class, oAuthSession::getBearerToken);
  }
}
