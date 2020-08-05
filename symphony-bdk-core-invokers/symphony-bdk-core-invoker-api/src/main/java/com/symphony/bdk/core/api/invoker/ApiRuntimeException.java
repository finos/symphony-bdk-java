package com.symphony.bdk.core.api.invoker;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Getter
public class ApiRuntimeException extends RuntimeException {

  private final int code;
  private final Map<String, List<String>> responseHeaders;
  private final String responseBody;

  public ApiRuntimeException(ApiException source) {
    super(source);
    this.code = source.getCode();
    this.responseHeaders = source.getResponseHeaders();
    this.responseBody = source.getResponseBody();
  }
}
