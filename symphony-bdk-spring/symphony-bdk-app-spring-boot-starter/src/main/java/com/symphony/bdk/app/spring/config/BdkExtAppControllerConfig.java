package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.CircleOfTrustController;
import com.symphony.bdk.app.spring.auth.service.AppTokenService;
import com.symphony.bdk.app.spring.auth.service.JwtService;
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
  public JwtService jwtService(ExtensionAppAuthenticator extensionAppAuthenticator) {
    return new JwtService(extensionAppAuthenticator);
  }

  @Bean
  @ConditionalOnMissingBean
  public AppTokenService appTokenService(ExtensionAppAuthenticator extensionAppAuthenticator) {
    return new AppTokenService(extensionAppAuthenticator);
  }

  @Bean
  @ConditionalOnProperty(name = "bdk-app.auth.enabled", havingValue = "true")
  public CircleOfTrustController circleOfTrustController(
      SymphonyBdkAppProperties properties,
      ExtensionAppAuthenticator extensionAppAuthenticator,
      JwtService jwtService,
      AppTokenService appTokenService
  ) {
    return new CircleOfTrustController(properties, extensionAppAuthenticator, jwtService, appTokenService);
  }

  @Bean
  @ConditionalOnMissingBean
  public GlobalControllerExceptionHandler globalControllerExceptionHandler(SymphonyBdkCoreProperties properties) {
    return new GlobalControllerExceptionHandler(properties);
  }

}
