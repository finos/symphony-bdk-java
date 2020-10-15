package com.symphony.bdk.app.spring.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.BdkAppError;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.model.exception.AppAuthException;
import com.symphony.bdk.app.spring.auth.service.AppTokenService;
import com.symphony.bdk.app.spring.auth.service.JwtService;
import com.symphony.bdk.app.spring.exception.GlobalControllerExceptionHandler;
import com.symphony.bdk.app.spring.properties.AppAuthProperties;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class CircleOfTrustControllerTest {

  private MockMvc mockMvc;
  private static final ObjectMapper MAPPER = new JsonMapper();

  @Mock
  private SymphonyBdkAppProperties properties;

  @Mock
  private ExtensionAppAuthenticator authenticator;

  @Mock
  private JwtService jwtService;

  @Mock
  private AppTokenService appTokenService;

  @Before
  public void setup() {
    MockitoAnnotations.openMocks(this);
    AppAuthProperties appAuth = new AppAuthProperties();
    appAuth.getJwtCookie().setEnabled(true);
    when(properties.getAuth()).thenReturn(appAuth);
    when(appTokenService.generateToken()).thenReturn("test-token");
    SymphonyBdkCoreProperties coreProperties = new SymphonyBdkCoreProperties();
    coreProperties.getApp().setAppId("appId");

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(new CircleOfTrustController(properties, authenticator, jwtService, appTokenService))
        .setControllerAdvice(new GlobalControllerExceptionHandler(coreProperties))
        .build();
  }

  @Test
  public void authenticateSuccess() throws Exception {
    AppAuthSession appAuthSession = mock(AppAuthSession.class);
    when(appAuthSession.getAppToken()).thenReturn("test-token");
    when(appAuthSession.getSymphonyToken()).thenReturn("test-symphony-token");
    when(authenticator.authenticateExtensionApp("test-token")).thenReturn(appAuthSession);

    String response =  mockMvc.perform(
        post("/bdk/v1/app/auth"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    AppToken appToken = MAPPER.readValue(response, AppToken.class);

    assertEquals(appToken.getAppToken(), "test-token");
  }

  @Test
  public void authenticateFailed() throws Exception {
    when(authenticator.authenticateExtensionApp(anyString())).thenThrow(AuthUnauthorizedException.class);

    String response =  mockMvc.perform(
        post("/bdk/v1/app/auth"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse().getContentAsString();
    BdkAppError error = MAPPER.readValue(response, BdkAppError.class);

    assertEquals(error.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(error.getCode(), BdkAppErrorCode.UNAUTHORIZED);
  }

  @Test
  public void validateTokenSuccess() throws Exception {
    when(appTokenService.validateTokens(any())).thenReturn(true);

    mockMvc.perform(
        post("/bdk/v1/app/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\",\n"
                + "    \"symphonyToken\": \"test-symphony-token\"\n"
                + "}"))
        .andExpect(status().isNoContent());
  }

  @Test
  public void validateTokenFailed() throws Exception {
    when(appTokenService.validateTokens(any())).thenReturn(false);

    String response = mockMvc.perform(
        post("/bdk/v1/app/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\",\n"
                + "    \"symphonyToken\": \"test-wrong-token\"\n"
                + "}"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse().getContentAsString();
    BdkAppError error = MAPPER.readValue(response, BdkAppError.class);

    assertEquals(error.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(error.getCode(), BdkAppErrorCode.INVALID_TOKEN);
  }

  @Test
  public void validateJwtSuccess() throws Exception {
    UserId userId = new UserId();
    userId.setUserId(1234L);
    when(jwtService.validateJwt("test-jwt")).thenReturn(userId);

    String response = mockMvc.perform(
        post("/bdk/v1/app/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"jwt\": \"test-jwt\"\n"
                + "}"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserId id = MAPPER.readValue(response, UserId.class);

    assertEquals(id.getUserId(), 1234L);
  }

  @Test
  public void validateJwtFailed() throws Exception {
    when(jwtService.validateJwt("test-jwt")).thenThrow(new AppAuthException(BdkAppErrorCode.INVALID_JWT));

    String response = mockMvc.perform(
        post("/bdk/v1/app/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"jwt\": \"test-jwt\"\n"
                + "}"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse().getContentAsString();
    BdkAppError error = MAPPER.readValue(response, BdkAppError.class);

    assertEquals(error.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(error.getCode(), BdkAppErrorCode.INVALID_JWT);
  }
}
