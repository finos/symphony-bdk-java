package com.symphony.bdk.core.auth.exception;

/**
 *
 */
public class AuthenticationException extends Exception {

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
