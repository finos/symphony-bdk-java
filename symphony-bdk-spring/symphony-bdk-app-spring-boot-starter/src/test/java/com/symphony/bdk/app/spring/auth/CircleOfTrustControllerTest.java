package com.symphony.bdk.app.spring.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.service.CircleOfTrustService;
import com.symphony.bdk.app.spring.exception.BdkAppError;
import com.symphony.bdk.app.spring.exception.BdkAppErrorCode;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.app.spring.properties.AppAuthProperties;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CircleOfTrustController.class)
public class CircleOfTrustControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SymphonyBdkCoreProperties coreProperties;

  @MockBean
  private SymphonyBdkAppProperties appProperties;

  @MockBean
  private CircleOfTrustService service;

  @BeforeEach
  public void setup() {
    final AppAuthProperties appAuth = new AppAuthProperties();
    appAuth.getJwtCookie().setEnabled(true);
    when(appProperties.getAuth()).thenReturn(appAuth);

    final BdkExtAppConfig appConfig = new BdkExtAppConfig();
    appConfig.setAppId("appId");
    when(coreProperties.getApp()).thenReturn(appConfig);
  }

  @Test
  public void authenticateSuccess() throws Exception {
    when(service.authenticate()).thenReturn(new AppToken("test-token"));

    MockHttpServletResponse response = mockMvc.perform(
        post("/bdk/v1/app/auth"))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    AppToken appToken = objectMapper.readValue(response.getContentAsString(), AppToken.class);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(appToken.getAppToken(), "test-token");
  }

  @Test
  public void authenticateFailed() throws Exception {
    when(service.authenticate()).thenThrow(new BdkAppException(BdkAppErrorCode.AUTH_FAILURE));

    MockHttpServletResponse response =  mockMvc.perform(
        post("/bdk/v1/app/auth"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse();

    BdkAppError error = objectMapper.readValue(response.getContentAsString(), BdkAppError.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertEquals(BdkAppErrorCode.AUTH_FAILURE, error.getCode());
  }

  @Test
  public void validateTokenSuccess() throws Exception {
    mockMvc.perform(
        post("/bdk/v1/app/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{"
                + "    \"appToken\": \"test-token\","
                + "    \"symphonyToken\": \"test-symphony-token\""
                + "}"))
        .andExpect(status().isNoContent());
  }

  @Test
  public void validateTokenFailed() throws Exception {
    doThrow(new BdkAppException(BdkAppErrorCode.INVALID_TOKEN)).when(service).validateTokens(any());

    MockHttpServletResponse response = mockMvc.perform(
        post("/bdk/v1/app/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\n"
                + "    \"appToken\": \"test-token\",\n"
                + "    \"symphonyToken\": \"test-wrong-token\"\n"
                + "}"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse();

    BdkAppError error = objectMapper.readValue(response.getContentAsString(), BdkAppError.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertEquals(BdkAppErrorCode.INVALID_TOKEN, error.getCode());
  }

  @Test
  public void validateJwtSuccess() throws Exception {
    UserId userId = new UserId(1234L);
    when(service.validateJwt("test-jwt")).thenReturn(userId);

    MockHttpServletResponse response = mockMvc.perform(
        post("/bdk/v1/app/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"jwt\": \"test-jwt\" }"))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    assertEquals("None", ((MockCookie) response.getCookies()[0]).getSameSite());

    UserId id = objectMapper.readValue(response.getContentAsString(), UserId.class);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(id.getUserId(), 1234L);

  }

  @Test
  public void validateJwtFailed() throws Exception {
    when(service.validateJwt("test-jwt")).thenThrow(new BdkAppException(BdkAppErrorCode.INVALID_JWT));

    MockHttpServletResponse response = mockMvc.perform(
        post("/bdk/v1/app/jwt")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"jwt\": \"test-jwt\" }"))
        .andExpect(status().isUnauthorized())
        .andReturn().getResponse();
    BdkAppError error = objectMapper.readValue(response.getContentAsString(), BdkAppError.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertEquals(BdkAppErrorCode.INVALID_JWT, error.getCode());
  }
}
