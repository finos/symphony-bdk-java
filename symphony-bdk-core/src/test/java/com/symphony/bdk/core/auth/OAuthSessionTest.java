package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuthSessionTest {
  private final AuthSession authSession = mock(AuthSession.class);

  public static final String JWT = "Bearer eyJraWQiOiJGNG5Xak9WbTRBZU9JYUtEL2JCUWNleXI5MW89IiwiYWxnIjoiUlMyNTYifQ."
      + "eyJleHAiOjE2NDEzMDgyNzgsInN1YiI6IjEzMDU2NzAwNTgwOTE1IiwiZXh0X3BvZF9pZCI6MTkwLCJwb2xpY3lfaWQiOiJhcHAiLCJlbnRpdGx"
      + "lbWVudHMiOiIifQ.signature";

  @Test
  void testRefresh() throws AuthUnauthorizedException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);

    OAuthSession oAuthSession = new OAuthSession(authSession);
    oAuthSession.refresh();

    verify(authSession).refreshAuthToken();
  }

  @Test
  void testRefreshWhenNoAuthToken() throws AuthUnauthorizedException {
    when(authSession.getAuthorizationToken()).thenReturn(null);

    OAuthSession oAuthSession = new OAuthSession(authSession);
    oAuthSession.refresh();

    verify(authSession, times(0)).refreshAuthToken();
  }

  @Test
  void getBearerToken() {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    OAuthSession oAuthSession = new OAuthSession(authSession);

    assertEquals(JWT, oAuthSession.getBearerToken());
  }
}
