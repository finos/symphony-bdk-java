package com.symphony.bdk.app.spring.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AppTokenServiceToken {

  private AppTokenService appTokenService;

  @Mock
  private ExtensionAppAuthenticator authenticator;

  @BeforeEach
  public void setup() {
    this.appTokenService = new AppTokenService(authenticator );
  }

  @Test
  void generateTokenTest() {

    String token = appTokenService.generateToken();

    assertNotNull(token);
  }

  @Test
  void validateTokenSuccess() {
     when(authenticator.validateTokens("test-app-token", "test-symphony-token")).thenReturn(true);
    TokenPair tokenPair = new TokenPair();
    tokenPair.setAppToken("test-app-token");
    tokenPair.setSymphonyToken("test-symphony-token");

    Boolean validated = appTokenService.validateTokens(tokenPair);

    assertTrue(validated);
  }

  @Test
  void validateTokenFailed() {
    when(authenticator.validateTokens("test-app-token", "test-symphony-token")).thenReturn(false);
    TokenPair tokenPair = new TokenPair();
    tokenPair.setAppToken("test-app-token");
    tokenPair.setSymphonyToken("test-symphony-token");

    Boolean validated = appTokenService.validateTokens(tokenPair);

    assertFalse(validated);
  }
}
