package com.symphony.bdk.http.api;

import com.symphony.bdk.http.api.util.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpClient is the main entry point to fluent API used to build an {@link ApiClient} and execute
 * client requests in order to consume responses returned
 */
public class HttpClient {

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
   * The HttpClient Fluent API builder.
   */
  public static class Builder {
    private ApiClientBuilderProvider provider;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private final Map<String, Object> formParams = new HashMap<>();
    private final List<Pair> queryParams = new ArrayList<>();
    private String basePath = "";
    private String path = "";
    private Object body;
    private String accept = "";
    private String contentType = "";
    private byte[] keyStore = null;
    private String keyStorePassword = "";
    private byte[] trustStore = null;
    private String trustStorePassword = "";
    private String proxyHost = null;
    private int proxyPort = -1;
    private String proxyUser = null;
    private String proxyPassword = null;

    protected Builder(ApiClientBuilderProvider provider) {
      this.provider = provider;
    }

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
     * Add the sub-path of the HTTP URL.
     * @param path The sub-path of the HTTP URL.
     * @return the updated builder.
     */
    public Builder path(String path) {
      this.path = path;
      return this;
    }

    /**
     * Add an arbitrary header.
     * @param key the name of the header.
     * @param value the value of the header.
     * @return the updated builder.
     */
    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    /**
     * Add a cookie to be set.
     *
     * @param key the name of the cookie.
     * @param value the value of the cookie.
     * @return the updated builder.
     */
    public Builder cookie(String key, String value) {
      this.cookies.put(key, value);
      return this;
    }

    /**
     * Add the query parameter.
     *
     * @param key the name of the parameter.
     * @param value the value of the parameter.
     * @return the updated builder.
     */
    public Builder queryParam(String key, String value) {
      this.queryParams.add(new Pair(key, value));
      return this;
    }

    /**
     * Add the form parameter.
     *
     * @param key the name of the parameter.
     * @param value the value of the parameter.
     * @return the updated builder.
     */
    public Builder formParams(String key, Object value) {
      this.formParams.put(key, value);
      return this;
    }

    /**
     * Add the request body object.
     *
     * @param body the body of the request.
     * @return the updated builder.
     */
    public Builder body(Object body) {
      this.body = body;
      return this;
    }

    /**
     * Add the accepted response media type.
     *
     * @param accept accepted response media type.
     * @return the updated builder.
     */
    public Builder accept(String accept) {
      this.accept = accept;
      return this;
    }

    /**
     * Add the request's Content-Type header.
     *
     * @param contentType the request's Content-Type header.
     * @return the updated builder.
     */
    public Builder contentType(String contentType) {
      this.contentType = contentType;
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

    public Builder proxy(String proxyHost, int proxyPort) {
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      return this;
    }

    public Builder proxyCredentials(String proxyUser, String proxyPassword) {
      this.proxyUser = proxyUser;
      this.proxyPassword = proxyPassword;
      return this;
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
      ApiClientBuilder builder = provider.newInstance();
      builder.withBasePath(this.basePath);

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

      if (contentType.equals("")) {
        contentType = "application/json";
      }
      ApiClient apiClient = builder.build();
      ApiResponse<T> apiResponse = apiClient
          .invokeAPI(this.path, method, this.queryParams, this.body, this.headers, this.cookies, this.formParams,
              this.accept, this.contentType, new String[] {}, type);
      return apiResponse.getData();
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
  }
}
