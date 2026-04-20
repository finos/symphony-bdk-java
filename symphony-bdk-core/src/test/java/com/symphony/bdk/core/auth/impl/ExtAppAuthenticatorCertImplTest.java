package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BdkMockServerExtension.class)
class ExtAppAuthenticatorCertImplTest {

  private ExtAppAuthenticatorCertImpl authenticator;

  @BeforeEach
  void init(final BdkMockServer mockServer) {
    this.authenticator = new ExtAppAuthenticatorCertImpl(
        ofMinimalInterval(1),
        "appId",
        mockServer.newApiClient("/login")
    );
  }

  @Test
  void testAuthenticateExtApp(final BdkMockServer mockServer) throws AuthUnauthorizedException {
    mockServer.onPost("/login/v1/app/authenticate", res -> res.withBody("{ \"token\": \"1234\" }"));

    final ExtAppAuthSession session = this.authenticator.authenticateExtApp();
    assertNotNull(session);
    assertEquals(ExtAppAuthSessionCertImpl.class, session.getClass());
    assertEquals(this.authenticator, ((ExtAppAuthSessionCertImpl) session).getAuthenticator());
  }

  @Test
  void testRetrieveAppSessionToken(final BdkMockServer mockServer) throws AuthUnauthorizedException {
    mockServer.onPost("/login/v1/app/authenticate", res -> res.withBody("{ \"token\": \"1234\" }"));

    final String appSessionToken = this.authenticator.retrieveAppSessionToken();
    assertEquals("1234", appSessionToken);
  }

  @Test
  void testAuthUnauthorizedException(final BdkMockServer mockServer) {
    mockServer.onPost("/login/v1/app/authenticate", res -> res.withStatusCode(401));

    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.retrieveAppSessionToken());
  }

  @Test
  void testUnknownApiException(final BdkMockServer mockServer) {
    mockServer.onPost("/login/v1/app/authenticate", res -> res.withStatusCode(503));

    assertThrows(ApiRuntimeException.class, () -> this.authenticator.retrieveAppSessionToken());
  }
}
