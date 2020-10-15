package com.symphony.bdk.core.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

/**
 * Test class for the {@link SessionService}.
 */
@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @Mock
  private SessionApi sessionApi;

  @Mock
  private AuthSession authSession;

  private SessionService service;

  @BeforeEach
  void setUp() {
    final BdkRetryConfig retryConfig = BdkRetryConfig.ofMinimalInterval(1);
    this.service = new SessionService(this.sessionApi, new RetryWithRecoveryBuilder().retryConfig(retryConfig));
  }

  @Test
  void testGetSessionSuccess() throws Exception {
    final String sessionToken = UUID.randomUUID().toString();
    when(this.authSession.getSessionToken()).thenReturn(sessionToken);
    when(this.sessionApi.v2SessioninfoGet(eq(sessionToken))).thenReturn(new UserV2());
    final UserV2 session = this.service.getSession(this.authSession);
    assertNotNull(session);
  }

  @Test
  void testGetSessionFailure() throws Exception {
    final String sessionToken = UUID.randomUUID().toString();
    when(this.authSession.getSessionToken()).thenReturn(sessionToken);
    when(this.sessionApi.v2SessioninfoGet(eq(sessionToken))).thenThrow(new ApiException(401, "Auth error"));
    assertThrows(ApiRuntimeException.class, () -> this.service.getSession(this.authSession));
  }
}
