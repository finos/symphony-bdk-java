package com.symphony.bdk.core.api.invoker;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

/**
 * Runtime version of the {@link ApiException}.
 */
@Getter
@API(status = API.Status.EXPERIMENTAL)
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
