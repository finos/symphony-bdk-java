package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.junit.jupiter.api.Test;

class ExtAppAuthSessionImplTest {

  @Test
  void testRefresh() throws AuthUnauthorizedException {
    final ExtAppAuthenticatorRsaImpl authenticator = mock(ExtAppAuthenticatorRsaImpl.class);
    final ExtAppAuthSessionImpl session = new ExtAppAuthSessionImpl(authenticator);

    when(authenticator.retrieveAppSessionToken()).thenReturn("appSessionToken");

    session.refresh();
    assertEquals("appSessionToken", session.getAppSession());
  }

  @Test
  void testRefreshAuthUnauthorized() throws AuthUnauthorizedException {
    final ExtAppAuthenticatorRsaImpl authenticator = mock(ExtAppAuthenticatorRsaImpl.class);
    final ExtAppAuthSessionImpl session = new ExtAppAuthSessionImpl(authenticator);

    when(authenticator.retrieveAppSessionToken()).thenThrow(new AuthUnauthorizedException(""));

    assertThrows(AuthUnauthorizedException.class, session::refresh);
  }
}
