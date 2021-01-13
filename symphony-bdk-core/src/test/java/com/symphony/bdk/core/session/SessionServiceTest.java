package com.symphony.bdk.core.session;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.session.OboSessionService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

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
    this.service = new SessionService(this.sessionApi, this.authSession,
        new RetryWithRecoveryBuilder().retryConfig(ofMinimalInterval(1)));
  }

  @Test
  void nonOboEndpointShouldThrowExceptionInOboMode() {
    this.service = new SessionService(this.sessionApi, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, () -> this.service.getSession());
  }

  @Test
  void testGetSessionOboMode() throws ApiException {
    final String sessionToken = UUID.randomUUID().toString();
    when(this.authSession.getSessionToken()).thenReturn(sessionToken);
    when(this.sessionApi.v2SessioninfoGet(eq(sessionToken))).thenReturn(new UserV2());

    this.service = new SessionService(this.sessionApi, new RetryWithRecoveryBuilder<>());
    final UserV2 session = this.service.obo(this.authSession).getSession();

    assertNotNull(session);
  }

  @Test
  void testGetSessionSuccess() throws Exception {
    final String sessionToken = UUID.randomUUID().toString();
    when(this.authSession.getSessionToken()).thenReturn(sessionToken);
    when(this.sessionApi.v2SessioninfoGet(eq(sessionToken))).thenReturn(new UserV2());
    final UserV2 session = this.service.getSession();
    assertNotNull(session);
  }

  @Test
  void testGetSessionFailure() throws Exception {
    final String sessionToken = UUID.randomUUID().toString();
    when(this.authSession.getSessionToken()).thenReturn(sessionToken);
    when(this.sessionApi.v2SessioninfoGet(eq(sessionToken))).thenThrow(new ApiException(401, "Auth error"));
    assertThrows(ApiRuntimeException.class, () -> this.service.getSession());
  }

  @Test
  void oboSessionServiceTest() {
    final OboSessionService oboSessionService = this.service.obo(this.authSession);
    assertNotNull(oboSessionService);
  }
}
