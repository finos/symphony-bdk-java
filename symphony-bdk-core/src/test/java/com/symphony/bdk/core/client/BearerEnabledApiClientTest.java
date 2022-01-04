package com.symphony.bdk.core.client;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.loadbalancing.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.http.api.ApiClient;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.util.TypeReference;

import org.bouncycastle.crypto.agreement.srp.SRP6Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    when(apiClientFactory.getPodClient(any())).thenReturn(this.apiClient);

  }

  @Test
  public void testInvokeApiIsDelegated() throws ApiException {
    when(authSession.getAuthorizationToken()).thenReturn(JWT);
    BearerEnabledApiClient bearerEnabledApiClient =
        spy(new BearerEnabledApiClient(apiClient, authSession));

    List<Pair> queryParams = Collections.singletonList(new Pair("param", "value"));
    Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", "sessionTokenValue");

    bearerEnabledApiClient.invokeAPI("path", "POST", queryParams, "body", headers,
        null, null, "application/json" ,
        "content type", null, new TypeReference<String>() {});

    ArgumentCaptor<Map<String, String>> argCaptor = ArgumentCaptor.forClass(Map.class);

    verify(apiClient).invokeAPI(any(), any(), eq(queryParams), any(), argCaptor.capture(), any(),
        any(), any(), any(), any(), any());
    assertTrue(argCaptor.getValue().containsKey("Authorization"));
    assertFalse(argCaptor.getValue().containsKey("sessionToken"));
  }

  @Test
  public void testEscapeStringIsDelegated() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession);

    final String value = "value";
    bearerEnabledApiClient.escapeString(value);

    verify(apiClient).escapeString(eq(value));
  }

  @Test
  public void testSelectHeaderContentTypeIsDelegated() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession);

    final String value = "value";
    bearerEnabledApiClient.selectHeaderContentType(value);

    verify(apiClient).selectHeaderContentType(eq(value));
  }

  @Test
  public void testParameterToPairsIsDelegated() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession);

    final String format = "format";
    final String name = "name";
    final String value = "value";

    bearerEnabledApiClient.parameterToPairs(format, name, value);

    verify(apiClient).parameterToPairs(eq(format), eq(name), eq(value));
  }

  @Test
  public void testSelectHeaderAcceptsIsDelegated() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession);

    final String value = "value";
    bearerEnabledApiClient.selectHeaderAccept(value);

    verify(apiClient).selectHeaderAccept(eq(value));
  }

  @Test
  public void testParameterToStringIsDelegated() {
    BearerEnabledApiClient bearerEnabledApiClient = new BearerEnabledApiClient(apiClient, authSession);
    final String param = "param";

    bearerEnabledApiClient.parameterToString(param);

    verify(apiClient).parameterToString(eq(param));
  }
}
