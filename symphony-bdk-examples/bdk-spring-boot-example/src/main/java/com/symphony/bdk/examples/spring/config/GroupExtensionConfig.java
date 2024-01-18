package com.symphony.bdk.examples.spring.config;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.ext.group.SymphonyGroupService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupService groupService(
      final RetryWithRecoveryBuilder<?> retryBuilder,
      final ApiClientFactory apiClientFactory,
      final BotAuthSession session) {
    return new SymphonyGroupService(retryBuilder, apiClientFactory, session);
  }
}
