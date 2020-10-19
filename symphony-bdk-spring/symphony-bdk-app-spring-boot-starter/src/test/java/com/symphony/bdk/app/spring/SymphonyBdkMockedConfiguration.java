package com.symphony.bdk.app.spring;

import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import com.symphony.bdk.core.config.model.BdkExtAppConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SymphonyBdkMockedConfiguration {

  @Bean
  public ExtensionAppAuthenticator extensionAppAuthenticator() {
    return mock(ExtensionAppAuthenticator.class);
  }

  @Bean
  public SymphonyBdkCoreProperties coreProperties() {
    SymphonyBdkCoreProperties properties = new SymphonyBdkCoreProperties();
    BdkExtAppConfig appConfig = new BdkExtAppConfig();
    appConfig.setAppId("appId");
    properties.setApp(appConfig);
    return properties;
  }
}
