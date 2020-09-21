package com.symphony.bdk.core.service.datafeed.exception;

import com.symphony.bdk.core.retry.RetryWithRecovery;

/**
 * Exception thrown when recovery strategy in a {@link RetryWithRecovery} failed.
 * Especially used in DataFeed services.
 */
public class NestedRetryException extends RuntimeException {
  public NestedRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
