package com.symphony.bdk.bot.sdk.lib.restclient;

import java.util.Map;

import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;

/**
 * Interface which abstracts the underlying REST client library
 *
 * @author Marcus Secato
 *
 */
public interface RestClient {

  /**
   * Perform a get request
   * @param <T> the response type
   * @param url destination url
   * @param clazz the return type
   * @return T the rest response
   */
  <T> RestResponse<T> getRequest(String url, Class<T> clazz);

  /**
   * Perform a get request
   * @param <T> the response type
   * @param url destination url
   * @param headers header parameters
   * @param clazz the return type
   * @return T the rest response
   */
  <T> RestResponse<T> getRequest(String url, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a post request
   * @param <T> the response type
   * @param <U> the body payload type
   * @param url destination url
   * @param body entity body
   * @param clazz the return type
   * @return T the rest response
   */
  <T, U> RestResponse<T> postRequest(String url, U body, Class<T> clazz);

  /**
   * Perform a post request
   * @param <T> the response type
   * @param <U> the body payload type
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @param clazz the return type
   * @return T the rest response
   */
  <T, U> RestResponse<T> postRequest(String url, U body, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a put request
   * @param <T> the response type
   * @param <U> the body payload type
   * @param url destination url
   * @param body entity body
   * @param clazz the return type
   * @return T the rest response
   */
  <T, U> RestResponse<T> putRequest(String url, U body, Class<T> clazz);

  /**
   * Perform a put request
   * @param <T> the response type
   * @param <U> the body payload type
   * @param url destination url
   * @param body entity body
   * @param headers header parameters
   * @param clazz the return type
   * @return T the rest response
   */
  <T, U> RestResponse<T> putRequest(String url, U body, Map<String, String> headers, Class<T> clazz);

  /**
   * Perform a delete request
   * @param <T> the response type
   * @param url destination url
   * @param clazz the return type
   * @return T the rest response
   */
  <T> RestResponse<T> deleteRequest(String url, Class<T> clazz);

  /**
   * Perform a delete request
   * @param <T> the response type
   * @param url destination url
   * @param headers header parameters
   * @param clazz the return type
   * @return T the rest response
   */
  <T> RestResponse<T> deleteRequest(String url, Map<String, String> headers, Class<T> clazz);

}
