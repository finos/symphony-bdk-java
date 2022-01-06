package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public interface BdkRetryBuilderAware {

  void setRetryBuilder(RetryWithRecoveryBuilder<?> retryBuilder);
}
