package com.symphony.bdk.http.api;

import com.symphony.bdk.http.api.util.TypeReference;

import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

/**
 * Interface used to perform HTTP requests performed by the generated Swagger code.
 */
@API(status = API.Status.STABLE)
public interface ApiClient {

  /**
   * Invoke API by sending HTTP request with the given options.
   *
   * @param <T> Type
   * @param path The sub-path of the HTTP URL
   * @param method The request method, one of "GET", "POST", "PUT", "HEAD" and "DELETE"
   * @param queryParams The query parameters
   * @param body The request body object
   * @param headerParams The header parameters
   * @param cookieParams The cookie parameters
   * @param formParams The form parameters
   * @param accept The request's Accept header
   * @param contentType The request's Content-Type header
   * @param authNames The authentications to apply
   * @param returnType The return type into which to deserialize the response
   * @return The response body in type of string
   * @throws ApiException API exception
   */
  <T> ApiResponse<T> invokeAPI(
      String path,
      String method,
      List<Pair> queryParams,
      Object body,
      Map<String, String> headerParams,
      Map<String, String> cookieParams,
      Map<String, Object> formParams,
      String accept,
      String contentType,
      String[] authNames,
      TypeReference<T> returnType
  ) throws ApiException;

  /**
   * Returns the API base path
   *
   * @return API base path
   */
  String getBasePath();

  /**
   * Format the given parameter object into string.
   *
   * @param param Object
   * @return Object in string format
   */
  String parameterToString(Object param);

  /**
   * Format to {@code Pair} objects.
   *
   * @param collectionFormat Collection format
   * @param name Name
   * @param value Value
   * @return List of pairs
   */
  List<Pair> parameterToPairs(String collectionFormat, String name, Object value);

  /**
   * Select the Accept header's value from the given accepts array: if JSON exists in the given
   * array, use it; otherwise use all of them (joining into a string)
   *
   * @param accepts The accepts array to select from
   * @return The Accept header to use. If the given array is empty, null will be returned (not to
   * set the Accept header explicitly).
   */
  String selectHeaderAccept(String... accepts);

  /**
   * Select the Content-Type header's value from the given array: if JSON exists in the given array,
   * use it; otherwise use the first one of the array.
   *
   * @param contentTypes The Content-Type array to select from
   * @return The Content-Type header to use. If the given array is empty, JSON will be used.
   */
  String selectHeaderContentType(String... contentTypes);

  /**
   * Escape the given string to be used as URL query value.
   *
   * @param str String
   * @return Escaped string
   */
  String escapeString(String str);
}
