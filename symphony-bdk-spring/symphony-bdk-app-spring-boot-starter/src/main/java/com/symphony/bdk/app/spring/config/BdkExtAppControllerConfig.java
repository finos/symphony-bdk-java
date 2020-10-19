package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.CircleOfTrustController;
import com.symphony.bdk.app.spring.auth.service.CircleOfTrustService;
import com.symphony.bdk.app.spring.exception.GlobalControllerExceptionHandler;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Configuration and injection of the main rest controllers for extension app APIs as beans within the Spring application context.
 */
public class BdkExtAppControllerConfig {

  @Bean
  @ConditionalOnMissingBean
  public CircleOfTrustService circleOfTrustService(ExtensionAppAuthenticator authenticator, SymphonyBdkCoreProperties properties) {
    return new CircleOfTrustService(authenticator, properties);
  }

  @Bean
  @ConditionalOnProperty(name = "bdk-app.auth.enabled", havingValue = "true")
  public CircleOfTrustController circleOfTrustController(
      SymphonyBdkAppProperties properties,
      CircleOfTrustService circleOfTrustService
  ) {
    return new CircleOfTrustController(properties, circleOfTrustService);
  }

  @Bean
  @ConditionalOnMissingBean
  public GlobalControllerExceptionHandler globalControllerExceptionHandler() {
    return new GlobalControllerExceptionHandler();
  }
}
