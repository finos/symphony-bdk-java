package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.Token;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for the {@link AuthSessionRsaImpl}.
 */
class AuthSessionRsaImplTest {

  @Test
  void testRefresh() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    Token authToken = new Token();
    authToken.setToken(sessionToken);
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveAuthToken()).thenReturn(authToken);
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);

    final AuthSessionRsaImpl session = new AuthSessionRsaImpl(auth);
    session.refresh();

    assertEquals(sessionToken, session.getSessionToken());
    assertEquals(kmToken, session.getKeyManagerToken());

    verify(auth, times(1)).retrieveAuthToken();
    verify(auth, times(1)).retrieveKeyManagerToken();
  }

  @Test
  void testRefreshAuthToken() throws AuthUnauthorizedException {

    String authToken = "Bearer qwerty";
    Token token = new Token();
    token.setAuthorizationToken(authToken);

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveAuthToken()).thenReturn(token);

    final AuthSessionRsaImpl session = new AuthSessionRsaImpl(auth);
    session.refreshAuthToken();

    assertEquals(authToken, session.getAuthorizationToken());

    verify(auth, times(1)).retrieveAuthToken();
  }
}
