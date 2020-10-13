package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class BdkExtAppConfig {

  @Bean
  @ConditionalOnMissingBean
  public ExtensionAppAuthenticator extensionAppAuthenticator(AuthenticatorFactory authenticatorFactory) {
    try {
      return authenticatorFactory.getExtensionAppAuthenticator();
    } catch (AuthInitializationException e) {
      throw new BeanInitializationException("Unable to authenticate app", e);
    }
  }
}
