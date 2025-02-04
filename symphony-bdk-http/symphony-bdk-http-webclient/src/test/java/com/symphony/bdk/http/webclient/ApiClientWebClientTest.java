package com.symphony.bdk.http.webclient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;
import com.symphony.bdk.http.api.util.TypeReference;
import com.symphony.bdk.http.webclient.test.BdkMockServer;
import com.symphony.bdk.http.webclient.test.BdkMockServerExtension;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(BdkMockServerExtension.class)
class ApiClientWebClientTest {

  private ApiClient apiClient;

  @BeforeEach
  void setUp(final BdkMockServer mockServer) {

    this.apiClient = mockServer.newApiClient("");
    this.apiClient.getAuthentications().put("testAuth", headerParams -> headerParams.put("Authorization", "test"));
  }

  @Test
  void testInvokeApiNullMethod() {
    assertThrows(ApiException.class, () -> this.apiClient.invokeAPI("/test-api", null, null, null,
        Collections.singletonMap("sessionToken", "test-token"),
        null, null, null, null, new String[] {}, new TypeReference<Response>() {}));
  }

  @Test
  void testInvokeApiTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token")
            .withHeader("Authorization", "test"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    final Map<String, String> headers = new HashMap<>();
    headers.put("sessionToken", "test-token");
    this.apiClient.addEnforcedAuthenticationScheme("testAuth");
    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, null, headers,
            null, null, null, "application/json", new String[] { "testAuth" }, new TypeReference<Response>() {});

    assertTrue(this.apiClient.getBasePath().startsWith("http://localhost:"));
    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiTest2xx(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(201,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody("{\"code\": 201, \"message\": \"success\"}"));

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
            null, null, null, "application/json", new String[] {}, new TypeReference<Response>() {});

    assertTrue(this.apiClient.getBasePath().startsWith("http://localhost:"));
    assertEquals(201, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiExceptionTest(final BdkMockServer mockServer) {
    mockServer.onRequestModifierWithResponse(400,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody("test-error"));

    assertThrows(ApiException.class, () -> this.apiClient.invokeAPI("/test-api", "GET", null, null,
        Collections.singletonMap("sessionToken", "test-token"),
        null, null, null, "application/json", new String[] {}, new TypeReference<Response>() {}));
  }

  @Test
  void testInvokeApiParameterizedTypeTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody("[{\"code\": 200, \"message\": \"success\"}]"));

    ApiResponse<List<Response>> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
            null, null, null, "application/json", new String[] {}, new TypeReference<List<Response>>() {});

    assertEquals(1, response.getData().size());
    assertEquals(200, response.getData().get(0).getCode());
    assertEquals("success", response.getData().get(0).getMessage());
  }

  @Test
  void testInvokeApiNoContentTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(204,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody(""));

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
            null, null, null, "application/json", new String[] {}, new TypeReference<Response>() {});

    assertEquals(204, response.getStatusCode());
    assertNull(response.getData());
  }

  @Test
  void testInvokeApiNoReturnTypeTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody(""));

    ApiResponse<?> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
            null, null, null, "application/json", new String[] {}, null);

    assertEquals(200, response.getStatusCode());
    assertNull(response.getData());
  }

  @Test
  void testInvokeApiWithQueryParamsTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withQueryStringParameter("param", "test-param")
            .withHeader("sessionToken", "test-token")
            .withCookie("cookie", "test-cookie"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "GET", Collections.singletonList(new Pair("param", "test-param")), null,
            Collections.singletonMap("sessionToken", "test-token"),
            Collections.singletonMap("cookie", "test-cookie"), null, null, "application/json", new String[] {},
            new TypeReference<Response>() {});

    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiUrlEncode(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            // percentage needs to be encoded by client
            .withPath("/test+api%25")
            .withQueryStringParameter("param=", "value="),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test+api%", "GET", Collections.singletonList(new Pair("param=", "value=")),
            null, null, null, null, null, "application/json",
            new String[] {},
            new TypeReference<Response>() {});

    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiWithBodyTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withBody(new JsonBody("{\"id\":\"test-id\", \"content\": \"body-content\"}", StandardCharsets.UTF_8,
                MatchType.STRICT))
            .withHeader("sessionToken", "test-token")
            .withCookie("cookie", "test-cookie"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "GET", null, new RequestBody("test-id", "body-content"),
            Collections.singletonMap("sessionToken", "test-token"),
            Collections.singletonMap("cookie", "test-cookie"), null, null, "application/json", new String[] {},
            new TypeReference<Response>() {});

    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiWithFormParamTest(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("POST")
            .withPath("/test-api")
            .withHeader(
                Header.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            )
            .withBody(ParameterBody.params(
                Parameter.param("param-1", "test-1"),
                Parameter.param("param-2", "test-2")
            ))
            .withHeader("sessionToken", "test-token")
            .withCookie("cookie", "test-cookie"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("param-1", "test-1");
    formParams.put("param-2", "test-2");

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "POST", null, null,
            Collections.singletonMap("sessionToken", "test-token"),
            Collections.singletonMap("cookie", "test-cookie"), formParams, null,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE, new String[] {},
            new TypeReference<Response>() {});

    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiWithFormValueTest(final BdkMockServer mockServer, @TempDir Path tempDir)
      throws ApiException, IOException {
    Path tempFilePath = tempDir.resolve("tempFile");
    IOUtils.write("test", new FileOutputStream(tempFilePath.toFile()), "utf-8");
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("POST")
            .withPath("/test-api")
            .withHeader(Header.header("sessionToken", "test-token"))
            .withBody(anyString())
            .withCookie("cookie", "test-cookie"),
        httpResponse -> httpResponse
            .withBody("{\"code\": 200, \"message\": \"success\"}"));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("param-1", tempFilePath.toFile());
    formParams.put("param-2", "test-2");

    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "POST", null, null,
            Collections.singletonMap("sessionToken", "test-token"),
            Collections.singletonMap("cookie", "test-cookie"), formParams, null, MediaType.MULTIPART_FORM_DATA_VALUE,
            new String[] {},
            new TypeReference<Response>() {});

    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void testInvokeApiWithApiClientBodyPart(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("POST")
            .withPath("/test-api")
            .withHeader(Header.header("sessionToken", "test-token"))
            .withBody(anyString())
            .withCookie("cookie", "test-cookie"),
        httpResponse -> httpResponse
            .withBody("{\"code\": 200, \"message\": \"success\"}"));
    Map<String, Object> formParams = new HashMap<>();

    formParams.put("attachment", Collections.singletonList(new ApiClientBodyPart(new ByteArrayInputStream("test".getBytes()), "filename")));


    ApiResponse<Response> response =
        this.apiClient.invokeAPI("/test-api", "POST", null, null,
            Collections.singletonMap("sessionToken", "test-token"),
            Collections.singletonMap("cookie", "test-cookie"), formParams, null, MediaType.MULTIPART_FORM_DATA_VALUE,
            new String[] {},
            new TypeReference<Response>() {});


    assertEquals(200, response.getData().getCode());
    assertEquals("success", response.getData().getMessage());
  }

  @Test
  void shouldClearTraceIdIfNotSet(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    DistributedTracingContext.clear();

    this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
            null, null, null, "application/json", new String[] {}, new TypeReference<Response>() {});

    assertTrue(DistributedTracingContext.getTraceId().isEmpty());
  }

  @Test
  void shouldPreserveExistingTraceId(final BdkMockServer mockServer) throws ApiException {
    mockServer.onRequestModifierWithResponse(200,
        httpRequest -> httpRequest
            .withMethod("GET")
            .withPath("/test-api")
            .withHeader("sessionToken", "test-token"),
        httpResponse -> httpResponse.withBody("{\"code\": 200, \"message\": \"success\"}"));

    String traceId = UUID.randomUUID().toString();
    DistributedTracingContext.setTraceId(traceId);

    this.apiClient.invokeAPI("/test-api", "GET", null, null, Collections.singletonMap("sessionToken", "test-token"),
        null, null, null, "application/json", new String[] {}, new TypeReference<Response>() {});

    assertEquals(traceId, DistributedTracingContext.getTraceId());
  }

  @Test
  void testParameterToString() {
    RequestBody body = new RequestBody("test-id", "content");

    assertEquals("", this.apiClient.parameterToString(null));
    assertEquals("test", this.apiClient.parameterToString("test"));
    assertEquals(body.toString(), this.apiClient.parameterToString(body));
    assertEquals("test1,test2", this.apiClient.parameterToString(Arrays.asList("test1", "test2")));
  }

  @Test
  void parameterToPairsTest() {
    List<Pair> pairs = new ArrayList<>();
    pairs.addAll(this.apiClient.parameterToPairs("", "test", "test-value"));
    pairs.addAll(this.apiClient.parameterToPairs("", "test", Collections.emptyList()));
    pairs.addAll(this.apiClient.parameterToPairs("multi", "multi", Arrays.asList("test1", "test2")));
    pairs.addAll(this.apiClient.parameterToPairs("csv", "csv", Arrays.asList("test1", "test2")));
    pairs.addAll(this.apiClient.parameterToPairs("ssv", "ssv", Arrays.asList("test1", "test2")));
    pairs.addAll(this.apiClient.parameterToPairs("tsv", "tsv", Arrays.asList("test1", "test2")));
    pairs.addAll(this.apiClient.parameterToPairs("pipes", "pipes", Arrays.asList("test1", "test2")));
    pairs.addAll(this.apiClient.parameterToPairs("pipes", "", Arrays.asList("test1", "test2")));

    assertEquals(7, pairs.size());
    assertEquals("test-value", pairs.get(0).getValue());
    assertEquals("test1", pairs.get(1).getValue());
    assertEquals("test2", pairs.get(2).getValue());
    assertEquals("test1,test2", pairs.get(3).getValue());
    assertEquals("test1 test2", pairs.get(4).getValue());
    assertEquals("test1\ttest2", pairs.get(5).getValue());
    assertEquals("test1|test2", pairs.get(6).getValue());
  }

  @Test
  void selectHeaderAcceptTest() {
    assertNull(this.apiClient.selectHeaderAccept());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, this.apiClient.selectHeaderAccept(MediaType.APPLICATION_JSON_VALUE));
    assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE + "," + MediaType.MULTIPART_FORM_DATA_VALUE,
        this.apiClient.selectHeaderAccept(MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE));
  }

  @Test
  void selectHeaderContentTypeTest() {
    assertEquals("application/json", this.apiClient.selectHeaderContentType());
    assertEquals(MediaType.APPLICATION_JSON_VALUE, this.apiClient.selectHeaderContentType(
        MediaType.APPLICATION_JSON_VALUE));
    assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE, this.apiClient.selectHeaderContentType(
        MediaType.APPLICATION_FORM_URLENCODED_VALUE));
  }

  @Test
  void escapeStringTest() {
    String url = "http://localhost/search?q=hello+world";

    assertEquals("http://localhost/search?q=hello+world", this.apiClient.escapeString(url));
  }

  private static class RequestBody {
    private String id;
    private String content;

    protected RequestBody(String id, String content) {
      this.id = id;
      this.content = content;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }


  private static class Response {
    private int code;
    private String message;

    public int getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }
  }
}
