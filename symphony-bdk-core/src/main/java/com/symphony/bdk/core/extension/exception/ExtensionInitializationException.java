package com.symphony.bdk.core.extension.exception;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public class ExtensionInitializationException extends RuntimeException {

  public ExtensionInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
