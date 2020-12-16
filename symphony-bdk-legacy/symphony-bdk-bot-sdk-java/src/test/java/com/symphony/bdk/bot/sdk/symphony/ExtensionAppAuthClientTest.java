package com.symphony.bdk.bot.sdk.symphony;

import com.symphony.bdk.bot.sdk.symphony.authentication.ExtensionAppAuthenticator;
import com.symphony.bdk.bot.sdk.symphony.exception.AppAuthenticateException;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.AuthenticateResponse;
import lombok.SneakyThrows;
import model.AppAuthResponse;
import model.UserInfo;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExtensionAppAuthClientTest {
  private ExtensionAppAuthenticator extensionAppAuthenticator;
  private ExtensionAppAuthClient extensionAppAuthClient;

  @BeforeEach
  public void init() {
    extensionAppAuthenticator = mock(ExtensionAppAuthenticator.class);
    extensionAppAuthClient = new ExtensionAppAuthClientImpl(extensionAppAuthenticator);
  }

  @SneakyThrows
  @Test
  public void appAuthenticateSuccess() {
    AppAuthResponse appAuthResponse = new AppAuthResponse();
    appAuthResponse.setAppToken("testToken");
    when(extensionAppAuthenticator.appAuthenticate()).thenReturn(appAuthResponse);
    AuthenticateResponse authResponse = extensionAppAuthClient.appAuthenticate("testAppId");
    assertEquals("testAppId", authResponse.getAppId());
    assertEquals("testToken", authResponse.getAppToken());
  }

  @SneakyThrows
  @Test
  public void appAuthenticateFailed() {
    when(extensionAppAuthenticator.appAuthenticate()).thenThrow(new RuntimeException());
    assertThrows(SymphonyClientException.class, () -> extensionAppAuthClient.appAuthenticate("testAppId"));
  }

  @SneakyThrows
  @Test
  public void appAuthenticateAppTokenNull() {
    when(extensionAppAuthenticator.appAuthenticate()).thenReturn(null);
    assertThrows(AppAuthenticateException.class, () -> extensionAppAuthClient.appAuthenticate("testAppId"));
  }

  @Test
  public void validateTokensTest() {
    when(extensionAppAuthenticator.validateTokens("appToken", "symphonyToken")).thenReturn(true);
    assertTrue(extensionAppAuthClient.validateTokens("appToken", "symphonyToken"));
  }

  @Test
  public void validateTokensFalseTest() {
    when(extensionAppAuthenticator.validateTokens("appToken", "symphonyToken")).thenReturn(false);
    assertFalse(extensionAppAuthClient.validateTokens("appToken", "symphonyToken"));
  }

  @Test
  public void verifyJWTTest() {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(123456L);
    when(extensionAppAuthenticator.verifyJWT("testJwt")).thenReturn(userInfo);
    assertEquals(123456L, extensionAppAuthClient.verifyJWT("testJwt"));
  }

  @Test
  public void verifyJWTFailedTest() {
    when(extensionAppAuthenticator.verifyJWT("testJwt")).thenReturn(null);
    assertThrows(AppAuthenticateException.class, () -> extensionAppAuthClient.verifyJWT("testJwt"));
  }
}
