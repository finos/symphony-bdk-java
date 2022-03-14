package com.symphony.bdk.core.test;

import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.jersey2.ApiClientJersey2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.mockito.ArgumentMatchers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MockApiClient {

  private static final ObjectMapper MAPPER = new JsonMapper();
  static {
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
  private final Client httpClient = mock(Client.class);
  {
    when(this.httpClient.target(anyString())).thenThrow(new MockApiClientException("Calling the mocked ApiClient with wrong path"));
  }

  public void onGet(String path, String resContent) {
    this.onRequestWithResponseCode("GET", 200, path, null, resContent);
  }

  public void onGet(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("GET", statusCode, path, null, resContent);
  }

  public void onGet(String path, List<Pair> queryParams, String resContent) {
    this.onRequestWithResponseCode("GET", 200, path, queryParams, resContent);
  }

  public void onPost(String path, String resContent) {
    this.onRequestWithResponseCode("POST", 200, path, null, resContent);
  }

  public void onPost(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("POST", statusCode, path, null, resContent);
  }

  public void onPatch(String path, String resContent) {
    this.onRequestWithResponseCode("PATCH", 200, path, null, resContent);
  }

  public void onPatch(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("PATCH", statusCode, path, null, resContent);
  }

  public void onPut(String path, String resContent) {
    this.onRequestWithResponseCode("PUT", 200, path, null, resContent);
  }

  public void onPut(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("PUT", statusCode, path, null, resContent);
  }

  public void onDelete(String path, String resContent) {
    this.onRequestWithResponseCode("DELETE", 200, path, null, resContent);
  }

  public void onDelete(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("DELETE", statusCode, path, null, resContent);
  }

  public ApiClient getApiClient(String basePath) {
    return new ApiClientJersey2(this.httpClient, basePath, new HashMap<>(), null);
  }

  private void onRequestWithResponseCode(String method, int statusCode, String path, List<Pair> queryParams,
      String resContent)  {
    WebTarget webTarget = null;
    try {
      webTarget = this.httpClient.target(path);
    } catch (RuntimeException ignored) {
      // ignored
    }
    if (webTarget == null) {
      webTarget = this.initMockWebTarget(queryParams);
      doReturn(webTarget).when(this.httpClient).target(path);
    }
    Invocation.Builder invocationBuilder = webTarget.request();
    if (invocationBuilder == null) {
      invocationBuilder = this.initInvocationBuilder();
      when(webTarget.request()).thenReturn(invocationBuilder);
    }

    final Response httpResponse = Response.status(statusCode).type(MediaType.APPLICATION_JSON).build();

    Response response = spy(httpResponse);
    doAnswer(answer((GenericType<?> type) -> {
      if (type.getType() instanceof ParameterizedType) {
        return MAPPER.readValue(resContent, MAPPER.getTypeFactory().constructCollectionType(List.class,
            (Class<?>) ((ParameterizedType) type.getType()).getActualTypeArguments()[0]));
      }
      return MAPPER.readValue(resContent, (Class<?>) type.getType());
    })).when(response).readEntity(ArgumentMatchers.<GenericType<?>>any());
    when(response.hasEntity()).thenReturn(true);

    if ("GET".equals(method)) {
      doReturn(response).when(invocationBuilder).get();
    } else if ("POST".equals(method)) {
      doReturn(response).when(invocationBuilder).post(any(Entity.class));
    } else if ("PUT".equals(method)) {
      doReturn(response).when(invocationBuilder).put(any(Entity.class));
    } else if ("DELETE".equals(method)) {
      doReturn(response).when(invocationBuilder).method(eq("DELETE"), any(Entity.class));
    } else if ("PATCH".equals(method)) {
      doReturn(response).when(invocationBuilder).method(eq("PATCH"), any(Entity.class));
    }
  }

  private Invocation.Builder initInvocationBuilder() {
    Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);

    when(invocationBuilder.accept(anyString())).thenReturn(invocationBuilder);
    when(invocationBuilder.header(anyString(), any())).thenReturn(invocationBuilder);
    when(invocationBuilder.cookie(anyString(), any())).thenReturn(invocationBuilder);
    when(invocationBuilder.get()).thenThrow(new MockApiClientException("Calling the mocked ApiClient with wrong method"));
    when(invocationBuilder.post(any(Entity.class))).thenThrow(new MockApiClientException("Calling the mocked ApiClient with wrong method"));
    when(invocationBuilder.method(eq("DELETE"), any(Entity.class))).thenThrow(new MockApiClientException("Calling the mocked ApiClient with wrong method"));
    return invocationBuilder;
  }

  private WebTarget initMockWebTarget(List<Pair> queryParams) {
    WebTarget webTarget = mock(WebTarget.class);

    if (queryParams != null) {
      when(webTarget.queryParam(anyString(), any())).thenThrow(new MockApiClientException("Calling the mocked ApiClient with wrong query params"));
      for (Pair queryParam : queryParams) {
        when(webTarget.queryParam(eq(queryParam.getName()), eq(escapeString(queryParam.getValue())))).thenReturn(webTarget);
      }
    } else {
      when(webTarget.queryParam(anyString(), any())).thenReturn(webTarget);
    }

    return webTarget;
  }

  private static String escapeString(String str) {
    try {
      return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      return str;
    }
  }
}
