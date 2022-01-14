package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.auth.JwtHelperTest.JWT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.Token;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class AuthSessionImplTest {

  @Test
  void testRefresh() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    Token authToken = new Token();
    authToken.setToken(sessionToken);
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(authToken);
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);

    final AuthSessionImpl session = new AuthSessionImpl(auth);
    session.refresh();

    assertEquals(sessionToken, session.getSessionToken());
    assertEquals(kmToken, session.getKeyManagerToken());

    verify(auth, times(1)).retrieveSessionToken();
    verify(auth, times(1)).retrieveKeyManagerToken();
  }

  @Test
  void testRefreshAuthTokenException() throws AuthUnauthorizedException {

    Token token = new Token();
    token.setAuthorizationToken("Invalid jwt");

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(token);

    assertThrows(AuthUnauthorizedException.class, new AuthSessionImpl(auth)::refresh);
  }

  @Test
  void testRefreshBearerTokenOnly() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(getToken(sessionToken));
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);
    when(auth.isCommonJwtEnabled()).thenReturn(true);
    when(auth.retrieveAuthorizationToken(sessionToken)).thenReturn(JWT);

    final AuthSessionImpl session = new AuthSessionImpl(auth);

    // first refresh initialise the tokens
    session.refresh();

    verify(auth, times(1)).retrieveSessionToken();
    verify(auth, never()).retrieveAuthorizationToken(any());

    // second refresh should try only with the bearer token
    session.refresh();

    verify(auth).retrieveAuthorizationToken(any());
  }

  @Test
  void testGetAuthorizationTokenWithRefresh() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(getToken(sessionToken));
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);
    when(auth.isCommonJwtEnabled()).thenReturn(true);
    when(auth.retrieveAuthorizationToken(sessionToken)).thenReturn(JWT);

    final AuthSessionImpl session = new AuthSessionImpl(auth);

    // first refresh initialise the tokens
    session.refresh();

    verify(auth, times(1)).retrieveSessionToken();
    verify(auth, never()).retrieveAuthorizationToken(any());

    // getting auth token checks if token is expired and refresh bearer token only
    session.getAuthorizationToken();

    verify(auth).retrieveAuthorizationToken(any());
    verify(auth, times(1)).retrieveSessionToken();
  }

  @Test
  void testGetAuthorizationTokenWhenNotSupported() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    final String kmToken = UUID.randomUUID().toString();
    Token authToken = new Token();
    authToken.setToken(sessionToken);
    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(authToken);
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);
    when(auth.isCommonJwtEnabled()).thenReturn(true);

    final AuthSessionImpl session = new AuthSessionImpl(auth);

    assertThrows(UnsupportedOperationException.class, session::getAuthorizationToken);
  }

  @Test
  void testGetAuthorizationTokenWhenRefreshBearerFails() throws AuthUnauthorizedException {

    final String sessionToken = UUID.randomUUID().toString();
    final String kmToken = UUID.randomUUID().toString();

    final BotAuthenticatorRsaImpl auth = mock(BotAuthenticatorRsaImpl.class);
    when(auth.retrieveSessionToken()).thenReturn(getToken(sessionToken));
    when(auth.retrieveKeyManagerToken()).thenReturn(kmToken);
    when(auth.isCommonJwtEnabled()).thenReturn(true);
    when(auth.retrieveAuthorizationToken(sessionToken)).thenThrow(AuthUnauthorizedException.class);

    final AuthSessionImpl session = new AuthSessionImpl(auth);

    // first refresh initialise the tokens
    session.refresh();

    verify(auth, times(1)).retrieveSessionToken();
    verify(auth, never()).retrieveAuthorizationToken(any());

    // getting auth token fails then we refresh all tokens
    session.getAuthorizationToken();

    verify(auth).retrieveAuthorizationToken(any());
    verify(auth, times(2)).retrieveSessionToken();
  }

  private Token getToken(String sessionToken) {
    Token authToken = new Token();
    authToken.setToken(sessionToken);
    authToken.setAuthorizationToken(JWT);
    return authToken;
  }

}
