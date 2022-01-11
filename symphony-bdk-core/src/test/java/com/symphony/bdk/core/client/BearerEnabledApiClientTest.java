package com.symphony.bdk.core.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.util.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BearerEnabledApiClientTest {

  private ApiClient apiClient;
  private AuthSession authSession;
  public static final String JWT = "Bearer eyJraWQiOiJGNG5Xak9WbTRBZU9JYUtEL2JCUWNleXI5MW89IiwiYWxnIjoiUlMyNTYifQ."
      + "eyJleHAiOjE2NDEzMDgyNzgsInN1YiI6IjEzMDU2NzAwNTgwOTE1IiwiZXh0X3BvZF9pZCI6MTkwLCJwb2xpY3lfaWQiOiJhcHAiLCJlbnRpdGx"
      + "lbWVudHMiOiIifQ.signature";

  @BeforeEach
  public void setUp() {
    this.apiClient = mock(ApiClient.class);
    ApiClientFactory apiClientFactory = mock(ApiClientFactory.class);
    this.authSession = mock(AuthSession.class);
    when(apiClientFactory.getPodClient()).thenReturn(this.apiClient);
  }

  @Test
  public void testInvokeApi() throws ApiException, AuthUnauthorizedException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    BearerEnabledApiClient bearerEnabledApiClient =
        spy(new BearerEnabledApiClient(apiClient, authSession, true));

    List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
    Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", "sessionTokenValue");

    bearerEnabledApiClient.invokeAPI("path", "POST", queryParams, "body", headers,
        null, null, "application/json" ,
        "content type", null, new TypeReference<String>() {});

    ArgumentCaptor<Map<String, String>> argCaptor = ArgumentCaptor.forClass(Map.class);
    verify(apiClient).invokeAPI(any(), any(), eq(queryParams), any(), argCaptor.capture(), any(),
        any(), any(), any(), any(), any());
    assertTrue(argCaptor.getValue().containsKey("sessionToken"));
    verify(apiClient).getAuthentications();
    verify(authSession).refreshAuthToken();
  }

  @Test
  public void testInvokeApiWithCommonJwtDisabled() throws ApiException, AuthUnauthorizedException {
    when(authSession.getAuthorizationToken()).thenReturn(null);
    BearerEnabledApiClient bearerEnabledApiClient =
        spy(new BearerEnabledApiClient(apiClient, authSession, false));

    List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
    Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", "sessionTokenValue");

    bearerEnabledApiClient.invokeAPI("path", "POST", queryParams, "body", headers,
        null, null, "application/json" ,
        "content type", null, new TypeReference<String>() {});

    ArgumentCaptor<Map<String, String>> argCaptor = ArgumentCaptor.forClass(Map.class);

    verify(apiClient).invokeAPI(any(), any(), eq(queryParams), any(), argCaptor.capture(), any(),
        any(), any(), any(), any(), any());
    assertTrue(argCaptor.getValue().containsKey("sessionToken"));
    verify(apiClient).getAuthentications();
    verify(authSession, never()).refreshAuthToken();
  }

  @Test
  public void testInvokeApiWithNullAuthToken() throws ApiException, AuthUnauthorizedException {
    when(authSession.getAuthorizationToken()).thenReturn(null);
    BearerEnabledApiClient bearerEnabledApiClient =
        spy(new BearerEnabledApiClient(apiClient, authSession, true));

    List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
    Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", "sessionTokenValue");

    bearerEnabledApiClient.invokeAPI("path", "POST", queryParams, "body", headers,
        null, null, "application/json" ,
        "content type", null, new TypeReference<String>() {});

    ArgumentCaptor<Map<String, String>> argCaptor = ArgumentCaptor.forClass(Map.class);

    verify(apiClient).invokeAPI(any(), any(), eq(queryParams), any(), argCaptor.capture(), any(),
        any(), any(), any(), any(), any());
    assertTrue(argCaptor.getValue().containsKey("sessionToken"));
    verify(apiClient).getAuthentications();
    verify(authSession, never()).refreshAuthToken();
  }

  @Test
  public void testInvokeApiWithExpiredJwt() throws AuthUnauthorizedException, ApiException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    when(authSession.getAuthTokenExpirationDate()).thenReturn(Instant.now().getEpochSecond());
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

     bearerEnabledApiClient.invokeAPI("path", "POST", new ArrayList<>(), "body",
        new HashMap<>(), null, null, "application/json" ,
        "content type", null, new TypeReference<String>() {});

    verify(authSession).refreshAuthToken();
  }

  @Test
  public void testEscapeString() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    final String value = "value";
    bearerEnabledApiClient.escapeString(value);

    verify(apiClient).escapeString(eq(value));
  }

  @Test
  public void testSelectHeaderContentType() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    final String value = "value";
    bearerEnabledApiClient.selectHeaderContentType(value);

    verify(apiClient).selectHeaderContentType(eq(value));
  }

  @Test
  public void testParameterToPairs() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    final String format = "format";
    final String name = "name";
    final String value = "value";
    bearerEnabledApiClient.parameterToPairs(format, name, value);

    verify(apiClient).parameterToPairs(eq(format), eq(name), eq(value));
  }

  @Test
  public void testSelectHeaderAccepts() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    final String value = "value";
    bearerEnabledApiClient.selectHeaderAccept(value);

    verify(apiClient).selectHeaderAccept(eq(value));
  }

  @Test
  public void testParameterToString() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    final String param = "param";
    bearerEnabledApiClient.parameterToString(param);

    verify(apiClient).parameterToString(eq(param));
  }

  @Test
  public void testGetBasePath(){
    when(apiClient.getBasePath()).thenReturn("/pod");
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    String path = bearerEnabledApiClient.getBasePath();

    assertEquals("/pod", path);
  }

  @Test
  public void testGetAuthentications(){
    when(apiClient.getBasePath()).thenReturn("/pod");
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession, true);

    assertNotNull(bearerEnabledApiClient.getAuthentications());
  }
}
