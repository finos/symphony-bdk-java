package com.symphony.bdk.core.api.invoker.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiResponse;
import com.symphony.bdk.core.api.invoker.Pair;

import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

/**
 * Dummy {@link ApiClient} implementation returned by default by the {@link com.symphony.bdk.core.api.invoker.Configuration} class.
 */
@API(status = API.Status.INTERNAL)
public class ApiClientNotImplemented implements ApiClient {

  private static final String MESSAGE = "You must provide an real implementation of the ApiClient interface.";

  @Override
  public <T> ApiResponse<T> invokeAPI(String path, String method, List<Pair> queryParams, Object body,
      Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept,
      String contentType, String[] authNames, GenericType<T> returnType) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String getBasePath() {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient setBasePath(String basePath) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient setUserAgent(String userAgent) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient addDefaultHeader(String key, String value) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String getTempFolderPath() {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient setTempFolderPath(String tempFolderPath) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public int getConnectTimeout() {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient setConnectTimeout(int connectionTimeout) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public int getReadTimeout() {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public ApiClient setReadTimeout(int readTimeout) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String parameterToString(Object param) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String selectHeaderAccept(String[] accepts) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String selectHeaderContentType(String[] contentTypes) {
    throw new UnsupportedOperationException(MESSAGE);
  }

  @Override
  public String escapeString(String str) {
    throw new UnsupportedOperationException(MESSAGE);
  }
}
