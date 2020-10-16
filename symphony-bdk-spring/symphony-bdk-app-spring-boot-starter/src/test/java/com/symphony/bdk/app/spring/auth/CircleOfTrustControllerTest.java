package com.symphony.bdk.app.spring.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.BdkAppError;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.service.CircleOfTrustService;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.app.spring.properties.AppAuthProperties;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CircleOfTrustController.class)
public class CircleOfTrustControllerTest {
  private static final ObjectMapper MAPPER = new JsonMapper();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SymphonyBdkCoreProperties coreProperties;

  @MockBean
  private SymphonyBdkAppProperties properties;

  @MockBean
  private CircleOfTrustService service;

  @BeforeEach
  public void setup() {
    AppAuthProperties appAuth = new AppAuthProperties();
    appAuth.getJwtCookie().setEnabled(true);
    when(properties.getAuth()).thenReturn(appAuth);
    BdkExtAppConfig appConfig = new BdkExtAppConfig();
    appConfig.setAppId("appId");
    when(coreProperties.getApp()).thenReturn(appConfig);
  }

  @Test
  public void authenticateSuccess() throws Exception {
    when(service.authenticate()).thenReturn(new AppToken("test-token"));

    String response =  mockMvc.perform(
        post("/bdk/v1/app/auth"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    AppToken appToken = MAPPER.readValue(response, AppToken.class);

    assertEquals(appToken.getAppToken(), "test-token");
  }

  @Test
  public void authenticateFailed() throws Exception {
    when(service.authenticate()).thenThrow(new BdkAppException(BdkAppErrorCode.UNAUTHORIZED));

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
    doThrow(new BdkAppException(BdkAppErrorCode.INVALID_TOKEN)).when(service).validateTokens(any());

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
    UserId userId = new UserId(1234L);
    when(service.validateJwt("test-jwt")).thenReturn(userId);

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
    when(service.validateJwt("test-jwt")).thenThrow(new BdkAppException(BdkAppErrorCode.INVALID_JWT));

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
