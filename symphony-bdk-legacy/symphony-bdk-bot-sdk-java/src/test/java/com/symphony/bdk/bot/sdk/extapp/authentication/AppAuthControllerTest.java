package com.symphony.bdk.bot.sdk.extapp.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.bot.sdk.symphony.ConfigClient;
import com.symphony.bdk.bot.sdk.symphony.ExtensionAppAuthClient;
import com.symphony.bdk.bot.sdk.symphony.exception.AppAuthenticateException;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.AuthenticateResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppAuthControllerTest {

  private ExtensionAppAuthClient extAppAuthClient;
  private ConfigClient configClient;
  private AppAuthController appAuthController;
  private AppToken appToken;
  private JwtInfo jwtInfo;
  private HttpServletRequest httpServletRequest;
  private HttpServletResponse httpServletResponse;

  @Before
  public void init(){
      this.configClient = mock(ConfigClient.class);
      assertNotNull(this.configClient);
      Mockito.when(this.configClient.getExtAppAuthPath()).thenReturn("extAppAuthPath");

      this.extAppAuthClient = mock(ExtensionAppAuthClient.class);
      assertNotNull(this.extAppAuthClient);

      this.appToken = mock(AppToken.class);
      Mockito.when(this.appToken.getAppToken()).thenReturn("appToken");
      Mockito.when(this.appToken.getSymphonyToken()).thenReturn("SymphonyToken");

      this.jwtInfo = mock(JwtInfo.class);
      Mockito.when(this.jwtInfo.getJwt()).thenReturn("JWT");

      this.appAuthController = new AppAuthController(this.extAppAuthClient, this.configClient, true);
      assertNotNull(this.appAuthController);

      this.httpServletRequest = mock(HttpServletRequest.class);
      Mockito.when(this.httpServletRequest.getContextPath()).thenReturn("JWT");

      this.httpServletResponse = mock(HttpServletResponse.class);
      Mockito.doNothing().when(this.httpServletResponse).addCookie(any(Cookie.class));
  }

  @Test
  public void testAuthenticateWithAppAuthenticateException() throws SymphonyClientException {
    final AppInfo appInfo = new AppInfo();
    ResponseEntity responseEntity;

    // Initialize configClient.extAppId
    Mockito.when(this.configClient.getExtAppId()).thenReturn("56789");

    // AppId = null
    appInfo.setAppId(null);
    responseEntity = this.appAuthController.authenticate(appInfo);
    assertEquals(ResponseEntity.badRequest().build(), responseEntity);

    // AppId != configClient.extAppId
    appInfo.setAppId(this.configClient.getExtAppId() + "Test1");
    responseEntity = this.appAuthController.authenticate(appInfo);
    assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), responseEntity);

    // AppId = configClient.extAppId
    appInfo.setAppId(this.configClient.getExtAppId());
    responseEntity = this.appAuthController.authenticate(appInfo);
    AuthenticateResponse authenticateResponse = null;
    try {
      assertNotNull(appInfo.getAppId());
      authenticateResponse = this.extAppAuthClient.appAuthenticate(appInfo.getAppId());
      assertEquals(ResponseEntity.ok(authenticateResponse), responseEntity);
    } catch (final AppAuthenticateException aae) {
      assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), responseEntity);
    }
  }

  @Test
  public void testAuthenticateWithAppSymphonyException() {
    final AppInfo appInfo = new AppInfo();
    ResponseEntity responseEntity;

    // Initialize configClient.extAppId
    Mockito.when(this.configClient.getExtAppId()).thenReturn("56789");

    // AppId = null
    appInfo.setAppId(null);
    responseEntity = this.appAuthController.authenticate(appInfo);
    assertEquals(ResponseEntity.badRequest().build(), responseEntity);

    // AppId != configClient.extAppId
    appInfo.setAppId(this.configClient.getExtAppId() + "Test1");
    responseEntity = this.appAuthController.authenticate(appInfo);
    assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), responseEntity);

    // AppId = configClient.extAppId
    appInfo.setAppId(this.configClient.getExtAppId());
    responseEntity = this.appAuthController.authenticate(appInfo);
    AuthenticateResponse authenticateResponse = null;
    try {
      assertNotNull(appInfo.getAppId());
      authenticateResponse = this.extAppAuthClient.appAuthenticate(appInfo.getAppId());
      assertEquals(ResponseEntity.ok(authenticateResponse), responseEntity);
    } catch (final SymphonyClientException sce) {
      assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(), responseEntity);
    }
  }

  @Test
  public void testValidateTokenSuccess(){
    Mockito.when(this.extAppAuthClient.validateTokens("appToken", "SymphonyToken")).thenReturn(true);
    final ResponseEntity response = this.appAuthController.validateTokens(this.appToken);
    assertNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testValidateTokenFailure(){
    Mockito.when(this.extAppAuthClient.validateTokens("appToken", "symphonyToken")).thenReturn(false);
    final ResponseEntity response = this.appAuthController.validateTokens(this.appToken);
    assertNull(response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void testValidateJWTSuccess() {
    Mockito.when(this.extAppAuthClient.verifyJWT("JWT")).thenReturn(12345L);
    final ResponseEntity response =
        this.appAuthController.validateJwt(this.jwtInfo, this.httpServletRequest, this.httpServletResponse);
    assertEquals(12345L, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testValidateJWTFailure(){
    Mockito.when(this.extAppAuthClient.verifyJWT("JWT")).thenThrow(AppAuthenticateException.class);
    final ResponseEntity response =
        this.appAuthController.validateJwt(this.jwtInfo, this.httpServletRequest, this.httpServletResponse);
    assertNull(response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
}
