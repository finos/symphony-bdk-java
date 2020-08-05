package com.symphony.bdk.core.auth.exception;

import com.symphony.bdk.core.api.invoker.ApiException;

import org.apiguardian.api.API;

/**
 * This is a particularly tough exception that means that authentication cannot be performed for several raisons
 * depending on the context : regular authentication or OBO.
 */
@API(status = API.Status.STABLE)
public class AuthUnauthorizedException extends Exception {

  public AuthUnauthorizedException(String message, ApiException source) {
    super(message, source);
  }
}
