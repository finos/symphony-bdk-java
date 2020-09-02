package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
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
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(sessionToken);
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);

    final AuthSessionRsaImpl session = new AuthSessionRsaImpl(auth);
    session.refresh();

    assertEquals(sessionToken, session.getSessionToken());
    assertEquals(kmToken, session.getKeyManagerToken());

    verify(auth, times(1)).retrieveSessionToken();
    verify(auth, times(1)).retrieveKeyManagerToken();
  }
}
