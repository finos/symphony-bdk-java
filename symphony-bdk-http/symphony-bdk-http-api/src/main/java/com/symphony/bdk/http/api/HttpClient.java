package com.symphony.bdk.http.api;

import com.symphony.bdk.http.api.util.GenericClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

  public static Builder builder(ApiClientBuilderProvider provider) {
    return new Builder(provider);
  }

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

    protected Builder(ApiClientBuilderProvider provider) {
      this.provider = provider;
    }

    public Builder basePath(String basePath) {
      this.basePath = basePath;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    public Builder cookie(String key, String value) {
      this.cookies.put(key, value);
      return this;
    }

    public Builder queryParam(String key, String value) {
      this.queryParams.add(new Pair(key, value));
      return this;
    }

    public Builder formParams(String key, Object value) {
      this.formParams.put(key, value);
      return this;
    }

    public Builder body(Object body) {
      this.body = body;
      return this;
    }

    public Builder accept(String accept) {
      this.accept = accept;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder keyStore(byte[] keyStore, String keyStorePassword) {
      this.keyStore = keyStore;
      this.keyStorePassword = keyStorePassword;
      return this;
    }

    public Builder trustStore(byte[] trustStore, String trustStorePassword) {
      this.trustStore = trustStore;
      this.trustStorePassword = trustStorePassword;
      return this;
    }

    protected Builder apiClientBuilderProvider(ApiClientBuilderProvider provider) {
      this.provider = provider;
      return this;
    }

    public <T> T method(String method, GenericClass<T> type) throws ApiException {
      ApiClientBuilder builder = provider.newInstance();
      builder.withBasePath(this.basePath);

      if (this.keyStore != null) {
        builder.withKeyStore(this.keyStore, this.keyStorePassword);
      }
      if (this.trustStore != null) {
        builder.withTrustStore(this.trustStore, this.trustStorePassword);
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

    public <T> T get(GenericClass<T> type) throws ApiException {
      return this.method("GET", type);
    }

    public <T> T post(GenericClass<T> type) throws ApiException {
      return this.method("POST", type);
    }

    public <T> T delete(GenericClass<T> type) throws ApiException {
      return this.method("DELETE", type);
    }
  }
}
