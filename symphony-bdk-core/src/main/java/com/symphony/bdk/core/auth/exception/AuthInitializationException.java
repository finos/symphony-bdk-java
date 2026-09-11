package com.symphony.bdk.core.auth.exception;

import org.apiguardian.api.API;

/**
 * Thrown when unable to read/parse a RSA Private Key or a certificate.
 */
@API(status = API.Status.STABLE)
public class AuthInitializationException extends Exception {

  public AuthInitializationException(String message) {
    super(message);
  }

  public AuthInitializationException(String message, Throwable source) {
    super(message, source);
  }
}
