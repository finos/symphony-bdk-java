package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class BdkExtensionConfig {

  @Bean
  @ConditionalOnMissingBean(ExtensionService.class)
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public ExtensionService extensionService(
      final ApiClientFactory apiClientFactory,
      final Optional<AuthSession> botSession,
      final BdkConfig config,
      final List<BdkExtension> extensions
  ) {

    final ExtensionService extensionService = new ExtensionService(
        apiClientFactory,
        botSession.orElse(null),
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()),
        config
    );

    if (!extensions.isEmpty()) {
      log.debug("{} extension(s) found from application context. The following extension(s) will be registered:", extensions.size());
      extensions.forEach(e -> log.debug("- {}", e.getClass().getCanonicalName()));
      extensions.forEach(extensionService::register);
    }

    return extensionService;
  }
}
