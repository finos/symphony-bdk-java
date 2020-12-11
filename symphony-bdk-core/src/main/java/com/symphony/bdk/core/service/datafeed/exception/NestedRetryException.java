package com.symphony.bdk.core.service.datafeed.exception;

import com.symphony.bdk.core.retry.RetryWithRecovery;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import org.apiguardian.api.API;

/**
 * Exception thrown when recovery strategy in a {@link RetryWithRecovery} failed.
 * Especially used in {@link DatafeedLoop} implementations.
 */
@API(status = API.Status.INTERNAL)
public class NestedRetryException extends RuntimeException {
  public NestedRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
