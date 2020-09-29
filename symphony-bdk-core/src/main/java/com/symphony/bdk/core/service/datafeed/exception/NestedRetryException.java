package com.symphony.bdk.core.service.datafeed.exception;

import com.symphony.bdk.core.retry.RetryWithRecovery;

import org.apiguardian.api.API;

/**
 * Exception thrown when recovery strategy in a {@link RetryWithRecovery} failed.
 * Especially used in {@link com.symphony.bdk.core.service.datafeed.DatafeedService} implementations.
 */
@API(status = API.Status.INTERNAL)
public class NestedRetryException extends RuntimeException {
  public NestedRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
