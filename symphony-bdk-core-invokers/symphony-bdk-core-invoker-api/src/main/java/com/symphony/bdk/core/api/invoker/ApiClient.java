package com.symphony.bdk.core.api.invoker;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

/**
 * Interface used to perform HTTP requests performed by the generated Swagger code.
 */
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
      GenericType<T> returnType
  ) throws ApiException;

  /**
   * Returns the API base path
   * @return API base path
   */
  String getBasePath();

  /**
   * Set the API base path
   * @param basePath Base path
   * @return API client
   */
  ApiClient setBasePath(String basePath);

  /**
   * Set the User-Agent header's value (by adding to the default header map).
   * @param userAgent Http user agent
   * @return API client
   */
  ApiClient setUserAgent(String userAgent);

  /**
   * Add a default header.
   * @param key The header's key
   * @param value The header's value
   * @return API client
   */
  ApiClient addDefaultHeader(String key, String value);

  /**
   * The path of temporary folder used to store downloaded files from endpoints
   * with file response. The default value is <code>null</code>, i.e. using
   * the system's default tempopary folder.
   * @return Temp folder path
   */
  String getTempFolderPath();

  /**
   * Set temp folder path
   * @param tempFolderPath Temp folder path
   * @return API client
   */
  ApiClient setTempFolderPath(String tempFolderPath);

  /**
   * Connect timeout (in milliseconds).
   * @return Connection timeout
   */
  int getConnectTimeout();

  /**
   * Set the connect timeout (in milliseconds).
   * A value of 0 means no timeout, otherwise values must be between 1 and {@link Integer#MAX_VALUE}.
   * @param connectionTimeout Connection timeout in milliseconds
   * @return API client
   */
  ApiClient setConnectTimeout(int connectionTimeout);

  /**
   * read timeout (in milliseconds).
   * @return Read timeout
   */
  int getReadTimeout();

  /**
   * Set the read timeout (in milliseconds).
   * A value of 0 means no timeout, otherwise values must be between 1 and
   * {@link Integer#MAX_VALUE}.
   * @param readTimeout Read timeout in milliseconds
   * @return API client
   */
  ApiClient setReadTimeout(int readTimeout);

  /**
   * Format the given parameter object into string.
   * @param param Object
   * @return Object in string format
   */
  String parameterToString(Object param);

  /**
   * Format to {@code Pair} objects.
   * @param collectionFormat Collection format
   * @param name Name
   * @param value Value
   * @return List of pairs
   */
  List<Pair> parameterToPairs(String collectionFormat, String name, Object value);

  /**
   * Select the Accept header's value from the given accepts array:
   * if JSON exists in the given array, use it;
   * otherwise use all of them (joining into a string)
   * @param accepts The accepts array to select from
   * @return The Accept header to use. If the given array is empty,
   * null will be returned (not to set the Accept header explicitly).
   */
  String selectHeaderAccept(String[] accepts);

  /**
   * Select the Content-Type header's value from the given array:
   * if JSON exists in the given array, use it;
   * otherwise use the first one of the array.
   * @param contentTypes The Content-Type array to select from
   * @return The Content-Type header to use. If the given array is empty,
   * JSON will be used.
   */
  String selectHeaderContentType(String[] contentTypes);

  /**
   * Escape the given string to be used as URL query value.
   * @param str String
   * @return Escaped string
   */
  String escapeString(String str);
}
