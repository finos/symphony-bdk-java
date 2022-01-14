package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
@ComponentScan(
    basePackages = "com.symphony.bdk",
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BdkExtensionService.class)
)
public class BdkExtensionConfig {

  @Bean
  @ConditionalOnMissingBean(ExtensionService.class)
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public ExtensionService extensionService(
      final RetryWithRecoveryBuilder<?> retryWithRecoveryBuilder,
      final ApiClientFactory apiClientFactory,
      final Optional<AuthSession> botSession,
      final BdkConfig config,
      final List<BdkExtension> extensions
  ) {

    final ExtensionService extensionService = new ExtensionService(
        apiClientFactory,
        botSession.orElse(null),
        retryWithRecoveryBuilder,
        config
    );

    if (!extensions.isEmpty()) {
      log.debug("{} extension(s) found from application context. The following extension(s) will be registered:", extensions.size());
      extensions.forEach(e -> log.debug("- {}", e.getClass().getCanonicalName()));
      extensions.forEach(extensionService::register);
    }

    return extensionService;
  }

  @Bean
  @DependsOn("extensionService")
  public List<BdkExtensionService> bdkExtensionServices(final List<BdkExtension> extensions, final ConfigurableListableBeanFactory beanFactory) {
    final List<BdkExtensionService> services = new ArrayList<>(extensions.size());

    for (BdkExtension extension : extensions) {
     if (extension instanceof BdkExtensionServiceProvider) {
       final BdkExtensionService serviceBean = ((BdkExtensionServiceProvider<?>) extension).getService();
       beanFactory.registerSingleton(serviceBean.getClass().getCanonicalName(), serviceBean);
       services.add(serviceBean);
       log.info("Extension service bean <{}> successfully registered in application context", serviceBean.getClass().getCanonicalName());
     }
    }

    return services;
  }
}
