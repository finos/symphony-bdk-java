package com.symphony.ms.songwriter.internal.lib.restclient;

import java.util.Map;
import com.symphony.ms.songwriter.internal.lib.restclient.model.RestResponse;

/**
 * Interface which abstracts the underlying REST client library
 *
 * @author Marcus Secato
 *
 */
public interface RestClient {

  /**
   * Perform a get request
   * @param url destination url
   * @param clazz the return type
   * @return T
   */
  <T> RestResponse<T> getRequest(String url, Class<T> clazz);

  /**
   * Perform a get request
   * @param url destination url
   * @param headers header parameters
   * @param clazz the return type
   * @return T
   */
  <T> RestResponse<T> getRequest(String url, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a post request
   * @param url destination url
   * @param body entity body
   * @param clazz the return type
   * @return T
   */
  <T, U> RestResponse<T> postRequest(String url, U body, Class<T> clazz);

  /**
   * Perform a post request
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @param clazz the return type
   * @return T
   */
  <T, U> RestResponse<T> postRequest(String url, U body, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a put request
   * @param url destination url
   * @param body entity body
   * @param clazz the return type
   * @return T
   */
  <T, U> RestResponse<T> putRequest(String url, U body, Class<T> clazz);

  /**
   * Perform a put request
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @param clazz the return type
   * @return T
   */
  <T, U> RestResponse<T> putRequest(String url, U body, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a delete request
   * @param url destination url
   * @param clazz the return type
   * @return T
   */
  <T> RestResponse<T> deleteRequest(String url, Class<T> clazz);

  /**
   * Perform a delete request
   * @param url destination url
   * @param headers header parameters
   * @param clazz the return type
   * @return T
   */
  <T> RestResponse<T> deleteRequest(String url, Map<String, String> headers, Class<T> clazz);

}
