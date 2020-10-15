package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppTokensRepository;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.core.test.RsaTestHelper;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ExtensionAppAuthenticatorRsaImplTest {

  private static final String V1_EXTENSION_APP_AUTHENTICATE = "/login/v1/pubkey/app/authenticate/extensionApp";
  public static final String V1_POD_CERT = "/pod/v1/podcert";

  private ExtensionAppAuthenticatorRsaImpl authenticator;
  private MockApiClient mockApiClient;
  private ExtensionAppTokensRepository tokensRepository;

  @BeforeEach
  void init() {
    mockApiClient = new MockApiClient();
    tokensRepository = spy(new InMemoryTokensRepository());
    authenticator = new ExtensionAppAuthenticatorRsaImpl(
        BdkRetryConfig.ofMinimalInterval(1),
        "appId",
        RsaTestHelper.generateKeyPair().getPrivate(),
        mockApiClient.getApiClient("/login"),
        mockApiClient.getApiClient("/pod"),
        tokensRepository);
  }

  @Test
  void testAuthenticateExtensionApp() throws AuthUnauthorizedException {
    final String appToken = "APP_TOKEN";
    final String symphonyToken = "SYMPHONY_TOKEN";

    mockApiClient.onPost(V1_EXTENSION_APP_AUTHENTICATE, "{\n"
        + "  \"appId\" : \"appId\",\n"
        + "  \"appToken\" : \"" + appToken + "\",\n"
        + "  \"symphonyToken\" : \"" + symphonyToken + "\",\n"
        + "  \"expireAt\" : 1539636528288\n"
        + "}");

    final AppAuthSession session = authenticator.authenticateExtensionApp(appToken);

    assertEquals(AppAuthSessionRsaImpl.class, session.getClass());
    assertEquals(authenticator, ((AppAuthSessionRsaImpl) session).getAuthenticator());
    assertEquals(session.getAppToken(), appToken);
    assertEquals(session.getSymphonyToken(), symphonyToken);
    assertEquals(session.expireAt(), 1539636528288L);

    verify(tokensRepository).save(eq(appToken), eq(symphonyToken));
    assertTrue(authenticator.validateTokens(appToken, symphonyToken));
    assertFalse(authenticator.validateTokens("OTHER_TOKEN", symphonyToken));
    assertFalse(authenticator.validateTokens(appToken, "OTHER_TOKEN"));
  }

  @Test
  void testRetrieveExtensionAppSessionUnauthorized() {
    mockApiClient.onPost(401, V1_EXTENSION_APP_AUTHENTICATE, "{}");

    assertThrows(AuthUnauthorizedException.class, () -> authenticator.authenticateExtensionApp("APP_TOKEN"));
  }

  @Test
  void testRetrieveExtensionAppSessionApiException() {
    mockApiClient.onPost(400, V1_EXTENSION_APP_AUTHENTICATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> authenticator.authenticateExtensionApp("APP_TOKEN"));
  }

  @Test
  void testGetPodCertificate() {
    mockApiClient.onGet(200, V1_POD_CERT, "{ \"certificate\" : \"PEM_content\"}");

    assertEquals("PEM_content", authenticator.getPodCertificate().getCertificate());
  }

  @Test
  void testGetPodCertificateFailure() {
    mockApiClient.onGet(500, V1_POD_CERT, "{}");

    assertThrows(ApiRuntimeException.class, () -> authenticator.getPodCertificate().getCertificate());
  }
}
