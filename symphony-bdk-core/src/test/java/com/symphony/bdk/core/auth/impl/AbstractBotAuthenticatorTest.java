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
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketTimeoutException;

import javax.annotation.Nonnull;
import javax.ws.rs.ProcessingException;

class AbstractBotAuthenticatorTest {

  private ApiClient apiClient;

  private static class TestBotAuthenticator extends AbstractBotAuthenticator {
    public TestBotAuthenticator(BdkRetryConfig retryConfig) {
      super(retryConfig, new BdkCommonJwtConfig(), null);
    }

    @Nonnull
    @Override
    protected Token retrieveAuthToken() throws AuthUnauthorizedException {
      return null;
    }

    @Nonnull
    @Override
    protected String retrieveKeyManagerToken() throws AuthUnauthorizedException {
      return null;
    }

    @Override
    protected String authenticateAndGetToken(ApiClient client) throws ApiException {
      return null;
    }

    @Override
    protected Token authenticateAndGetAuthToken(ApiClient client) throws ApiException {
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
  void testSuccessAuthToken() throws ApiException, AuthUnauthorizedException {
    final Token token = new Token();
    token.setToken("12324");
    token.setAuthorizationToken("Bearer qwerty");

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval()));
    doReturn(token).when(botAuthenticator).authenticateAndGetAuthToken(any());

    assertEquals(token, botAuthenticator.retrieveAuthToken(apiClient));
    verify(botAuthenticator, times(1)).authenticateAndGetAuthToken(any());
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
        .doThrow(new ProcessingException(new SocketTimeoutException()))
        .doReturn(token)
        .when(botAuthenticator).authenticateAndGetToken(any());

    assertEquals(token, botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(4)).authenticateAndGetToken(any());
  }

  @Test
  void testShouldRetryAuthToken() throws ApiException, AuthUnauthorizedException {
    final Token token = new Token();
    token.setToken("12324");
    token.setAuthorizationToken("Bearer qwerty");

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(4)));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new SocketTimeoutException()))
        .doReturn(token)
        .when(botAuthenticator).authenticateAndGetAuthToken(any());

    assertEquals(token, botAuthenticator.retrieveAuthToken(apiClient));
    verify(botAuthenticator, times(4)).authenticateAndGetAuthToken(any());
  }


  @Test
  void testRetriesExhausted() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(429, "")).when(botAuthenticator).authenticateAndGetToken(any());

    assertThrows(ApiRuntimeException.class, () -> botAuthenticator.retrieveToken(apiClient));
    verify(botAuthenticator, times(2)).authenticateAndGetToken(any());
  }
}
