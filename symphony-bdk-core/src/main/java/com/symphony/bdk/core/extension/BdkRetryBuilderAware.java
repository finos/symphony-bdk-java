package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

import org.apiguardian.api.API;

/**
 * Interface to be implemented by any {@link com.symphony.bdk.extension.BdkExtension} that wishes to benefit from the
 * BDK HTTP Retry logic.
 *
 * @see com.symphony.bdk.extension.BdkExtension
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkRetryBuilderAware {

  /**
   * Set the {@link RetryWithRecoveryBuilder} object.
   *
   * @param retryBuilder the {@code RetryWithRecoveryBuilder} instance to be used by this object.
   */
  void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder);
}
