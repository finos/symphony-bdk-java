package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.gen.api.model.JwtToken;
import com.symphony.bdk.gen.api.model.Token;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.ApiRuntimeException;
import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.net.SocketTimeoutException;
import java.util.Collections;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractBotAuthenticatorTest {

  private ApiClient apiClient;

  private static class TestBotAuthenticator extends AbstractBotAuthenticator {
    public TestBotAuthenticator(BdkRetryConfig retryConfig, ApiClient apiClient) {
      super(retryConfig, new BdkCommonJwtConfig(), apiClient);
    }

    @Override
    protected Token retrieveSessionToken() {
      return null;
    }

    @Override
    protected String retrieveKeyManagerToken() {
      return null;
    }

    @Override
    protected Token doRetrieveToken(ApiClient client) throws ApiException {
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
  void testSuccess() throws AuthUnauthorizedException, ApiException {
    final String token = "12324";

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(), apiClient));
    doReturn(new Token().token(token)).when(botAuthenticator).doRetrieveToken(any());

    assertEquals(token, botAuthenticator.retrieveKeyManagerToken(apiClient));
    verify(botAuthenticator, times(1)).doRetrieveToken(any());
  }

  @Test
  void testSuccessAuthToken() throws ApiException, AuthUnauthorizedException {
    final Token token = new Token();
    token.setToken("12324");
    token.setAuthorizationToken("Bearer qwerty");

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(), apiClient));
    doReturn(token).when(botAuthenticator).doRetrieveToken(any());

    assertEquals(token, botAuthenticator.retrieveSessionToken(apiClient));
    verify(botAuthenticator, times(1)).doRetrieveToken(any());
  }

  @Test
  void testSuccessBearerToken() throws AuthUnauthorizedException, ApiException {
    JwtToken token = new JwtToken();
    token.setAccessToken("qwertyui");
    AbstractBotAuthenticator botAuthenticator = new TestBotAuthenticator(ofMinimalInterval(), apiClient);
    when(apiClient.invokeAPI(any(), any(), any(), any(),any(), any(), any(),any(),any(),any(),any()))
        .thenReturn(new ApiResponse<>(200, Collections.emptyMap(), token));

    assertEquals("qwertyui", botAuthenticator.retrieveAuthorizationToken("sessionToken"));
  }

  @Test
  void testUnauthorized() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(), apiClient));
    doThrow(new ApiException(401, "")).when(botAuthenticator).doRetrieveToken(any());

    assertThrows(AuthUnauthorizedException.class, () -> botAuthenticator.retrieveKeyManagerToken(apiClient));
    verify(botAuthenticator, times(1)).doRetrieveToken(any());
  }

  @Test
  void testUnexpectedApiException() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(), apiClient));
    doThrow(new ApiException(404, "")).when(botAuthenticator).doRetrieveToken(any());

    assertThrows(ApiRuntimeException.class, () -> botAuthenticator.retrieveKeyManagerToken(apiClient));
    verify(botAuthenticator, times(1)).doRetrieveToken(any());
  }

  @Test
  void testShouldRetry() throws AuthUnauthorizedException, ApiException {
    final String token = "12324";

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(4),
        apiClient));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new SocketTimeoutException()))
        .doReturn(new Token().token(token))
        .when(botAuthenticator).doRetrieveToken(any());

    assertEquals(token, botAuthenticator.retrieveKeyManagerToken(apiClient));
    verify(botAuthenticator, times(4)).doRetrieveToken(any());
  }

  @Test
  void testShouldRetryAuthToken() throws ApiException, AuthUnauthorizedException {
    final Token token = new Token();
    token.setToken("12324");
    token.setAuthorizationToken("Bearer qwerty");

    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(4),
        apiClient));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(new SocketTimeoutException()))
        .doReturn(token)
        .when(botAuthenticator).doRetrieveToken(any());

    assertEquals(token, botAuthenticator.retrieveSessionToken(apiClient));
    verify(botAuthenticator, times(4)).doRetrieveToken(any());
  }

  @Test
  void testRetriesExhausted() throws ApiException {
    AbstractBotAuthenticator botAuthenticator = spy(new TestBotAuthenticator(ofMinimalInterval(2),
        apiClient));
    doThrow(new ApiException(429, "")).when(botAuthenticator).doRetrieveToken(any());

    assertThrows(ApiRuntimeException.class, () -> botAuthenticator.retrieveKeyManagerToken(apiClient));
    verify(botAuthenticator, times(2)).doRetrieveToken(any());
  }

  @Test
  void isCommonJwtEnabled() {
    TestBotAuthenticator authenticator = new TestBotAuthenticator(ofMinimalInterval(), apiClient);

    assertFalse(authenticator.isCommonJwtEnabled());
  }
}
