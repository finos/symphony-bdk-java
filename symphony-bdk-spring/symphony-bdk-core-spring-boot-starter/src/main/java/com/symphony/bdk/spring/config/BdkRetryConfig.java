package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@API(status = API.Status.STABLE)
public class BdkRetryConfig {

  @Bean
  @ConditionalOnMissingBean(RetryWithRecoveryBuilder.class)
  public RetryWithRecoveryBuilder<?> retryWithRecoveryBuilder(BdkConfig config) {
    return new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry());
  }
}
