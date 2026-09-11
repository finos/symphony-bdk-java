package com.symphony.bdk.http.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;
import com.symphony.bdk.http.api.util.TypeReference;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class ApiClientJdkTest {

  private ClientAndServer mockServer;

  @BeforeEach
  void setUp() {
    this.mockServer = ClientAndServer.startClientAndServer();
  }

  @AfterEach
  void tearDown() {
    this.mockServer.stop();
  }

  private ApiClient buildClient() {
    return new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .build();
  }

  private ApiResponse<Object> invokeGet(ApiClient client, String path) throws ApiException {
    return client.invokeAPI(path, "GET", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        new HashMap<>(), "application/json", "application/json", null, null);
  }

  // --------------------------------------------------------------------------------------------
  // Distributed tracing header propagation
  // --------------------------------------------------------------------------------------------

  @Test
  void traceIdIsGeneratedAndClearedWhenAbsentBeforeTheCall() throws ApiException {
    DistributedTracingContext.clear();
    this.mockServer.when(HttpRequest.request().withPath("/test")).respond(HttpResponse.response().withStatusCode(200));

    this.invokeGet(this.buildClient(), "/test");

    assertTrue(DistributedTracingContext.getTraceId().isEmpty());
  }

  @Test
  void traceIdIsPreservedAndNotClearedWhenAlreadySetBeforeTheCall() throws ApiException {
    String traceId = UUID.randomUUID().toString();
    DistributedTracingContext.setTraceId(traceId);
    this.mockServer.when(HttpRequest.request().withPath("/test")).respond(HttpResponse.response().withStatusCode(200));

    this.invokeGet(this.buildClient(), "/test");

    assertEquals(traceId, DistributedTracingContext.getTraceId());
    DistributedTracingContext.clear();
  }

  @Test
  void traceIdHeaderIsSentOnOutgoingRequest() throws ApiException {
    String traceId = UUID.randomUUID().toString();
    DistributedTracingContext.setTraceId(traceId);
    this.mockServer.when(HttpRequest.request().withPath("/test")).respond(HttpResponse.response().withStatusCode(200));

    this.invokeGet(this.buildClient(), "/test");

    HttpRequest[] recorded = this.mockServer.retrieveRecordedRequests(HttpRequest.request().withPath("/test"));
    assertEquals(1, recorded.length);
    assertEquals(traceId, recorded[0].getFirstHeader(DistributedTracingContext.TRACE_ID));
    DistributedTracingContext.clear();
  }

  // --------------------------------------------------------------------------------------------
  // Content encoding: query params, cookies, JSON body, form-urlencoded, multipart
  // --------------------------------------------------------------------------------------------

  @Test
  void queryParamsAreAppendedAndEscaped() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/query")).respond(HttpResponse.response().withStatusCode(200));

    List<com.symphony.bdk.http.api.Pair> queryParams = Arrays.asList(new com.symphony.bdk.http.api.Pair("q", "a b"));

    this.buildClient().invokeAPI("/query", "GET", queryParams, null, new HashMap<>(), new HashMap<>(),
        new HashMap<>(), "application/json", "application/json", null, null);

    HttpRequest[] recorded = this.mockServer.retrieveRecordedRequests(HttpRequest.request().withPath("/query"));
    assertEquals(1, recorded.length);
    assertEquals("a b", recorded[0].getFirstQueryStringParameter("q"));
  }

  @Test
  void cookieParamsAreSentAsCookieHeader() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/cookie")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, String> cookieParams = new HashMap<>();
    cookieParams.put("session", "abc123");

    this.buildClient().invokeAPI("/cookie", "GET", Collections.emptyList(), null, new HashMap<>(), cookieParams,
        new HashMap<>(), "application/json", "application/json", null, null);

    HttpRequest[] recorded = this.mockServer.retrieveRecordedRequests(HttpRequest.request().withPath("/cookie"));
    assertEquals(1, recorded.length);
    assertEquals("session=abc123", recorded[0].getFirstHeader("Cookie"));
  }

  @Test
  void jsonBodyIsSerializedUsingJacksonMapper() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/body")).respond(HttpResponse.response().withStatusCode(200));

    Model model = new Model();
    model.name = "foo";

    this.buildClient().invokeAPI("/body", "POST", Collections.emptyList(), model, new HashMap<>(), new HashMap<>(),
        new HashMap<>(), "application/json", "application/json", null, null);

    String body = this.retrieveSingleRequestBody("/body");
    assertEquals("foo", new JSON().getMapper().readTree(body).get("name").asString());
  }

  @Test
  void stringBodyIsSentAsRawUtf8Bytes() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/raw")).respond(HttpResponse.response().withStatusCode(200));

    this.buildClient().invokeAPI("/raw", "POST", Collections.emptyList(), "raw-string-body", new HashMap<>(),
        new HashMap<>(), new HashMap<>(), "application/json", "text/plain", null, null);

    assertEquals("raw-string-body", this.retrieveSingleRequestBody("/raw"));
  }

  @Test
  void unserializableBodyThrowsApiException() {
    this.mockServer.when(HttpRequest.request().withPath("/bad-body")).respond(HttpResponse.response().withStatusCode(200));

    Object unserializable = new Object() {
      public String getName() {
        throw new RuntimeException("boom");
      }
    };

    assertThrows(ApiException.class, () -> this.buildClient().invokeAPI("/bad-body", "POST",
        Collections.emptyList(), unserializable, new HashMap<>(), new HashMap<>(), new HashMap<>(),
        "application/json", "application/json", null, null));
  }

  @Test
  void formUrlEncodedBodyIsSentAsPercentEncodedKeyValuePairs() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/form")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("hello world", "a b&c");

    this.buildClient().invokeAPI("/form", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "application/x-www-form-urlencoded", null, null);

    HttpRequest[] recorded = this.mockServer.retrieveRecordedRequests(HttpRequest.request().withPath("/form"));
    assertEquals(1, recorded.length);
    assertEquals("hello%20world=a%20b%26c", recorded[0].getBodyAsString());
  }

  @Test
  void multipartFileFormParamProducesMatchingPart(@TempDir Path tempDir) throws ApiException, IOException {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    File file = tempDir.resolve("hello.txt").toFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write("hello content");
    }

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("file", file);

    this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "multipart/form-data", null, null);

    String body = this.retrieveSingleRequestBody("/upload");
    assertTrue(body.contains("Content-Disposition: form-data; name=\"file\"; filename=\"hello.txt\""));
    assertTrue(body.contains("hello content"));
  }

  @Test
  void multipartCollectionOfFilesProducesOnePartPerFile(@TempDir Path tempDir) throws ApiException, IOException {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    File file1 = tempDir.resolve("one.txt").toFile();
    File file2 = tempDir.resolve("two.txt").toFile();
    try (FileWriter w1 = new FileWriter(file1); FileWriter w2 = new FileWriter(file2)) {
      w1.write("content-one");
      w2.write("content-two");
    }

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("files", Arrays.asList(file1, file2));

    this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "multipart/form-data", null, null);

    String body = this.retrieveSingleRequestBody("/upload");
    assertTrue(body.contains("filename=\"one.txt\""));
    assertTrue(body.contains("content-one"));
    assertTrue(body.contains("filename=\"two.txt\""));
    assertTrue(body.contains("content-two"));
  }

  @Test
  void multipartApiClientBodyPartProducesPartFromInputStream() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("attachment",
        new ApiClientBodyPart(new java.io.ByteArrayInputStream("stream-content".getBytes()), "stream.bin"));

    this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "multipart/form-data", null, null);

    String body = this.retrieveSingleRequestBody("/upload");
    assertTrue(body.contains("filename=\"stream.bin\""));
    assertTrue(body.contains("stream-content"));
  }

  @Test
  void multipartApiClientBodyPartArrayProducesOnePartPerElement() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("attachments", new ApiClientBodyPart[] {
        new ApiClientBodyPart(new java.io.ByteArrayInputStream("first".getBytes()), "first.bin"),
        new ApiClientBodyPart(new java.io.ByteArrayInputStream("second".getBytes()), "second.bin")
    });

    this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "multipart/form-data", null, null);

    String body = this.retrieveSingleRequestBody("/upload");
    assertTrue(body.contains("filename=\"first.bin\""));
    assertTrue(body.contains("first"));
    assertTrue(body.contains("filename=\"second.bin\""));
    assertTrue(body.contains("second"));
  }

  @Test
  void multipartPlainFieldIsSentAsFormDataField() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("description", "some text value");

    this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
        formParams, "application/json", "multipart/form-data", null, null);

    String body = this.retrieveSingleRequestBody("/upload");
    assertTrue(body.contains("Content-Disposition: form-data; name=\"description\""));
    assertTrue(body.contains("some text value"));
  }

  @Test
  void multipartWithMissingFileThrowsApiException(@TempDir Path tempDir) {
    this.mockServer.when(HttpRequest.request().withPath("/upload")).respond(HttpResponse.response().withStatusCode(200));

    Map<String, Object> formParams = new HashMap<>();
    formParams.put("file", tempDir.resolve("does-not-exist.txt").toFile());

    assertThrows(ApiException.class, () -> this.buildClient().invokeAPI("/upload", "POST", Collections.emptyList(),
        null, new HashMap<>(), new HashMap<>(), formParams, "application/json", "multipart/form-data", null,
        null));
  }

  private String retrieveSingleRequestBody(String path) {
    HttpRequest[] recorded = this.mockServer.retrieveRecordedRequests(HttpRequest.request().withPath(path));
    assertEquals(1, recorded.length);
    return recorded[0].getBodyAsString();
  }

  // --------------------------------------------------------------------------------------------
  // Response handling
  // --------------------------------------------------------------------------------------------

  @Test
  void noContentResponseReturnsApiResponseWithNullData() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/empty")).respond(HttpResponse.response().withStatusCode(204));

    ApiResponse<Object> response = this.invokeGet(this.buildClient(), "/empty");

    assertEquals(204, response.getStatusCode());
    assertNull(response.getData());
  }

  @Test
  void byteArrayReturnTypeReturnsRawResponseBytes() throws ApiException {
    byte[] payload = {1, 2, 3, 4};
    this.mockServer.when(HttpRequest.request().withPath("/bytes"))
        .respond(HttpResponse.response().withStatusCode(200).withBody(payload));

    ApiResponse<byte[]> response = this.buildClient().invokeAPI("/bytes", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/octet-stream", "application/json", null,
        new TypeReference<byte[]>() {});

    assertEquals(200, response.getStatusCode());
    assertTrue(Arrays.equals(payload, response.getData()));
  }

  @Test
  void jsonResponseIsDeserializedIntoReturnType() throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/model"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"name\":\"foo\",\"ignoredExtraField\":\"bar\"}"));

    ApiResponse<Model> response = this.buildClient().invokeAPI("/model", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/json", "application/json", null,
        new TypeReference<Model>() {});

    assertEquals(200, response.getStatusCode());
    assertEquals("foo", response.getData().name);
  }

  @Test
  void nonSuccessResponseThrowsApiExceptionWithBody() {
    this.mockServer.when(HttpRequest.request().withPath("/error"))
        .respond(HttpResponse.response().withStatusCode(400).withBody("bad request details"));

    ApiException exception =
        assertThrows(ApiException.class, () -> this.invokeGet(this.buildClient(), "/error"));

    assertEquals(400, exception.getCode());
    assertEquals("bad request details", exception.getResponseBody());
  }

  @Test
  void fileDownloadWritesResponseBodyToTemporaryFolderUsingContentDispositionFilename(@TempDir Path tempDir)
      throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/download"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withHeader("Content-Disposition", "attachment; filename=\"report.pdf\"")
            .withBody("file-content"));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withTemporaryFolderPath(tempDir.toString())
        .build();

    ApiResponse<File> response = client.invokeAPI("/download", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/pdf", "application/json", null,
        new TypeReference<File>() {});

    assertEquals(200, response.getStatusCode());
    File downloaded = response.getData();
    assertTrue(downloaded.getParentFile().toPath().equals(tempDir));
    assertTrue(downloaded.getName().startsWith("report"));
    assertTrue(downloaded.getName().endsWith(".pdf"));
  }

  @Test
  void fileDownloadWithoutContentDispositionUsesGenericPrefix(@TempDir Path tempDir) throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/download"))
        .respond(HttpResponse.response().withStatusCode(200).withBody("file-content"));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withTemporaryFolderPath(tempDir.toString())
        .build();

    ApiResponse<File> response = client.invokeAPI("/download", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/pdf", "application/json", null,
        new TypeReference<File>() {});

    assertEquals(200, response.getStatusCode());
    assertTrue(response.getData().getName().startsWith("download-"));
  }

  @Test
  void fileDownloadWithShortFilenameFallsBackToGenericPrefix(@TempDir Path tempDir) throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/download"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withHeader("Content-Disposition", "attachment; filename=\"a\"")
            .withBody("file-content"));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withTemporaryFolderPath(tempDir.toString())
        .build();

    ApiResponse<File> response = client.invokeAPI("/download", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/pdf", "application/json", null,
        new TypeReference<File>() {});

    assertEquals(200, response.getStatusCode());
    assertTrue(response.getData().getName().startsWith("download-"));
  }

  @Test
  void fileDownloadWithFilenameWithoutExtension(@TempDir Path tempDir) throws ApiException {
    this.mockServer.when(HttpRequest.request().withPath("/download"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withHeader("Content-Disposition", "attachment; filename=\"reportnoext\"")
            .withBody("file-content"));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withTemporaryFolderPath(tempDir.toString())
        .build();

    ApiResponse<File> response = client.invokeAPI("/download", "GET", Collections.emptyList(), null,
        new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/pdf", "application/json", null,
        new TypeReference<File>() {});

    assertEquals(200, response.getStatusCode());
    assertTrue(response.getData().getName().startsWith("reportnoext-"));
  }

  @Test
  void nonSuccessFileDownloadResponseThrowsApiExceptionWithBody(@TempDir Path tempDir) {
    this.mockServer.when(HttpRequest.request().withPath("/download"))
        .respond(HttpResponse.response().withStatusCode(404).withBody("not found"));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withTemporaryFolderPath(tempDir.toString())
        .build();

    ApiException exception = assertThrows(ApiException.class,
        () -> client.invokeAPI("/download", "GET", Collections.emptyList(), null, new HashMap<>(), new HashMap<>(),
            new HashMap<>(), "application/pdf", "application/json", null, new TypeReference<File>() {}));

    assertEquals(404, exception.getCode());
    assertEquals("not found", exception.getResponseBody());
  }

  @Test
  void malformedJsonResponseThrowsApiException() {
    this.mockServer.when(HttpRequest.request().withPath("/malformed"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withHeader("Content-Type", "application/json")
            .withBody("not-json"));

    assertThrows(ApiException.class, () -> this.buildClient().invokeAPI("/malformed", "GET",
        Collections.emptyList(), null, new HashMap<>(), new HashMap<>(), new HashMap<>(), "application/json",
        "application/json", null, new TypeReference<Model>() {}));
  }

  @Test
  void interruptedThreadDuringSendThrowsIllegalStateException() throws IOException {
    this.mockServer.when(HttpRequest.request().withPath("/slow"))
        .respond(HttpResponse.response().withStatusCode(200).withDelay(new Delay(TimeUnit.SECONDS, 2)));

    ApiClient client = this.buildClient();
    Thread current = Thread.currentThread();
    java.util.concurrent.ScheduledExecutorService interrupter =
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
    interrupter.schedule(current::interrupt, 200, TimeUnit.MILLISECONDS);

    try {
      assertThrows(IllegalStateException.class, () -> this.invokeGet(client, "/slow"));
    } finally {
      interrupter.shutdownNow();
      // clear the interrupted flag so later tests in the same JVM aren't affected
      Thread.interrupted();
    }
  }

  // --------------------------------------------------------------------------------------------
  // Outgoing request logging
  // --------------------------------------------------------------------------------------------

  @Test
  void completedRequestProducesDebugLogEntry() throws ApiException {
    Logger logger = (Logger) LoggerFactory.getLogger("com.symphony.bdk.requests.outgoing");
    Level originalLevel = logger.getLevel();
    logger.setLevel(Level.DEBUG);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);

    try {
      this.mockServer.when(HttpRequest.request().withPath("/logged"))
          .respond(HttpResponse.response().withStatusCode(200));

      this.invokeGet(this.buildClient(), "/logged");

      boolean found = appender.list.stream()
          .anyMatch(event -> event.getFormattedMessage().contains("status=200")
              && event.getFormattedMessage().contains("/logged"));
      assertTrue(found, "expected a DEBUG log entry with status and url, got: " + appender.list);
    } finally {
      logger.detachAppender(appender);
      logger.setLevel(originalLevel);
    }
  }

  // --------------------------------------------------------------------------------------------
  // Exception translation for retry compatibility (D2)
  // --------------------------------------------------------------------------------------------

  @Test
  void connectTimeoutTranslatesToConnectExceptionRootCause() {
    ApiClientJdk client =
        new ApiClientJdk(HttpClient.newHttpClient(), "http://localhost", new HashMap<>(), null, null,
            new ArrayList<>());

    RuntimeException translated = client.translateAndWrap(new HttpConnectTimeoutException("timed out"));

    assertTrue(hasExactRootCause(translated, ConnectException.class));
    assertTrue(RetryWithRecoveryBuilder.isNetworkIssueOrMinorError(translated));
  }

  @Test
  void requestTimeoutTranslatesToSocketTimeoutExceptionRootCause() {
    ApiClientJdk client =
        new ApiClientJdk(HttpClient.newHttpClient(), "http://localhost", new HashMap<>(), null, null,
            new ArrayList<>());

    RuntimeException translated = client.translateAndWrap(new HttpTimeoutException("timed out"));

    assertTrue(hasExactRootCause(translated, SocketTimeoutException.class));
    assertTrue(RetryWithRecoveryBuilder.isNetworkIssueOrMinorError(translated));
  }

  @Test
  void requestTimeoutDuringSendSurfacesAsRetryableSocketTimeoutException() {
    this.mockServer.when(HttpRequest.request().withPath("/slow"), Times.once())
        .respond(HttpResponse.response().withStatusCode(200).withDelay(new Delay(TimeUnit.SECONDS, 3)));

    ApiClient client = new ApiClientBuilderJdk()
        .withBasePath("http://localhost:" + this.mockServer.getPort())
        .withReadTimeout(200)
        .build();

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> this.invokeGet(client, "/slow"));

    assertTrue(hasExactRootCause(thrown, SocketTimeoutException.class));
    assertTrue(RetryWithRecoveryBuilder.isNetworkIssueOrMinorError(thrown));
  }

  private static boolean hasExactRootCause(Throwable throwable, Class<? extends Throwable> expected) {
    Throwable current = throwable;
    while (current != null) {
      if (current.getClass().equals(expected)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  static class Model {
    public String name;
  }
}
