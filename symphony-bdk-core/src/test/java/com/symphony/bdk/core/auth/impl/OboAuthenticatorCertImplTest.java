package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.test.MockApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OboAuthenticatorCertImplTest {

  private static final String APP_AUTHENTICATE = "/sessionauth/v1/app/authenticate";
  private static final String APP_AUTHENTICATE_OBO_USERNAME = "/sessionauth/v1/app/username/{username}/authenticate";
  private static final String APP_AUTHENTICATE_OBO_USERID = "/sessionauth/v1/app/user/{uid}/authenticate";

  private OboAuthenticatorCertImpl authenticator;
  private MockApiClient mockApiClient;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authenticator = new OboAuthenticatorCertImpl(
        "appId",
        this.mockApiClient.getApiClient("/sessionauth")
    );
  }

  @Test
  void testAuthenticateByUsername() throws AuthUnauthorizedException {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(APP_AUTHENTICATE_OBO_USERNAME.replace("{username}", "username"), "{ \"sessionToken\": \"1234\" }");
    final AuthSession session = this.authenticator.authenticateByUsername("username");
    assertNotNull(session);
    assertEquals(AuthSessionOboCertImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AuthSessionOboCertImpl) session).getAuthenticator());
    assertEquals(session.getSessionToken(), "1234");
    assertNull(session.getKeyManagerToken());
  }

  @Test
  void testAuthenticateByUserId() throws AuthUnauthorizedException {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(APP_AUTHENTICATE_OBO_USERID.replace("{uid}", "123456"), "{ \"sessionToken\": \"1234\" }");
    final AuthSession session = this.authenticator.authenticateByUserId(123456L);
    assertNotNull(session);
    assertEquals(AuthSessionOboCertImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AuthSessionOboCertImpl) session).getAuthenticator());
    assertEquals(session.getSessionToken(), "1234");
    assertNull(session.getKeyManagerToken());
  }

  @Test
  void testRetrieveAppSessionTokenUnauthorized() {
    this.mockApiClient.onPost(401, APP_AUTHENTICATE, "{}");
    assertThrows(AuthUnauthorizedException.class, this.authenticator::retrieveAppSessionToken);
  }

  @Test
  void testRetrieveAppSessionTokenApiException() {
    this.mockApiClient.onPost(400, APP_AUTHENTICATE, "{}");
    assertThrows(ApiRuntimeException.class, this.authenticator::retrieveAppSessionToken);
  }

  @Test
  void testAuthenticateByUsernameUnauthorized() {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(401, APP_AUTHENTICATE_OBO_USERNAME.replace("{username}", "username"), "{}");
    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.authenticateByUsername("username"));
  }

  @Test
  void testAuthenticateByUsernameApiException() {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(400, APP_AUTHENTICATE_OBO_USERNAME.replace("{username}", "username"), "{}");
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.authenticateByUsername("username"));
    this.mockApiClient.onPost(500, APP_AUTHENTICATE_OBO_USERNAME.replace("{username}", "username"), "{}");
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.authenticateByUsername("username"));
  }

  @Test
  void testAuthenticateByUserIdUnauthorized() {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(401, APP_AUTHENTICATE_OBO_USERID.replace("{uid}", "123456"), "{}");
    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.authenticateByUserId(123456L));
  }

  @Test
  void testAuthenticateByUserIdApiException() {
    this.mockApiClient.onPost(APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(400, APP_AUTHENTICATE_OBO_USERID.replace("{uid}", "123456"), "{}");
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.authenticateByUserId(123456L));
    this.mockApiClient.onPost(500, APP_AUTHENTICATE_OBO_USERID.replace("{uid}", "123456"), "{}");
    assertThrows(ApiRuntimeException.class, () -> this.authenticator.authenticateByUserId(123456L));
  }
}
