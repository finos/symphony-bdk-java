package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.Test;

import javax.ws.rs.ProcessingException;

public class AbstractOboAuthenticatorTest {

  private static class TestAbstractOboAuthenticator extends AbstractOboAuthenticator {

    public TestAbstractOboAuthenticator(BdkRetryConfig retryConfig) {
      super(retryConfig, "");
    }

    @Override
    protected String authenticateAndRetrieveOboSessionToken(String appSessionToken, Long userId) throws ApiException {
      return null;
    }

    @Override
    protected String authenticateAndRetrieveOboSessionToken(String appSessionToken, String username)
        throws ApiException {
      return null;
    }

    @Override
    protected String authenticateAndRetrieveAppSessionToken() throws ApiException {
      return null;
    }

    @Override
    public AuthSession authenticateByUsername(String username) throws AuthUnauthorizedException {
      return null;
    }

    @Override
    public AuthSession authenticateByUserId(Long userId) throws AuthUnauthorizedException {
      return null;
    }
  }

  // test retrieveOboSessionTokenByUserId()
  @Test
  void testRetrieveTokenByUserIdSuccess() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doReturn(token).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());

    assertEquals(token, authenticator.retrieveOboSessionTokenByUserId(0L));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());
  }

  @Test
  void testRetrieveTokenByUserIdUnauthorized() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(401, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());

    assertThrows(AuthUnauthorizedException.class, () -> authenticator.retrieveOboSessionTokenByUserId(0L));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());
  }

  @Test
  void testRetrieveTokenByUserIdUnexpectedApiException() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(404, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveOboSessionTokenByUserId(0L));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());
  }

  @Test
  void testRetrieveTokenByUserIdShouldRetry() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(""))
        .doReturn(token).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());

    assertEquals(token, authenticator.retrieveOboSessionTokenByUserId(0L));
    verify(authenticator, times(4)).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());
  }

  @Test
  void testRetrieveTokenByUserIdRetriesExhausted() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval(2)));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(429, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveOboSessionTokenByUserId(0L));
    verify(authenticator, times(2)).authenticateAndRetrieveOboSessionToken(anyString(), anyLong());
  }

  // test retrieveOboSessionTokenByUsername()
  @Test
  void testRetrieveTokenByUsernameSuccess() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doReturn(token).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyString());

    assertEquals(token, authenticator.retrieveOboSessionTokenByUsername(""));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyString());
  }

  @Test
  void testRetrieveTokenByUsernameUnauthorized() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(401, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyString());

    assertThrows(AuthUnauthorizedException.class, () -> authenticator.retrieveOboSessionTokenByUsername(""));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyString());
  }

  @Test
  void testRetrieveTokenByUsernameUnexpectedApiException() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(404, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyString());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveOboSessionTokenByUsername(""));
    verify(authenticator, times(1)).authenticateAndRetrieveOboSessionToken(anyString(), anyString());
  }

  @Test
  void testRetrieveTokenByUsernameShouldRetry() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(""))
        .doReturn(token).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyString());

    assertEquals(token, authenticator.retrieveOboSessionTokenByUsername(""));
    verify(authenticator, times(4)).authenticateAndRetrieveOboSessionToken(anyString(), anyString());
  }

  @Test
  void testRetrieveTokenByUsernameRetriesExhausted() throws ApiException, AuthUnauthorizedException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval(2)));
    doReturn("").when(authenticator).retrieveAppSessionToken();
    doThrow(new ApiException(429, "")).when(authenticator).authenticateAndRetrieveOboSessionToken(anyString(), anyString());

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveOboSessionTokenByUsername(""));
    verify(authenticator, times(2)).authenticateAndRetrieveOboSessionToken(anyString(), anyString());
  }

  // test retrieveAppSessionToken()
  @Test
  void testRetrieveAppSessionTokenSuccess() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doReturn(token).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertEquals(token, authenticator.retrieveAppSessionToken());
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenUnauthorized() throws ApiException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(401, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(AuthUnauthorizedException.class, () -> authenticator.retrieveAppSessionToken());
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveAppSessionTokenUnexpectedApiException() throws ApiException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(404, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveAppSessionToken());
    verify(authenticator, times(1)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveApSessionTokenShouldRetry() throws ApiException, AuthUnauthorizedException {
    final String token = "12324";

    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval()));
    doThrow(new ApiException(429, ""))
        .doThrow(new ApiException(503, ""))
        .doThrow(new ProcessingException(""))
        .doReturn(token).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertEquals(token, authenticator.retrieveAppSessionToken());
    verify(authenticator, times(4)).authenticateAndRetrieveAppSessionToken();
  }

  @Test
  void testRetrieveApSessionTokenRetriesExhausted() throws ApiException {
    AbstractOboAuthenticator authenticator = spy(new TestAbstractOboAuthenticator(ofMinimalInterval(2)));
    doThrow(new ApiException(429, "")).when(authenticator).authenticateAndRetrieveAppSessionToken();

    assertThrows(ApiRuntimeException.class, () -> authenticator.retrieveAppSessionToken());
    verify(authenticator, times(2)).authenticateAndRetrieveAppSessionToken();
  }
}
