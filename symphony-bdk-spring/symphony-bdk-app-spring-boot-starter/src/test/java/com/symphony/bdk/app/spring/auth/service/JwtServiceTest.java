package com.symphony.bdk.app.spring.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.model.exception.AppAuthException;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  private JwtService jwtService;

  @Mock
  private ExtensionAppAuthenticator authenticator;

  @BeforeEach
  public void setup() {
    this.jwtService = new JwtService(authenticator);
  }

  @Test
  void validateJwtSuccess() throws AuthInitializationException {
    UserClaim userClaim = new UserClaim();
    userClaim.setId(1234L);
    when(authenticator.validateJwt("test-token")).thenReturn(userClaim);

    UserId userId = jwtService.validateJwt("test-token");

    assertEquals(userId.getUserId(), 1234L);
  }

  @Test
  void validateJwtFailed() throws AuthInitializationException {
    when(authenticator.validateJwt("test-token")).thenThrow(AuthInitializationException.class);

    assertThrows(AppAuthException.class, () -> jwtService.validateJwt("test-token"));
  }
}
