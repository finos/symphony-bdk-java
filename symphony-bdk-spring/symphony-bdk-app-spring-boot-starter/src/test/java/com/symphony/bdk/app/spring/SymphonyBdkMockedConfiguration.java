package com.symphony.bdk.app.spring;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.model.UserV2;

import com.symphony.bdk.core.config.model.BdkExtAppConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SymphonyBdkMockedConfiguration {

  @Bean
  public ExtensionAppAuthenticator extensionAppAuthenticator() {
    return mock(ExtensionAppAuthenticator.class);
  }

  @Bean
  public AuthSession botSession() {
    return mock(AuthSession.class);
  }

  @Bean
  public SessionService sessionService() {
    SessionService sessionService = mock(SessionService.class);
    when(sessionService.getSession()).thenReturn(new UserV2().displayName("BotMention"));
    return sessionService;
  }

  @Bean
  @Primary
  public SymphonyBdkCoreProperties coreProperties() {
    SymphonyBdkCoreProperties properties = new SymphonyBdkCoreProperties();
    BdkExtAppConfig appConfig = new BdkExtAppConfig();
    appConfig.setAppId("appId");
    properties.setApp(appConfig);
    return properties;
  }
}
