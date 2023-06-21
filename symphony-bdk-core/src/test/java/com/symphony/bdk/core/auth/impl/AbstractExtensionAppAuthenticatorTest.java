package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.model.ExtensionAppTokens;
import com.symphony.bdk.gen.api.model.PodCertificate;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AbstractExtensionAppAuthenticatorTest {

  private static class TestExtAppAuthenticator extends AbstractExtensionAppAuthenticator {
    public TestExtAppAuthenticator(BdkRetryConfig retryConfig) {
      super(retryConfig, "appId");
    }

    @Override
    protected PodCertificate callGetPodCertificate() throws ApiException {
      return null;
    }

    @Override
    protected ExtensionAppTokens authenticateAndRetrieveTokens(String appToken) throws ApiException {
      return null;
    }

    @Override
    protected String getPodCertificateBasePath() {
      return "localhost.symphony.com";
    }

    @Override
    protected String getAuthenticationBasePath() {
      return "localhost.symphony.com";
    }

    @Nonnull
    @Override
    public AppAuthSession authenticateExtensionApp(String appToken) {
      return mock(AppAuthSession.class);
    }

    @Override
    public UserClaim validateJwt(String jwt) {
      return null;
    }
  }

  // test getCertificate()
  @Test
  void testGetPodCertificateSuccess() throws ApiException {
    final String certificate = "cert";

    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doReturn(new PodCertificate().certificate(certificate)).when(authenticator).callGetPodCertificate();

    assertEquals(certificate, authenticator.getPodCertificate().getCertificate());
    verify(authenticator, times(1)).callGetPodCertificate();
  }

  @Test
  void testGetPodCertificateUnauthorized() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(401, "")).when(authenticator).callGetPodCertificate();

    assertThrows(ApiRuntimeException.class, () -> authenticator.getPodCertificate().getCertificate());
    verify(authenticator, times(1)).callGetPodCertificate();
  }

  @Test
  void testGetPodCertificateUnexpectedApiException() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(404, "")).when(authenticator).callGetPodCertificate();
  }

  @Test
  void testGetPodCertificateShouldRetry() throws ApiException {
    final String certificate = "cert";

    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new SocketTimeoutException()))
        .doReturn(new PodCertificate().certificate(certificate))
        .when(authenticator).callGetPodCertificate();

    assertEquals(certificate, authenticator.getPodCertificate().getCertificate());
    verify(authenticator, times(4)).callGetPodCertificate();
  }

  @Test
  void testGetPodCertificateRetriesExhausted() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(503, "")).when(authenticator).callGetPodCertificate();

    assertThrows(ApiRuntimeException.class, () -> authenticator.getPodCertificate());
    verify(authenticator, times(2)).callGetPodCertificate();
  }

  // test authenticateAndRetrieveTokens
  @Test
  void testGetTokensSuccess() throws ApiException, AuthUnauthorizedException {
    final ExtensionAppTokens extensionAppTokens =
        new ExtensionAppTokens().appId("appId").appToken("Ta").symphonyToken("Ts");

    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doReturn(extensionAppTokens).when(authenticator).authenticateAndRetrieveTokens(anyString());

    assertEquals(extensionAppTokens, authenticator.retrieveExtAppTokens(""));
    verify(authenticator, times(1)).authenticateAndRetrieveTokens(anyString());
  }

  @Test
  void testGetTokensUnauthorized() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(401, "")).when(authenticator).authenticateAndRetrieveTokens(anyString());

    assertThrows(AuthUnauthorizedException.class, () -> authenticator.retrieveExtAppTokens(""));
    verify(authenticator, times(1)).authenticateAndRetrieveTokens(anyString());
  }

  @Test
  void testGetTokensUnexpectedApiException() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(404, "")).when(authenticator).authenticateAndRetrieveTokens(anyString());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveExtAppTokens(""));
    verify(authenticator, times(1)).authenticateAndRetrieveTokens(anyString());
  }

  @Test
  void testGetTokensShouldRetry() throws ApiException, AuthUnauthorizedException {
    final ExtensionAppTokens extensionAppTokens =
        new ExtensionAppTokens().appId("appId").appToken("Ta").symphonyToken("Ts");

    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new ConnectException()))
        .doReturn(extensionAppTokens)
        .when(authenticator).authenticateAndRetrieveTokens(anyString());

    assertEquals(extensionAppTokens, authenticator.retrieveExtAppTokens(""));
    verify(authenticator, times(4)).authenticateAndRetrieveTokens(anyString());
  }

  @Test
  void testGetTokensRetriesExhausted() throws ApiException {
    AbstractExtensionAppAuthenticator authenticator =
        spy(new TestExtAppAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(429, "")).when(authenticator).authenticateAndRetrieveTokens(anyString());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveExtAppTokens(""));
    verify(authenticator, times(2)).authenticateAndRetrieveTokens(anyString());
  }
}
