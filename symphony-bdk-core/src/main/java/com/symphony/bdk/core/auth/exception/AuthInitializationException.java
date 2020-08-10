package com.symphony.bdk.core.auth.exception;

import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Thrown when unable to read/parse a RSA Private Key or a certificate.
 */
@API(status = API.Status.STABLE)
public class AuthInitializationException extends Exception {

  public AuthInitializationException(@Nonnull String message, @Nonnull Throwable source) {
    super(message, source);
  }
}
