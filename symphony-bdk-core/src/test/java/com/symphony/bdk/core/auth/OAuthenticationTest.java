package com.symphony.bdk.core.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuthenticationTest {
  private Map<String, String> headerParams;
  private OAuthentication auth;

  @BeforeEach
  void setUp() {
    this.headerParams = new HashMap<>();
    AuthSession authSession = mock(AuthSession.class);
    when(authSession.getAuthorizationToken()).thenReturn("Bearer jwt");
    final OAuthSession oAuthSession = new OAuthSession(authSession);
    this.auth = new OAuthentication(oAuthSession::getBearerToken);
  }

  @Test
  void testApplyWhenSessionTokenPresent() {
    headerParams.put("sessionToken", "sessionValue");

    auth.apply(headerParams);

    assertFalse(headerParams.containsKey("sessionToken"));
    assertTrue(headerParams.containsKey("Authorization"));
  }

  @Test
  void testApplyWhenNoSessionToken() {
    auth.apply(headerParams);

    assertFalse(headerParams.containsKey("sessionToken"));
    assertTrue(headerParams.containsKey("Authorization"));
  }
}
