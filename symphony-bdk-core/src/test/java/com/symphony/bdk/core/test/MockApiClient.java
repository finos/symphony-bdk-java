package com.symphony.bdk.core.test;

import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.mockito.ArgumentMatchers;

import java.lang.reflect.ParameterizedType;
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
  private final Client httpClient;
  private final WebTarget webTarget;
  private final Invocation.Builder invocationBuilder;

  public MockApiClient() {
    this.httpClient = mock(Client.class);
    this.webTarget = mock(WebTarget.class);
    this.invocationBuilder = mock(Invocation.Builder.class);

    when(this.webTarget.queryParam(anyString(), any())).thenReturn(this.webTarget);
    when(this.webTarget.request()).thenReturn(this.invocationBuilder);
    when(this.invocationBuilder.accept(anyString())).thenReturn(this.invocationBuilder);
    when(this.invocationBuilder.header(anyString(), any())).thenReturn(this.invocationBuilder);
    when(this.invocationBuilder.cookie(anyString(), any())).thenReturn(this.invocationBuilder);
  }

  public void onRequestWithResponseCode(String method, int statusCode, String path, String resContent)  {

    final Response httpResponse = Response.status(statusCode).type(MediaType.APPLICATION_JSON).build();

    Response response = spy(httpResponse);
    doAnswer(answer((GenericType<?> type) -> {
      if (type.getType() instanceof ParameterizedType) {
        return MAPPER.readValue(resContent, MAPPER.getTypeFactory().constructCollectionType(List.class,
            (Class<?>) ((ParameterizedType) type.getType()).getActualTypeArguments()[0]));
      }
      return MAPPER.readValue(resContent, (Class<?>) type.getType());
    })).when(response).readEntity(ArgumentMatchers.<GenericType<?>>any());

    when(this.httpClient.target(path)).thenReturn(this.webTarget);
    if ("GET".equals(method)) {
      when(this.invocationBuilder.get()).thenReturn(response);
    } else if ("POST".equals(method)) {
      when(this.invocationBuilder.post(any(Entity.class))).thenReturn(response);
    } else if ("DELETE".equals(method)) {
      when(this.invocationBuilder.method("DELETE", any(Entity.class))).thenReturn(response);
    }
  }

  public void onGet(String path, String resContent) {
    this.onRequestWithResponseCode("GET", 200, path, resContent);
  }

  public void onPost(String path, String resContent) {
    this.onRequestWithResponseCode("POST", 200, path, resContent);
  }

  public void onGet(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("GET", statusCode, path, resContent);
  }

  public void onPost(int statusCode, String path, String resContent) {
    this.onRequestWithResponseCode("POST", statusCode, path, resContent);
  }

  public ApiClient getApiClient(String basePath) {
    return new ApiClientJersey2(this.httpClient, basePath, new HashMap<>(), null);
  }
}
