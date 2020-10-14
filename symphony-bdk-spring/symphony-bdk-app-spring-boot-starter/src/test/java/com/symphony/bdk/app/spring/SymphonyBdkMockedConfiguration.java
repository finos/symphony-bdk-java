package com.symphony.bdk.app.spring;

import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SymphonyBdkMockedConfiguration {

  @Bean
  public ExtensionAppAuthenticator extensionAppAuthenticator() {
    return mock(ExtensionAppAuthenticator.class);
  }
}
