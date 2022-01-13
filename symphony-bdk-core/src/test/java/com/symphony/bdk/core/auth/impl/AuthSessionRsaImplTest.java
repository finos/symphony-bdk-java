package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.Token;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Test class for the {@link AuthSessionImpl}.
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

    final AuthSessionImpl session = new AuthSessionImpl(auth);
    session.refresh();

    assertEquals(sessionToken, session.getSessionToken());
    assertEquals(kmToken, session.getKeyManagerToken());

    verify(auth, times(1)).retrieveAuthToken();
    verify(auth, times(1)).retrieveKeyManagerToken();
  }

  @Test
  void testRefreshAuthTokenException() throws AuthUnauthorizedException {

    Token token = new Token();
    token.setAuthorizationToken("Invalid jwt");

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveAuthToken()).thenReturn(token);

    assertThrows(AuthUnauthorizedException.class, new AuthSessionImpl(auth)::refresh);
  }

}
