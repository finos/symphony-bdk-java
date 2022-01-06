package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

public interface BdkRetryBuilderAware {

  void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder);
}
