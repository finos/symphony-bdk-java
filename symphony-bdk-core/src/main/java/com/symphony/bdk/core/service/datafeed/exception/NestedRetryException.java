package com.symphony.bdk.core.service.datafeed.exception;

public class NestedRetryException extends RuntimeException {
  public NestedRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
