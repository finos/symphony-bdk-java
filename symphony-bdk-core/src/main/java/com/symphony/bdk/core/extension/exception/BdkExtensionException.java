package com.symphony.bdk.core.extension.exception;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public class BdkExtensionException extends RuntimeException {

  public BdkExtensionException(String message, Throwable cause) {
    super(message, cause);
  }
}
