package com.symphony.bdk.core.auth.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.core.test.RsaTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtensionAppAuthenticatorRsaImplTest {

  private static final String V1_EXTENSION_APP_AUTHENTICATE = "/login/v1/pubkey/app/authenticate/extensionApp";
  public static final String V1_POD_CERT = "/pod/v1/podcert";

  private ExtensionAppAuthenticatorRsaImpl authenticator;
  private MockApiClient mockApiClient;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authenticator = new ExtensionAppAuthenticatorRsaImpl(
        "appId",
        RsaTestHelper.generateKeyPair().getPrivate(),
        mockApiClient.getApiClient("/login"),
        mockApiClient.getApiClient("/pod"));
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

    assertEquals(AppAuthSessionRsaImpl.class, session.getClass());
    assertEquals(this.authenticator, ((AppAuthSessionRsaImpl) session).getAuthenticator());
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
    mockApiClient.onGet(200, V1_POD_CERT, "{ \"certificate\" : \"PEM_content\"}");

    assertEquals("PEM_content", this.authenticator.getPodCertificate().getCertificate());
  }

  @Test
  void testGetPodCertificateFailure() {
    mockApiClient.onGet(500, V1_POD_CERT, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.authenticator.getPodCertificate().getCertificate());
  }
}
