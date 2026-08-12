package com.symphony.bdk.http.jdk;

import static com.symphony.bdk.http.api.util.ApiUtils.isCollectionOfFiles;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBodyPart;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.auth.Authentication;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;
import com.symphony.bdk.http.api.util.TypeReference;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.core.JacksonException;

/**
 * {@code java.net.http.HttpClient}-based implementation for the {@link ApiClient} interface called by generated
 * code. Mirrors {@code com.symphony.bdk.http.jersey2.ApiClientJersey2}'s behavior wherever the shared {@link
 * ApiClient} contract requires it.
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientJdk implements ApiClient {

  private static final Logger log = LoggerFactory.getLogger("com.symphony.bdk.requests.outgoing");

  protected static final String MULTIPART_FORM_DATA = "multipart/form-data";
  protected static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

  protected final HttpClient httpClient;
  protected final String basePath;
  protected final Map<String, String> defaultHeaderMap;
  protected final String tempFolderPath;
  protected final Duration readTimeout;
  protected final List<Function<HttpRequest.Builder, HttpRequest.Builder>> filters;
  protected final JSON json;
  protected Map<String, Authentication> authentications;
  protected List<String> enforcedAuthenticationSchemes;

  public ApiClientJdk(
      final HttpClient httpClient,
      String basePath,
      Map<String, String> defaultHeaders,
      String temporaryFolderPath,
      Duration readTimeout,
      List<Function<HttpRequest.Builder, HttpRequest.Builder>> filters
  ) {
    this.httpClient = httpClient;
    this.basePath = basePath;
    this.defaultHeaderMap = new HashMap<>(defaultHeaders);
    this.tempFolderPath = temporaryFolderPath;
    this.readTimeout = readTimeout;
    this.filters = filters;
    this.json = new JSON();
    this.authentications = new HashMap<>();
    this.enforcedAuthenticationSchemes = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> ApiResponse<T> invokeAPI(
      final String path,
      final String method,
      final List<Pair> queryParams,
      final Object body,
      final Map<String, String> headerParams,
      final Map<String, String> cookieParams,
      final Map<String, Object> formParams,
      final String accept,
      final String contentType,
      final String[] authNames,
      final TypeReference<T> returnType
  ) throws ApiException {

    this.updateParamsForAuth(authNames, headerParams);

    boolean clearTraceId = false;
    if (!DistributedTracingContext.hasTraceId()) {
      DistributedTracingContext.setTraceId();
      clearTraceId = true;
    }

    try {
      HttpRequest request =
          this.buildRequest(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
              contentType);

      if (returnType != null && returnType.getType() == File.class) {
        return this.invokeForFileDownload(request);
      } else {
        return this.invokeForBytes(request, returnType);
      }
    } finally {
      if (clearTraceId) {
        DistributedTracingContext.clear();
      }
    }
  }

  private HttpRequest buildRequest(
      String path,
      String method,
      List<Pair> queryParams,
      Object body,
      Map<String, String> headerParams,
      Map<String, String> cookieParams,
      Map<String, Object> formParams,
      String accept,
      String contentType
  ) throws ApiException {

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(this.buildUri(path, queryParams));

    if (accept != null && !accept.isEmpty()) {
      requestBuilder.header("Accept", accept);
    }

    requestBuilder.header(DistributedTracingContext.TRACE_ID, DistributedTracingContext.getTraceId());

    if (headerParams != null) {
      for (Map.Entry<String, String> entry : headerParams.entrySet()) {
        if (entry.getValue() != null) {
          requestBuilder.header(entry.getKey(), entry.getValue());
        }
      }
    }

    if (cookieParams != null && !cookieParams.isEmpty()) {
      String cookieHeader = cookieParams.entrySet().stream()
          .filter(entry -> entry.getValue() != null)
          .map(entry -> entry.getKey() + "=" + entry.getValue())
          .collect(Collectors.joining("; "));
      if (!cookieHeader.isEmpty()) {
        requestBuilder.header("Cookie", cookieHeader);
      }
    }

    for (Map.Entry<String, String> entry : this.defaultHeaderMap.entrySet()) {
      if (headerParams == null || !headerParams.containsKey(entry.getKey())) {
        if (entry.getValue() != null) {
          requestBuilder.header(entry.getKey(), entry.getValue());
        }
      }
    }

    Body serializedBody = this.serialize(body, formParams, contentType);
    if (serializedBody.contentType != null && !serializedBody.contentType.isEmpty()) {
      requestBuilder.header("Content-Type", serializedBody.contentType);
    }
    requestBuilder.method(method, serializedBody.publisher);

    if (this.readTimeout != null) {
      requestBuilder.timeout(this.readTimeout);
    }

    for (Function<HttpRequest.Builder, HttpRequest.Builder> filter : this.filters) {
      requestBuilder = filter.apply(requestBuilder);
    }

    return requestBuilder.build();
  }

  private <T> ApiResponse<T> invokeForBytes(HttpRequest request, TypeReference<T> returnType) throws ApiException {
    HttpResponse<byte[]> response = this.send(request, HttpResponse.BodyHandlers.ofByteArray());

    int statusCode = response.statusCode();
    Map<String, List<String>> responseHeaders = response.headers().map();

    if (statusCode == 204) {
      return new ApiResponse<>(statusCode, responseHeaders);
    } else if (statusCode / 100 == 2) {
      if (returnType == null) {
        return new ApiResponse<>(statusCode, responseHeaders);
      }
      return new ApiResponse<>(statusCode, responseHeaders, this.deserialize(response.body(), returnType));
    } else {
      String message = new String(response.body(), StandardCharsets.UTF_8);
      throw new ApiException(statusCode, message, responseHeaders, message);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> ApiResponse<T> invokeForFileDownload(HttpRequest request) throws ApiException {
    HttpResponse<Path> response = this.send(request, responseInfo -> {
      try {
        return HttpResponse.BodySubscribers.ofFile(this.prepareDownloadFile(responseInfo.headers()).toPath());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    int statusCode = response.statusCode();
    Map<String, List<String>> responseHeaders = response.headers().map();

    if (statusCode == 204) {
      return new ApiResponse<>(statusCode, responseHeaders);
    } else if (statusCode / 100 == 2) {
      return new ApiResponse<>(statusCode, responseHeaders, (T) response.body().toFile());
    } else {
      String message;
      try {
        message = new String(Files.readAllBytes(response.body()), StandardCharsets.UTF_8);
      } catch (IOException e) {
        message = "error";
      }
      throw new ApiException(statusCode, message, responseHeaders, message);
    }
  }

  private <U> HttpResponse<U> send(HttpRequest request, HttpResponse.BodyHandler<U> bodyHandler)
      throws ApiException {
    long startTime = System.currentTimeMillis();
    try {
      HttpResponse<U> response = this.httpClient.send(request, bodyHandler);
      if (log.isDebugEnabled()) {
        long elapsed = System.currentTimeMillis() - startTime;
        log.debug("status={}, url={}, time={}", response.statusCode(), request.uri(), elapsed);
      }
      return response;
    } catch (IOException e) {
      throw this.translateAndWrap(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  /**
   * Translates transport-level {@link IOException}s thrown by {@link HttpClient#send} into the root-cause types
   * {@code RetryWithRecoveryBuilder#isNetworkIssueOrMinorError} already recognizes ({@link ConnectException},
   * {@link SocketTimeoutException}), then wraps the result as an {@link UncheckedIOException} since {@link
   * ApiClient#invokeAPI} does not declare {@link IOException} in its {@code throws} clause.
   *
   * <p>Note: {@link HttpConnectTimeoutException} and {@link HttpTimeoutException} both extend {@link
   * java.io.IOException} directly (not {@link ConnectException}/{@link SocketTimeoutException}), so an explicit
   * translation is required here rather than relying on inheritance.</p>
   */
  UncheckedIOException translateAndWrap(IOException e) {
    if (e instanceof HttpConnectTimeoutException) {
      ConnectException translated = new ConnectException(e.getMessage());
      translated.initCause(e);
      return new UncheckedIOException(translated);
    } else if (e instanceof HttpTimeoutException) {
      SocketTimeoutException translated = new SocketTimeoutException(e.getMessage());
      translated.initCause(e);
      return new UncheckedIOException(translated);
    } else {
      return new UncheckedIOException(e);
    }
  }

  @Override
  public String getBasePath() {
    return this.basePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String parameterToString(Object param) {
    if (param == null) {
      return "";
    } else if (param instanceof Collection) {
      StringBuilder b = new StringBuilder();
      for (Object o : (Collection<?>) param) {
        if (b.length() > 0) {
          b.append(',');
        }
        b.append(o);
      }
      return b.toString();
    } else {
      return String.valueOf(param);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    List<Pair> params = new ArrayList<>();

    if (name == null || name.isEmpty() || value == null) {
      return params;
    }

    Collection<?> valueCollection;
    if (value instanceof Collection) {
      valueCollection = (Collection<?>) value;
    } else {
      params.add(new Pair(name, parameterToString(value)));
      return params;
    }

    if (valueCollection.isEmpty()) {
      return params;
    }

    String format = (collectionFormat == null || collectionFormat.isEmpty() ? "csv" : collectionFormat);

    if ("multi".equals(format)) {
      for (Object item : valueCollection) {
        params.add(new Pair(name, parameterToString(item)));
      }
      return params;
    }

    String delimiter;
    switch (format) {
      case "ssv":
        delimiter = " ";
        break;
      case "tsv":
        delimiter = "\t";
        break;
      case "pipes":
        delimiter = "|";
        break;
      default:
        delimiter = ",";
        break;
    }

    StringBuilder sb = new StringBuilder();
    for (Object item : valueCollection) {
      sb.append(delimiter);
      sb.append(parameterToString(item));
    }

    params.add(new Pair(name, sb.substring(1)));

    return params;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderAccept(String[] accepts) {
    if (accepts.length == 0) {
      return null;
    }
    for (String accept : accepts) {
      if (isJsonMime(accept)) {
        return accept;
      }
    }
    return String.join(",", accepts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderContentType(String[] contentTypes) {
    if (contentTypes.length == 0) {
      return "application/json";
    }
    for (String contentType : contentTypes) {
      if (isJsonMime(contentType)) {
        return contentType;
      }
    }
    return contentTypes[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String escapeString(String str) {
    return URLEncoder.encode(str, StandardCharsets.UTF_8).replace("+", "%20");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Authentication> getAuthentications() {
    return this.authentications;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEnforcedAuthenticationScheme(String name) {
    this.enforcedAuthenticationSchemes.add(name);
  }

  /**
   * Check if the given MIME is a JSON MIME.
   *
   * @param mime MIME
   * @return True if the MIME type is JSON
   */
  protected boolean isJsonMime(String mime) {
    String jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$";
    return mime != null && (mime.matches(jsonMime) || mime.equals("*/*"));
  }

  private URI buildUri(String path, List<Pair> queryParams) {
    StringBuilder urlBuilder = new StringBuilder(this.basePath).append(path);
    if (queryParams != null && !queryParams.isEmpty()) {
      String query = queryParams.stream()
          .filter(param -> param.getValue() != null)
          .map(param -> param.getName() + "=" + this.escapeString(param.getValue()))
          .collect(Collectors.joining("&"));
      if (!query.isEmpty()) {
        urlBuilder.append(path.contains("?") ? "&" : "?").append(query);
      }
    }
    return URI.create(urlBuilder.toString());
  }

  private Body serialize(Object body, Map<String, Object> formParams, String contentType) throws ApiException {
    if (contentType != null && contentType.startsWith(MULTIPART_FORM_DATA)) {
      String boundary = "----BdkJdkBoundary" + UUID.randomUUID();
      return new Body("multipart/form-data; boundary=" + boundary, this.buildMultipartBody(formParams, boundary));
    } else if (contentType != null && contentType.startsWith(APPLICATION_FORM_URLENCODED)) {
      return new Body(contentType, this.buildFormUrlEncodedBody(formParams));
    } else if (body != null) {
      return new Body(contentType, HttpRequest.BodyPublishers.ofByteArray(this.serializeToJson(body)));
    } else {
      return new Body(contentType, HttpRequest.BodyPublishers.noBody());
    }
  }

  private byte[] serializeToJson(Object body) throws ApiException {
    try {
      if (body instanceof String) {
        return ((String) body).getBytes(StandardCharsets.UTF_8);
      }
      return this.json.getMapper().writeValueAsBytes(body);
    } catch (JacksonException e) {
      throw new ApiException("Unable to serialize request body", e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T deserialize(byte[] responseBody, TypeReference<T> returnType) throws ApiException {
    if (returnType == null) {
      return null;
    }
    if (returnType.getType() == byte[].class) {
      return (T) responseBody;
    }
    try {
      return this.json.getMapper()
          .readValue(responseBody, this.json.getMapper().getTypeFactory().constructType(returnType.getType()));
    } catch (JacksonException e) {
      throw new ApiException("Unable to deserialize response body", e);
    }
  }

  private HttpRequest.BodyPublisher buildFormUrlEncodedBody(Map<String, Object> formParams) {
    if (formParams == null || formParams.isEmpty()) {
      return HttpRequest.BodyPublishers.noBody();
    }
    String encoded = formParams.entrySet().stream()
        .map(entry -> this.escapeString(entry.getKey()) + "=" + this.escapeString(
            this.parameterToString(entry.getValue())))
        .collect(Collectors.joining("&"));
    return HttpRequest.BodyPublishers.ofString(encoded, StandardCharsets.UTF_8);
  }

  private static final String CRLF = "\r\n";

  private HttpRequest.BodyPublisher buildMultipartBody(Map<String, Object> formParams, String boundary)
      throws ApiException {
    List<HttpRequest.BodyPublisher> publishers = new ArrayList<>();

    if (formParams != null) {
      for (Map.Entry<String, Object> param : formParams.entrySet()) {
        Object value = param.getValue();
        if (value instanceof File) {
          this.addFilePart(publishers, boundary, param.getKey(), (File) value);
        } else if (isCollectionOfFiles(value)) {
          for (Object file : (Collection<?>) value) {
            this.addFilePart(publishers, boundary, param.getKey(), (File) file);
          }
        } else if (value instanceof ApiClientBodyPart[]) {
          for (ApiClientBodyPart part : (ApiClientBodyPart[]) value) {
            this.addStreamPart(publishers, boundary, param.getKey(), part);
          }
        } else if (value instanceof ApiClientBodyPart) {
          this.addStreamPart(publishers, boundary, param.getKey(), (ApiClientBodyPart) value);
        } else {
          this.addFieldPart(publishers, boundary, param.getKey(), this.parameterToString(value));
        }
      }
    }

    publishers.add(HttpRequest.BodyPublishers.ofByteArray(
        ("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8)));

    return HttpRequest.BodyPublishers.concat(publishers.toArray(new HttpRequest.BodyPublisher[0]));
  }

  private void addFilePart(List<HttpRequest.BodyPublisher> publishers, String boundary, String key, File file)
      throws ApiException {
    String header = "--" + boundary + CRLF
        + "Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"" + CRLF
        + "Content-Type: application/octet-stream" + CRLF + CRLF;
    publishers.add(HttpRequest.BodyPublishers.ofByteArray(header.getBytes(StandardCharsets.UTF_8)));
    try {
      publishers.add(HttpRequest.BodyPublishers.ofFile(file.toPath()));
    } catch (java.io.FileNotFoundException e) {
      throw new ApiException("Unable to read file for multipart upload: " + file, e);
    }
    publishers.add(HttpRequest.BodyPublishers.ofByteArray(CRLF.getBytes(StandardCharsets.UTF_8)));
  }

  private void addStreamPart(List<HttpRequest.BodyPublisher> publishers, String boundary, String key,
      ApiClientBodyPart part) {
    String header = "--" + boundary + CRLF
        + "Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + part.getFilename() + "\"" + CRLF
        + "Content-Type: application/octet-stream" + CRLF + CRLF;
    publishers.add(HttpRequest.BodyPublishers.ofByteArray(header.getBytes(StandardCharsets.UTF_8)));
    publishers.add(HttpRequest.BodyPublishers.ofInputStream(part::getContent));
    publishers.add(HttpRequest.BodyPublishers.ofByteArray(CRLF.getBytes(StandardCharsets.UTF_8)));
  }

  private void addFieldPart(List<HttpRequest.BodyPublisher> publishers, String boundary, String key, String value) {
    String part = "--" + boundary + CRLF
        + "Content-Disposition: form-data; name=\"" + key + "\"" + CRLF + CRLF
        + value + CRLF;
    publishers.add(HttpRequest.BodyPublishers.ofByteArray(part.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Prepares the target {@link File} onto which a file-download response will be streamed, deriving the file
   * name from the {@code Content-Disposition} header when present.
   *
   * <p>Note: like {@code ApiClientJersey2#prepareDownloadFile}, the returned file name is a unique temp-file
   * name derived from (not identical to) the {@code Content-Disposition} filename, via {@link
   * File#createTempFile}.</p>
   */
  protected File prepareDownloadFile(java.net.http.HttpHeaders headers) throws IOException {
    String filename = null;
    String contentDisposition = headers.firstValue("Content-Disposition").orElse(null);
    if (contentDisposition != null && !contentDisposition.isEmpty()) {
      Pattern pattern = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?");
      Matcher matcher = pattern.matcher(contentDisposition);
      if (matcher.find()) {
        filename = matcher.group(1);
      }
    }

    String prefix;
    String suffix;
    if (filename == null) {
      prefix = "download-";
      suffix = "";
    } else {
      int pos = filename.lastIndexOf('.');
      if (pos == -1) {
        prefix = filename + "-";
        suffix = null;
      } else {
        prefix = filename.substring(0, pos) + "-";
        suffix = filename.substring(pos);
      }
      if (prefix.length() < 3) {
        prefix = "download-";
      }
    }

    if (this.tempFolderPath == null) {
      return File.createTempFile(prefix, suffix);
    } else {
      return File.createTempFile(prefix, suffix, new File(this.tempFolderPath));
    }
  }

  /**
   * Update query and header parameters based on authentication settings.
   *
   * @param authNames The authentications to apply
   */
  protected void updateParamsForAuth(String[] authNames, Map<String, String> headerParams) throws ApiException {
    if (authNames == null && this.enforcedAuthenticationSchemes.isEmpty()) {
      return;
    }

    authNames = withEnforcedSecurityScheme(authNames);

    for (String authName : authNames) {
      Authentication auth = this.authentications.get(authName);
      if (auth == null) {
        throw new RuntimeException("Authentication undefined: " + authName);
      }
      auth.apply(headerParams);
    }
  }

  private String[] withEnforcedSecurityScheme(String[] authNames) {
    if (authNames == null) {
      authNames = new String[0];
    }
    return Stream.concat(this.enforcedAuthenticationSchemes.stream(), Arrays.stream(authNames))
        .toArray(String[]::new);
  }

  private static final class Body {
    private final String contentType;
    private final HttpRequest.BodyPublisher publisher;

    private Body(String contentType, HttpRequest.BodyPublisher publisher) {
      this.contentType = contentType;
      this.publisher = publisher;
    }
  }
}
