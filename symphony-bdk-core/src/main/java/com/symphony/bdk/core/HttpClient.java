package com.symphony.bdk.core;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.core.util.ProviderLoader;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

@Slf4j
public class HttpClient {

  public static Builder builder() {
    return new Builder();
  }

  protected HttpClient() {

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
    private String keyStorePath = "";
    private String keyStorePassword = "";
    private String trustStorePath = "";
    private String trustStorePassword = "";

    protected Builder() {
      this.provider = ProviderLoader.findApiClientBuilderProvider();
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

    public Builder keyStore(String keyStorePath, String keyStorePassword) {
      this.keyStorePath = keyStorePath;
      this.keyStorePassword = keyStorePassword;
      return this;
    }

    public Builder trustStore(String trustStorePath, String trustStorePassword) {
      this.trustStorePath = trustStorePath;
      this.trustStorePassword = trustStorePassword;
      return this;
    }

    protected Builder apiClientBuilderProvider(ApiClientBuilderProvider provider) {
      this.provider = provider;
      return this;
    }

    public <T> T method(String method, GenericType<T> type) throws ApiException, IOException {
      ApiClientBuilder builder = provider.newInstance();
      builder.withBasePath(this.basePath);

      if (!"".equals(this.keyStorePath)) {
          builder.withKeyStore(Files.readAllBytes(new File(this.keyStorePath).toPath()), this.keyStorePassword);
      }
      if (!"".equals(this.trustStorePath)) {
        builder.withTrustStore(Files.readAllBytes(new File(this.trustStorePath).toPath()), this.trustStorePassword);
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

    public <T> T get(GenericType<T> type) throws ApiException, IOException {
      return this.method("GET", type);
    }

    public <T> T post(GenericType<T> type) throws ApiException, IOException {
      return this.method("POST", type);
    }

    public <T> T delete(GenericType<T> type) throws ApiException, IOException {
      return this.method("DELETE", type);
    }
  }
}
