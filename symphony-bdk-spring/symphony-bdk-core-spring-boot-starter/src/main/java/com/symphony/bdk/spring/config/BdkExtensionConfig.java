package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class BdkExtensionConfig {

  @Bean
  public ExtensionService extensionService(
      final ApiClientFactory apiClientFactory,
      final AuthSession botSession,
      final BdkConfig config,
      final List<BdkExtension> extensions
  ) {
    final ExtensionService extensionService = new ExtensionService(
        apiClientFactory,
        botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()),
        config
    );

    log.debug("Registering {} extensions", extensions.size());
    extensions.forEach(extensionService::register);

    return extensionService;
  }
}
