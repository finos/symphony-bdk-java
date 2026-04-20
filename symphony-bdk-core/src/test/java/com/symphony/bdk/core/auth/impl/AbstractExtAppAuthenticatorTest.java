package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import jakarta.ws.rs.ProcessingException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;

class AbstractExtAppAuthenticatorTest {

  private static class TestExtAppAuthenticator extends AbstractExtAppAuthenticator {
    public TestExtAppAuthenticator(BdkRetryConfig retryConfig) {
      super(retryConfig, "appId");
    }

    @Override
    protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
      return null;
    }

    @Override
    protected String getBasePath() {
      return "localhost.symphony.com";
    }

    @NotNull
    @Override
    public ExtAppAuthSession authenticateExtApp() throws AuthUnauthorizedException {
      return null;
    }
  }

  @Test
  void testRetrieveAppSessionTokenSuccess() throws ApiException, AuthUnauthorizedException {
    final String appSessionToken = "appSessionToken";
    final AbstractExtAppAuthenticator authenticator = spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doReturn(appSessionToken).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertEquals(appSessionToken, authenticator.retrieveAppSessionToken());
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenShouldRetry() throws ApiException, AuthUnauthorizedException {
    final String appSessionToken = "appSessionToken";
    final AbstractExtAppAuthenticator authenticator = spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new ConnectException()))
        .doReturn(appSessionToken)
        .when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertEquals(appSessionToken, authenticator.retrieveAppSessionToken());
    verify(authenticator, times(4)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenRetriesExhausted() throws ApiException {
    final AbstractExtAppAuthenticator authenticator = spy(new TestExtAppAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(503, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(ApiRuntimeException.class, authenticator::retrieveAppSessionToken);
    verify(authenticator, times(2)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenUnauthorized() throws ApiException {
    final AbstractExtAppAuthenticator authenticator = spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(401, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(AuthUnauthorizedException.class, authenticator::retrieveAppSessionToken);
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenUnexpectedApiException() throws ApiException {
    final AbstractExtAppAuthenticator authenticator = spy(new TestExtAppAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(404, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(ApiRuntimeException.class, authenticator::retrieveAppSessionToken);
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }
}
