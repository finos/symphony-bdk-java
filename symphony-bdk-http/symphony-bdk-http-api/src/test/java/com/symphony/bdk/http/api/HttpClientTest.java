package com.symphony.bdk.http.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.util.ApiUtils;
import com.symphony.bdk.http.api.util.TypeReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HttpClientTest {

  @Mock
  private ApiClient apiClient;

  @Captor
  private ArgumentCaptor<List<Pair>> queryParams;

  @Captor
  private ArgumentCaptor<Map<String, String>> headerParams;

  @Captor
  private ArgumentCaptor<Map<String, String>> cookieParams;

  @Captor
  private ArgumentCaptor<Map<String, Object>> formParams;

  @Test
  public void usageWithFormParams() throws ApiException {

    final HttpClient httpClient = HttpClient.builder(this::mockedApiClientBuilder)
      .basePath("https://localhost:8080")
      .header("Connection", "Keep-Alive")
      .header("Keep-Alive", "timeout=5, max=1000")
      .cookie("foo", "bar")
      .build();

    httpClient.path("/api/v1/users")
        .header("Authorization", "Bearer AbCdEf123456")
        .queryParam("test1", "test2")
        .formParam("test3", "test4")
        .formParam("test3", "test5")
        .get(new TypeReference<String>() {});

    verify(apiClient).invokeAPI(eq("/api/v1/users"), eq("GET"), queryParams.capture(), isNull(),
        headerParams.capture(), cookieParams.capture(), formParams.capture(), isNull(), eq("application/json"), any(),
        any());

    assertEquals(Collections.singletonList(new Pair("test1", "test2")), queryParams.getValue());
    assertEquals(new HashMap<String, String>() {{
      put("Connection", "Keep-Alive");
      put("Keep-Alive", "timeout=5, max=1000");
      put("Authorization", "Bearer AbCdEf123456");
    }}, headerParams.getValue());
    assertEquals(Collections.singletonMap("foo", "bar"), cookieParams.getValue());
    assertEquals(Collections.singletonMap("test3", Arrays.asList("test4", "test5")), formParams.getValue());
  }

  @Test
  public void usageWithoutFormParams() throws ApiException {

    final HttpClient httpClient = HttpClient.builder(this::mockedApiClientBuilder)
        .basePath("https://localhost:8080")
        .build();

    httpClient.path("/api/v1/users")
        .get(new TypeReference<String>() {});

    verify(apiClient).invokeAPI(eq("/api/v1/users"), eq("GET"), queryParams.capture(), isNull(),
        headerParams.capture(), cookieParams.capture(), formParams.capture(), isNull(), eq("application/json"), any(),
        any());

    assertNull(queryParams.getValue());
    assertEquals(Collections.emptyMap(), headerParams.getValue());
    assertEquals(Collections.emptyMap(), cookieParams.getValue());
    assertEquals(Collections.emptyMap(), formParams.getValue());
  }

  @Test
  public void userAgent() {
    assertTrue(ApiUtils.getUserAgent().matches("^Symphony-BDK-Java/\\S+ Java/\\S+"));
  }

  private ApiClientBuilder mockedApiClientBuilder() {
    final ApiClientBuilder apiClientBuilder = mock(ApiClientBuilder.class);
    when(apiClientBuilder.build()).thenReturn(apiClient);
    return apiClientBuilder;
  }
}
