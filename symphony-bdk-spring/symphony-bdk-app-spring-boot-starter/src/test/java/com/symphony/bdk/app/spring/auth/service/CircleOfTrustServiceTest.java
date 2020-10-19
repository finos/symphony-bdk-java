package com.symphony.bdk.app.spring.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CircleOfTrustService.class)
public class CircleOfTrustServiceTest {

  @MockBean
  private ExtensionAppAuthenticator authenticator;

  @Autowired
  private CircleOfTrustService service;

  @Test
  void authenticateTest() throws AuthUnauthorizedException {
    AppAuthSession appAuthSession = mock(AppAuthSession.class);
    when(appAuthSession.getAppToken()).thenReturn("test-token");
    when(appAuthSession.getSymphonyToken()).thenReturn("test-symphony-token");
    when(authenticator.authenticateExtensionApp(anyString())).thenReturn(appAuthSession);

    AppToken appToken = service.authenticate();

    assertEquals(appToken.getAppToken(), "test-token");
  }

  @Test
  void authenticateFailed() throws AuthUnauthorizedException {
    when(authenticator.authenticateExtensionApp(anyString())).thenThrow(AuthUnauthorizedException.class);

    assertThrows(BdkAppException.class, () -> service.authenticate());
  }

  @Test
  void validateTokens() {
    when(authenticator.validateTokens("test-token", "symphony-token")).thenReturn(false);

    assertThrows(BdkAppException.class, () -> service.validateTokens(new TokenPair("test-token", "symphony-token")));
  }

  @Test
  void validateJwt() throws AuthInitializationException {
    UserClaim userClaim = new UserClaim();
    userClaim.setId(1234L);
    when(authenticator.validateJwt("test-jwt")).thenReturn(userClaim);

    UserId userId = service.validateJwt("test-jwt");

    assertEquals(userId.getUserId(), 1234L);
  }

  @Test
  void validateJwtFailed() throws AuthInitializationException {
    when(authenticator.validateJwt("test-jwt")).thenThrow(AuthInitializationException.class);

    assertThrows(BdkAppException.class, () -> service.validateJwt("test-jwt"));
  }
}
