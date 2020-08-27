package com.symphony.bdk.core.client.exception;

/**
 * Exception thrown when initializing the {@link com.symphony.bdk.core.api.invoker.ApiClient}. This can be triggered
 * when in several situation such as:
 * <ul>
 *   <li>trying to build a Client with certificate but path and password have not been properly configured</li>
 *   <li>the certificate could not be read</li>
 * </ul>
 * <p>
 *   Note: This is an unchecked since this kind of error can only happen when the application starts.
 * </p>
 */
public class ApiClientInitializationException extends RuntimeException {

  public ApiClientInitializationException(String message) {
    super(message);
  }

  public ApiClientInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
