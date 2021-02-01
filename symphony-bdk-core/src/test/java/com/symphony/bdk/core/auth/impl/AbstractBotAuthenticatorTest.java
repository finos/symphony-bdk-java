package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.ws.rs.ProcessingException;

class AbstractBotAuthenticatorTest {

  private ApiClient apiClient;

  private static class TestBotAuthenticator extends AbstractBotAuthenticator {
    public TestBotAuthenticator(BdkRetryConfig retryConfig) {
      super(retryConfig);
    }

    @Override
    protected String authenticateAndGetToken(ApiClient client) throws ApiException {
      return null;
    }

    @Override
    protected String getBotUsername() {
      return null;
    }

    @Override
    @Nonnull
    public AuthSession authenticateBot() {
      return mock(AuthSession.class);
    }
  }

  @BeforeEach
  void setUp() {
    apiClient = mock(ApiClient.class);
    when(apiClient.getBasePath()).thenReturn("path");
  }

  @Test
  void testSuccess() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval()));
    doReturn(token).when(botAuthenticator).authenticateAndGetToken(any());

    assertEquals(token, botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(1)).authenticateAndGetToken(any());
  }

  @Test
  void testUnauthorized() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(401, "")).when(botAuthenticator).authenticateAndGetToken(any());

    assertThrows(AuthUnauthorizedException.class, () -> botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(1)).authenticateAndGetToken(any());
  }

  @Test
  void testUnexpectedApiException() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(404, "")).when(botAuthenticator).authenticateAndGetToken(any());

    assertThrows(ApiRuntimeException.class, () -> botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(1)).authenticateAndGetToken(any());
  }

  @Test
  void testShouldRetry() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(4)));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(""))
        .doReturn(token)
        .when(botAuthenticator).authenticateAndGetToken(any());

    assertEquals(token, botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(4)).authenticateAndGetToken(any());
  }

  @Test
  void testRetriesExhausted() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(429, "")).when(botAuthenticator).authenticateAndGetToken(any());

    assertThrows(ApiRuntimeException.class, () -> botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(2)).authenticateAndGetToken(any());
  }
}
