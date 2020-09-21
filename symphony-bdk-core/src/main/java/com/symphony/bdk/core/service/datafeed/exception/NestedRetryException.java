package com.symphony.bdk.core.service.datafeed.exception;

/**
 * Exception thrown when recovery strategy in a {@link com.symphony.bdk.core.util.function.RetryWithRecovery} failed.
 * Especially used in DataFeed services.
 */
public class NestedRetryException extends RuntimeException {
  public NestedRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
