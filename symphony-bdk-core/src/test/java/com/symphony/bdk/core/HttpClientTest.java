package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.jersey2.ApiClientBuilderJersey2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import javax.ws.rs.core.GenericType;

public class HttpClientTest {

  private MockApiClient mockApiClient;
  private HttpClient.Builder httpClientBuilder;

  @BeforeEach
  void setup() {
    this.mockApiClient = new MockApiClient();
    ApiClient apiClient = this.mockApiClient.getApiClient("");

    ApiClientBuilder builder = new ApiClientBuilderJersey2();
    ApiClientBuilder spyBuilder = spy(builder);

    ApiClientBuilderProvider provider = mock(ApiClientBuilderProvider.class);
    doReturn(spyBuilder).when(provider).newInstance();
    when(spyBuilder.build()).thenReturn(apiClient);

    mockApiClient.onPost("/string", "1234");
    this.httpClientBuilder = HttpClient.builder()
        .apiClientBuilderProvider(provider)
        .basePath("https://acme.symphony.com")
        .body("body")
        .header("key", "value")
        .accept("application/json")
        .cookie("cookieKey", "cookieValue")
        .queryParam("queryKey", "quer")
        .contentType("application/json")
        .keyStore("./src/test/resources/certs/identity.p12", "password")
        .trustStore("./src/test/resources/certs/identity.p12", "password");
  }

  @Test
  void testBuilderCreate() {
    HttpClient.Builder builder = HttpClient.builder();
    assertNotNull(builder);
    HttpClient httpClient = new HttpClient();
    assertNotNull(httpClient);
  }

  @Test
  void testBuilderPost() throws ApiException, IOException {
    this.mockApiClient.onPost("/string/get", "1234");
    String string = this.httpClientBuilder.path("/string/get")
        .formParams("formParamKey", "formParamValue")
        .post(new GenericType<String>() {});
    assertEquals(string, "1234");
  }

  @Test
  void testBuilderGet() throws ApiException, IOException {
    this.mockApiClient.onGet("/string/post", "1234");
    String string = this.httpClientBuilder.path("/string/post").get(new GenericType<String>() {});
    assertEquals(string, "1234");
  }

  @Test
  void testBuilderDelete() throws ApiException, IOException {
    this.mockApiClient.onDelete("/string/post", "1234");
    String string = this.httpClientBuilder.path("/string/post").delete(new GenericType<String>() {});
    assertEquals(string, "1234");
  }

}
