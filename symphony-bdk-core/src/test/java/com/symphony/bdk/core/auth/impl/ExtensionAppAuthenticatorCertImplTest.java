package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.test.MockApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtensionAppAuthenticatorCertImplTest {
  private static final String V1_EXTENSION_APP_AUTHENTICATE = "/sessionauth/v1/authenticate/extensionApp";
  public static final String V1_APP_POD_CERTIFICATE = "/sessionauth/v1/app/pod/certificate";

  private ExtensionAppAuthenticatorCertImpl authenticator;
  private MockApiClient mockApiClient;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authenticator = new ExtensionAppAuthenticatorCertImpl(
        "appId",
        mockApiClient.getApiClient("/sessionauth"));
  }

  @Test
  void testAuthenticateExtensionApp() throws AuthUnauthorizedException {
    mockApiClient.onPost(V1_EXTENSION_APP_AUTHENTICATE, "{\n"
        + "  \"appId\" : \"appId\",\n"
        + "  \"appToken\" : \"APP_TOKEN\",\n"
        + "  \"symphonyToken\" : \"SYMPHONY_TOKEN\",\n"
        + "  \"expireAt\" : 1539636528288\n"
        + "}");

    final AppAuthSession session = this.authenticator.authenticateExtensionApp("APP_TOKEN");

    assertEquals(AppAuthSessionCertImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AppAuthSessionCertImpl) session).getAuthenticator());
    assertEquals(session.getAppToken(), "APP_TOKEN");
    assertEquals(session.getSymphonyToken(), "SYMPHONY_TOKEN");
    assertEquals(session.expireAt(), 1539636528288L);
  }

  @Test
  void testRetrieveExtensionAppSessionUnauthorized() {
    mockApiClient.onPost(401, V1_EXTENSION_APP_AUTHENTICATE, "{}");

    assertThrows(AuthUnauthorizedException.class, () -> this.authenticator.authenticateExtensionApp("APP_TOKEN"));
  }

  @Test
  void testRetrieveExtensionAppSessionApiException() {
    mockApiClient.onPost(400, V1_EXTENSION_APP_AUTHENTICATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.authenticator.authenticateExtensionApp("APP_TOKEN"));
  }

  @Test
  void testGetPodCertificate() {
    mockApiClient.onGet(200, V1_APP_POD_CERTIFICATE, "{ \"certificate\" : \"PEM_content\"}");

    assertEquals("PEM_content", this.authenticator.getPodCertificate().getCertificate());
  }

  @Test
  void testGetPodCertificateFailure() {
    mockApiClient.onGet(500, V1_APP_POD_CERTIFICATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.authenticator.getPodCertificate().getCertificate());
  }
}
