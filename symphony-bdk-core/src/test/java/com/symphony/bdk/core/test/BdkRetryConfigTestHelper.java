package com.symphony.bdk.core.test;

import com.symphony.bdk.core.config.model.BdkRetryConfig;

public class BdkRetryConfigTestHelper {

  public static BdkRetryConfig ofMinimalInterval() {
    return ofMinimalInterval(BdkRetryConfig.DEFAULT_MAX_ATTEMPTS);
  }

  public static BdkRetryConfig ofMinimalInterval(int maxAttempts) {
    BdkRetryConfig retryConfig = new BdkRetryConfig();
    retryConfig.setMultiplier(1);
    retryConfig.setInitialIntervalMillis(10);
    retryConfig.setMaxIntervalMillis(10);
    retryConfig.setMaxAttempts(maxAttempts);

    return retryConfig;
  }
}
