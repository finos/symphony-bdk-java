package com.symphony.ms.songwriter.internal.lib.restclient;

import java.util.Map;
import com.symphony.ms.songwriter.internal.lib.restclient.model.RestResponse;

public interface RestClient {

  /**
   * Perform a get request
   * @param url destination url
   * @return T
   */
  <T> RestResponse<T> getRequest(String url);

  /**
   * Perform a get request
   * @param url destination url
   * @param headers header parameters
   * @return T
   */
  <T> RestResponse<T> getRequest(String url, Map<String, String> headers);

  /**
   * Perform a post request
   * @param url destination url
   * @param body entity body
   * @return T
   */
  <T, U> RestResponse<T> postRequest(String url, U body);

  /**
   * Perform a post request
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @return T
   */
  <T, U> RestResponse<T> postRequest(String url, U body, Map<String, String> headers);

  /**
   * Perform a put request
   * @param url destination url
   * @param body entity body
   * @return T
   */
  <T, U> RestResponse<T> putRequest(String url, U body);

  /**
   * Perform a put request
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @return T
   */
  <T, U> RestResponse<T> putRequest(String url, U body, Map<String, String> headers);

  /**
   * Perform a delete request
   * @param url destination url
   * @return T
   */
  <T> RestResponse<T> deleteRequest(String url);

  /**
   * Perform a delete request
   * @param url destination url
   * @param headers header parameters
   * @return T
   */
  <T> RestResponse<T> deleteRequest(String url, Map<String, String> headers);

}
