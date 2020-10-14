package com.symphony.bdk.app.spring.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.BdkAppError;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.exception.GlobalControllerExceptionHandler;
import com.symphony.bdk.app.spring.properties.AppAuthProperties;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
public class CircleOfTrustControllerTest {

  private MockMvc mockMvc;
  private static final ObjectMapper MAPPER = new JsonMapper();

  @MockBean SymphonyBdkAppProperties properties;
  @MockBean ExtensionAppAuthenticator authenticator;

  @Before
  public void setup() {
    MockitoAnnotations.openMocks(this);
    AppAuthProperties appAuth = new AppAuthProperties();
    appAuth.getJwtCookie().setEnabled(true);
    when(properties.getAuth()).thenReturn(appAuth);

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(new CircleOfTrustController(properties, authenticator))
        .setControllerAdvice(new GlobalControllerExceptionHandler())
        .build();
  }

  @Test
  public void authenticateSuccess() throws Exception {
    AppAuthSession appAuthSession = mock(AppAuthSession.class);
    when(appAuthSession.getAppToken()).thenReturn("test-token");
    when(appAuthSession.getSymphonyToken()).thenReturn("test-symphony-token");
    when(authenticator.authenticateExtensionApp("test-token")).thenReturn(appAuthSession);

    String response =  mockMvc.perform(
        post("/bdk/v1/app/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\"\n"
                + "}"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    AppToken appToken = MAPPER.readValue(response, AppToken.class);

    assertEquals(appToken.getAppToken(), "test-token");
  }

  @Test
  public void authenticateFailed() throws Exception {
    when(authenticator.authenticateExtensionApp("test-token")).thenThrow(AuthUnauthorizedException.class);

    String response =  mockMvc.perform(
        post("/bdk/v1/app/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\"\n"
                + "}"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse().getContentAsString();
    BdkAppError error = MAPPER.readValue(response, BdkAppError.class);

    assertEquals(error.getStatus(), HttpStatus.UNAUTHORIZED.value());
    assertEquals(error.getCode(), BdkAppErrorCode.UNAUTHORIZED);
  }

  @Test
  public void validateTokenSuccess() throws Exception {
    when(authenticator.validateTokens("test-token", "test-symphony-token")).thenReturn(true);

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
    when(authenticator.validateTokens("test-token", "test-wrong-token")).thenReturn(false);

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
  public void validateTokenMissingFields() throws Exception {
    when(authenticator.validateTokens("test-token", "test-wrong-token")).thenReturn(false);

    String response = mockMvc.perform(
        post("/bdk/v1/app/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\"\n"
                + "}"))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();
    BdkAppError error = MAPPER.readValue(response, BdkAppError.class);

    assertEquals(error.getStatus(), HttpStatus.BAD_REQUEST.value());
    assertEquals(error.getCode(), BdkAppErrorCode.MISSING_FIELDS);
  }

  @Test
  public void validateJwtSuccess() throws Exception {
    UserClaim userClaim = new UserClaim();
    userClaim.setId(1234L);
    when(authenticator.validateJwt("test-jwt")).thenReturn(userClaim);

    String response = mockMvc.perform(
        post("/bdk/v1/app/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"jwt\": \"test-jwt\"\n"
                + "}"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserId userId = MAPPER.readValue(response, UserId.class);

    assertEquals(userId.getUserId(), 1234L);
  }

  @Test
  public void validateJwtFailed() throws Exception {
    when(authenticator.validateJwt("test-jwt")).thenThrow(AuthInitializationException.class);

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
