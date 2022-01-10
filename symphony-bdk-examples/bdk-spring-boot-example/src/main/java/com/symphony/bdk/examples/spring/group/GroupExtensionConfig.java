package com.symphony.bdk.examples.spring.group;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.ext.group.SymphonyGroupService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupService groupService(BdkConfig config, ApiClientFactory apiClientFactory, AuthSession session) {
    return new SymphonyGroupService(
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()),
        apiClientFactory,
        session
    );
  }
}
