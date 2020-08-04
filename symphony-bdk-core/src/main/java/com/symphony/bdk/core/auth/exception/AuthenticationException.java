package com.symphony.bdk.core.auth.exception;

import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.EXPERIMENTAL)
public class AuthenticationException extends Exception {

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
