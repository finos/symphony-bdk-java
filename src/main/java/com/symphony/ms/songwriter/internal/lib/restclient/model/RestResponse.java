package com.symphony.ms.songwriter.internal.lib.restclient.model;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestResponse<T> {

  private int status;

  private Map<String, String> headers;

  private T body;

  public RestResponse(Map<String, String> headers, int status) {
    this.headers = headers;
    this.status = status;
  }

  public RestResponse(T body, Map<String, String> headers, int status) {
    this.headers = headers;
    this.status = status;
    this.body = body;
  }

}
