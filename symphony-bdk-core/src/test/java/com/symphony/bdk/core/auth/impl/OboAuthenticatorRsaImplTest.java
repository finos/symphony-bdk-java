package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.core.test.RsaTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link OboAuthenticatorRsaImpl}.
 */
@ExtendWith(BdkMockServerExtension.class)
class OboAuthenticatorRsaImplTest {

  private OboAuthenticatorRsaImpl authenticator;

  @BeforeEach
  void init(final BdkMockServer mockServer) {

    this.authenticator = new OboAuthenticatorRsaImpl(
        "appId",
        RsaTestHelper.generateKeyPair().getPrivate(),
        mockServer.newApiClient("/login")
    );
  }

  @Test
  void testAuthenticateByUsername(final BdkMockServer mockServer) throws AuthUnauthorizedException {
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by username
    mockServer.onPost("/login/pubkey/app/username/" + "username" + "/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    final AuthSession session = this.authenticator.authenticateByUsername("username");
    assertNotNull(session);
    assertEquals(AuthSessionOboImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AuthSessionOboImpl) session).getAuthenticator());
  }

  @Test
  void testAuthenticateByUserId(final BdkMockServer mockServer) throws AuthUnauthorizedException {
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by userId
    mockServer.onPost("/login/pubkey/app/user/" + 1234L + "/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    final AuthSession session = this.authenticator.authenticateByUserId(1234L);
    assertNotNull(session);
    assertEquals(AuthSessionOboImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AuthSessionOboImpl) session).getAuthenticator());
  }

  @Test
  void testRetrieveOboSessionTokenByUsername(final BdkMockServer mockServer) throws AuthUnauthorizedException {

    final String username = UUID.randomUUID().toString();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by username
    mockServer.onPost("/login/pubkey/app/username/" + username + "/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));

    final String sessionToken = this.authenticator.retrieveOboSessionTokenByUsername(username);
    assertEquals("1234", sessionToken);
  }

  @Test
  void testUnauthorizedToRetrieveOboSessionTokenByUsername(final BdkMockServer mockServer) {

    final String username = UUID.randomUUID().toString();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by username
    mockServer.onPost("/login/pubkey/app/username/" + username + "/authenticate", res -> res.withStatusCode(401));
    // assert ex is thrown
    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.retrieveOboSessionTokenByUsername(username));
  }

  @Test
  void testUnableToRetrieveOboSessionTokenByUsername(final BdkMockServer mockServer) {

    final String username = UUID.randomUUID().toString();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by username
    mockServer.onPost("/login/pubkey/app/username/" + username + "/authenticate", res -> res.withStatusCode(503));
    // assert ex is thrown
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.retrieveOboSessionTokenByUsername(username));
  }

  @Test
  void testRetrieveOboSessionTokenByUserId(final BdkMockServer mockServer) throws AuthUnauthorizedException {

    final long userId = System.currentTimeMillis();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by userId
    mockServer.onPost("/login/pubkey/app/user/" + userId + "/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));

    final String sessionToken = this.authenticator.retrieveOboSessionTokenByUserId(userId);
    assertEquals("1234", sessionToken);
  }

  @Test
  void testUnauthorizedToRetrieveOboSessionTokenByUserId(final BdkMockServer mockServer) {

    final long userId = System.currentTimeMillis();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by userId
    mockServer.onPost("/login/pubkey/app/user/" + userId + "/authenticate", res -> res.withStatusCode(401));
    // assert ex is thrown
    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.retrieveOboSessionTokenByUserId(userId));
  }

  @Test
  void testUnableToRetrieveOboSessionTokenByUserId(final BdkMockServer mockServer) {

    final long userId = System.currentTimeMillis();

    // mock app authentication
    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
    // mock obo session retrieval by userId
    mockServer.onPost("/login/pubkey/app/user/" + userId + "/authenticate", res -> res.withStatusCode(503));
    // assert ex is thrown
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.retrieveOboSessionTokenByUserId(userId));
  }

  @Test
  void testAppAuthUnauthorized(final BdkMockServer mockServer) {

    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withStatusCode(401));

    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.retrieveAppSessionToken());
  }

  @Test
  void testAppAuthUnknownApiException(final BdkMockServer mockServer) {

    mockServer.onPost("/login/pubkey/app/authenticate", res -> res.withStatusCode(503));

    assertThrows(ApiRuntimeException.class, () -> this.authenticator.retrieveAppSessionToken());
  }
}
