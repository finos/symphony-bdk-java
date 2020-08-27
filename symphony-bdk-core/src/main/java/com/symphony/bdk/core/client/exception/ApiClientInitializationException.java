package com.symphony.bdk.core.client.exception;

/**
 * TODO: add description here
 */
public class ApiClientInitializationException extends RuntimeException {

  public ApiClientInitializationException(String message) {
    super(message);
  }

  public ApiClientInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
