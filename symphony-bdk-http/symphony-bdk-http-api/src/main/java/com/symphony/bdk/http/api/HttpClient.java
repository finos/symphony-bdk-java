package com.symphony.bdk.http.api;

import com.symphony.bdk.http.api.util.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic Restful Client built on top of the {@link ApiClient}.
 *
 * <p>Usage example:
 * <pre>{@code
 *   final HttpClient httpClient = HttpClient.builder(() -> new ApiClientBuilder())
 *       .basePath("https://localhost:8080")
 *       .header("Connection", "Keep-Alive")
 *       .header("Keep-Alive", "timeout=5, max=1000")
 *       .cookie("foo", "bar")
 *       .build();
 *
 *   final String response = httpClient.path("/api/v1/users")
 *       .header("Authorization", "Bearer AbCdEf123456")
 *       .get(new TypeReference<String>() {});
 * }</pre>
 */
public class HttpClient {

  private final ApiClient apiClient;
  private final RequestConfig requestConfig;

  private HttpClient(ApiClient apiClient, RequestConfig requestConfig) {
    this.apiClient = apiClient;
    this.requestConfig = requestConfig;
  }

  /**
   * Build a request and execute it using an arbitrary request method name.
   *
   * @param method request method name.
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T method(String method, TypeReference<T> type) throws ApiException {

    final ApiResponse<T> response = this.apiClient.invokeAPI(
        this.requestConfig.getPath(),
        method,
        this.requestConfig.getQueryParams(),
        this.requestConfig.getBody(),
        this.requestConfig.getHeaders(),
        this.requestConfig.getCookies(),
        this.requestConfig.getFormParams(),
        this.requestConfig.getAccept(),
        this.requestConfig.getContentType() == null ? "application/json" : this.requestConfig.getContentType(),
        new String[] {},
        type
    );

    return response != null ? response.getData() : null;
  }

  /**
   * Build a get request and execute it.
   *
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity.
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T get(TypeReference<T> type) throws ApiException {
    return this.method("GET", type);
  }

  /**
   * Build a post request and execute it.
   *
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity.
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T post(TypeReference<T> type) throws ApiException {
    return this.method("POST", type);
  }

  /**
   * Build a put request and execute it.
   *
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity.
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T put(TypeReference<T> type) throws ApiException {
    return this.method("PUT", type);
  }

  /**
   * Build a patch request and execute it.
   *
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity.
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T patch(TypeReference<T> type) throws ApiException {
    return this.method("PATCH", type);
  }

  /**
   * Build a delete request and execute it.
   *
   * @param type the {@link TypeReference} object representing a generic Java type the response should convert to.
   * @param <T> generic response type.
   * @return Response entity.
   * @throws ApiException if there are problems with the Api client request.
   */
  public <T> T delete(TypeReference<T> type) throws ApiException {
    return this.method("DELETE", type);
  }

  /**
   * Build an {@link Builder} from an {@link ApiClientBuilderProvider}
   *
   * @param provider {@link ApiClientBuilderProvider} to instantiate the {@link ApiClientBuilder} to build a {@link ApiClient}.
   * @return builder to build an {@link ApiClient} and execute client requests.
   */
  public static Builder builder(ApiClientBuilderProvider provider) {
    return new Builder(provider);
  }

  /**
   * Set request path.
   *
   * @param path the name of the header.
   * @return the updated instance.
   */
  public HttpClient path(String path) {
    return new HttpClient(this.apiClient, this.requestConfig.withPath(path));
  }

  /**
   * Add an arbitrary header.
   *
   * @param key the name of the header.
   * @param value the value of the header.
   * @return the updated instance.
   */
  public HttpClient header(String key, String value) {
    return new HttpClient(this.apiClient, this.requestConfig.withHeaders(this.requestConfig.appendHeader(key, value)));
  }

  /**
   * Add a cookie to be set.
   *
   * @param key the name of the cookie.
   * @param value the value of the cookie.
   * @return the updated instance.
   */
  public HttpClient cookie(String key, String value) {
    return new HttpClient(this.apiClient, this.requestConfig.withCookies(this.requestConfig.appendCookie(key, value)));
  }

  /**
   * Add a query parameter.
   *
   * @param key the name of the parameter.
   * @param value the value of the parameter.
   * @return the updated instance.
   */
  public HttpClient queryParam(String key, String value) {
    return new HttpClient(
        this.apiClient,
        this.requestConfig.withQueryParams(this.requestConfig.appendQueryParam(key, value))
    );
  }

  /**
   * Add a form parameter.
   *
   * @param key the name of the parameter.
   * @param value the value of the parameter.
   * @return the updated instance.
   */
  public HttpClient formParam(String key, Object value) {
    return new HttpClient(
        this.apiClient,
        this.requestConfig.withFormParams(this.requestConfig.appendFormParam(key, value))
    );
  }

  /**
   * Add the request body object.
   *
   * @param body the body of the request.
   * @return the updated instance.
   */
  public HttpClient body(Object body) {
    return new HttpClient(this.apiClient, this.requestConfig.withBody(body));
  }

  /**
   * Add the accepted response media type.
   *
   * @param accept accepted response media type.
   * @return the updated instance.
   */
  public HttpClient accept(String accept) {
    return new HttpClient(this.apiClient, this.requestConfig.withAccept(accept));
  }

  /**
   * Add the request's Content-Type header.
   *
   * @param contentType the request's Content-Type header.
   * @return the updated instance.
   */
  public HttpClient contentType(String contentType) {
    return new HttpClient(this.apiClient, this.requestConfig.withContentType(contentType));
  }

  /**
   * The {@link HttpClient} fluent builder.
   */
  @RequiredArgsConstructor
  public static class Builder {

    private final ApiClientBuilderProvider provider;

    // base path
    private String basePath = "";
    // keystore
    private byte[] keyStore = null;
    private String keyStorePassword = "";
    // truststore
    private byte[] trustStore = null;
    private String trustStorePassword = "";
    // proxy
    private String proxyHost = null;
    private int proxyPort = -1;
    private String proxyUser = null;
    private String proxyPassword = null;
    // common headers and cookies
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();

    /**
     * Add base path of the web resource target.
     * @param basePath a base path of the web resource target.
     * @return the updated builder.
     */
    public Builder basePath(String basePath) {
      this.basePath = basePath;
      return this;
    }

    /**
     * Add an arbitrary common header.
     * @param key the name of the header.
     * @param value the value of the header.
     * @return the updated builder.
     */
    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    /**
     * Add an arbitrary common cookie.
     * @param key the name of the header.
     * @param value the value of the header.
     * @return the updated builder.
     */
    public Builder cookie(String key, String value) {
      this.cookies.put(key, value);
      return this;
    }

    /**
     * Add the Java key store to the Http client.
     *
     * @param keyStore the Java key store.
     * @param keyStorePassword the key store password.
     * @return the updated builder.
     */
    public Builder keyStore(byte[] keyStore, String keyStorePassword) {
      this.keyStore = keyStore;
      this.keyStorePassword = keyStorePassword;
      return this;
    }

    /**
     * Add the Java trust store to the Http client.
     *
     * @param trustStore the Java trust store.
     * @param trustStorePassword the trust store password.
     * @return the updated builder.
     */
    public Builder trustStore(byte[] trustStore, String trustStorePassword) {
      this.trustStore = trustStore;
      this.trustStorePassword = trustStorePassword;
      return this;
    }

    /**
     * Configure proxy host and port.
     *
     * @param proxyHost the proxy host name.
     * @param proxyPort the proxy port number.
     * @return the updated builder.
     */
    public Builder proxy(String proxyHost, int proxyPort) {
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      return this;
    }

    /**
     * Configure proxy credentials.
     *
     * @param proxyUser proxy username.
     * @param proxyPassword proxy password.
     * @return the updated builder.
     */
    public Builder proxyCredentials(String proxyUser, String proxyPassword) {
      this.proxyUser = proxyUser;
      this.proxyPassword = proxyPassword;
      return this;
    }

    public HttpClient build() {

      final ApiClientBuilder builder = this.provider.newInstance();

      if (this.basePath != null) {
        builder.withBasePath(this.basePath);
      }
      if (this.keyStore != null) {
        builder.withKeyStore(this.keyStore, this.keyStorePassword);
      }
      if (this.trustStore != null) {
        builder.withTrustStore(this.trustStore, this.trustStorePassword);
      }
      if (this.proxyHost != null) {
        builder.withProxy(this.proxyHost, this.proxyPort);
      }
      if (this.proxyUser != null && proxyPassword != null) {
        builder.withProxyCredentials(this.proxyUser, this.proxyPassword);
      }

      return new HttpClient(
          builder.build(),
          new RequestConfig()
              .withHeaders(this.headers)
              .withCookies(this.cookies)
      );
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  private static class RequestConfig {

    @With private Map<String, String> headers;
    @With private Map<String, String> cookies;
    @With private Map<String, Object> formParams;
    @With private List<Pair> queryParams;

    @With private String path;
    @With private Object body;
    @With private String accept;
    @With private String contentType;

    public Map<String, String> appendHeader(String key, String value) {
      this.headers.put(key, value);
      return this.headers;
    }

    public Map<String, String> appendCookie(String key, String value) {
      this.cookies.put(key, value);
      return this.cookies;
    }

    public List<Pair> appendQueryParam(String key, String value) {
      this.queryParams.add(new Pair(key, value));
      return this.queryParams;
    }

    public Map<String, Object> appendFormParam(String key, Object value) {
      this.formParams.put(key, value);
      return this.formParams;
    }
  }
}
