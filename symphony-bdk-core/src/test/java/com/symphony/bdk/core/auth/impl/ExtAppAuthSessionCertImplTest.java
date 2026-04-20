package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.junit.jupiter.api.Test;

class ExtAppAuthSessionCertImplTest {

  @Test
  void testRefresh() throws AuthUnauthorizedException {
    final ExtAppAuthenticatorCertImpl authenticator = mock(ExtAppAuthenticatorCertImpl.class);
    final ExtAppAuthSessionCertImpl session = new ExtAppAuthSessionCertImpl(authenticator);

    when(authenticator.retrieveAppSessionToken()).thenReturn("appSessionToken");

    session.refresh();
    assertEquals("appSessionToken", session.getAppSession());
  }

  @Test
  void testRefreshAuthUnauthorized() throws AuthUnauthorizedException {
    final ExtAppAuthenticatorCertImpl authenticator = mock(ExtAppAuthenticatorCertImpl.class);
    final ExtAppAuthSessionCertImpl session = new ExtAppAuthSessionCertImpl(authenticator);

    when(authenticator.retrieveAppSessionToken()).thenThrow(new AuthUnauthorizedException(""));

    assertThrows(AuthUnauthorizedException.class, session::refresh);
  }
}
