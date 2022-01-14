package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.OAuthSession;
import com.symphony.bdk.core.auth.impl.OAuthentication;
import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class OAuthenticationTest {
  private Map<String, String> headerParams;
  private OAuthentication auth;

  @BeforeEach
  void setUp() throws AuthUnauthorizedException {
    this.headerParams = new HashMap<>();
    AuthSession authSession = mock(AuthSession.class);
    when(authSession.getAuthorizationToken()).thenReturn("Bearer jwt");
    final OAuthSession oAuthSession = new OAuthSession(authSession);
    this.auth = new OAuthentication(oAuthSession::getBearerToken);
  }

  @Test
  void testApplyWhenSessionTokenPresent() throws ApiException {
    headerParams.put("sessionToken", "sessionValue");

    auth.apply(headerParams);

    assertFalse(headerParams.containsKey("sessionToken"));
    assertTrue(headerParams.containsKey("Authorization"));
  }

  @Test
  void testApplyWhenNoSessionToken() throws ApiException {
    auth.apply(headerParams);

    assertFalse(headerParams.containsKey("sessionToken"));
    assertTrue(headerParams.containsKey("Authorization"));
  }

  @Test
  void testApplyWithException() throws ApiException {
    OAuthSession oAuthSession = mock(OAuthSession.class);
    when(oAuthSession.getBearerToken()).thenThrow(ApiException.class);
    this.auth = new OAuthentication(oAuthSession::getBearerToken);

    assertThrows(ApiException.class, ()-> auth.apply(headerParams));
  }
}
