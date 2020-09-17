package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AuthSessionExtensionAppRsaImplTest {

  @Test
  void testRefresh() throws AuthUnauthorizedException {

    final String appToken = UUID.randomUUID().toString();
    final String symphonyToken = UUID.randomUUID().toString();
    final Long expireAt = System.currentTimeMillis();

    ExtensionAppTokens appTokens = new ExtensionAppTokens();
    appTokens.appId("appId");
    appTokens.appToken(appToken);
    appTokens.symphonyToken(symphonyToken);
    appTokens.expireAt(expireAt);

    final ExtensionAppAuthenticatorRsaImpl auth = mock(ExtensionAppAuthenticatorRsaImpl.class);
    when(auth.retrieveExtensionAppSession(appToken)).thenReturn(appTokens);

    final AppAuthSession session = new AppAuthSessionRsaImpl(auth, appToken);
    session.refresh();

    assertEquals(symphonyToken, session.getSymphonyToken());
    assertEquals(appToken, session.getAppToken());
    assertEquals(expireAt, session.expireAt());

    verify(auth, times(1)).retrieveExtensionAppSession(appToken);
  }
}
