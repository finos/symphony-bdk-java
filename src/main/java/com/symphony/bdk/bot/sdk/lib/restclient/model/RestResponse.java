package com.symphony.bdk.bot.sdk.lib.restclient.model;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The RestClient response
 *
 * @author Marcus Secato
 *
 * @param <T> the response body type
 */
@Data
@NoArgsConstructor
public class RestResponse<T> {

  private int status;

  private Map<String, String> headers;

  private T body;

  /**
   * Rest response
   * @param headers the HTTP headers
   * @param status the HTTP status
   */
  public RestResponse(Map<String, String> headers, int status) {
    this.headers = headers;
    this.status = status;
  }

  /**
   * Rest response
   * 
   * @param body the response body
   * @param headers the HTTP headers
   * @param status the HTTP status
   */
  public RestResponse(T body, Map<String, String> headers, int status) {
    this.headers = headers;
    this.status = status;
    this.body = body;
  }

}
