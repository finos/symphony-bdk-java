package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.auth.CircleOfTrustController;
import com.symphony.bdk.app.spring.exception.GlobalControllerExceptionHandler;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public class BdkExtAppControllerConfig {

  @Bean
  @ConditionalOnProperty(name = "bdk.app.auth.enabled", havingValue = "true")
  public CircleOfTrustController circleOfTrustController(ExtensionAppAuthenticator extensionAppAuthenticator) {
    return new CircleOfTrustController(extensionAppAuthenticator);
  }

  @Bean
  @ConditionalOnMissingBean
  public GlobalControllerExceptionHandler globalControllerExceptionHandler() {
    return new GlobalControllerExceptionHandler();
  }

}
