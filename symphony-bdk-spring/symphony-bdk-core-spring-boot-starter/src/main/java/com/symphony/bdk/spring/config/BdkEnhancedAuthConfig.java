package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSessionRegistry;
import com.symphony.bdk.core.auth.CustomEnhancedAuthAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.CustomEnhancedAuthAuthentication;
import com.symphony.bdk.core.auth.impl.EnhancedAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.client.EnhancedAuthApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.http.api.auth.Authentication;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "bdk.enhanced-auth.enabled", havingValue = "true")
public class BdkEnhancedAuthConfig {

  @Bean
  public ApiClientFactory apiClientFactory(SymphonyBdkCoreProperties properties,
      @Qualifier("enhancedAuthAuthentication") Authentication customAuthentication) {
    return new EnhancedAuthApiClientFactory(properties, customAuthentication);
  }

  @Bean("enhancedAuthAuthentication")
  public Authentication enhancedAuth(BdkConfig config, EnhancedAuthSession enhancedAuthSession) {
    return new CustomEnhancedAuthAuthentication(config.getEnhancedAuth().getHeaderName(),
        enhancedAuthSession::getEnhancedAuthToken);
  }

  @Bean
  public EnhancedAuthSession enhancedAuthSession(CustomEnhancedAuthAuthenticator enhancedAuthAuthenticator,
      AuthSessionRegistry sessionRegistry) {
    EnhancedAuthSession enhancedAuthSession = new EnhancedAuthSession((enhancedAuthAuthenticator), sessionRegistry);
    try {
      enhancedAuthSession.refresh();
    } catch (AuthUnauthorizedException e) {
      throw new BeanInitializationException("Unable to get enhanced authentication token.", e);
    }
    enhancedAuthSession.register(sessionRegistry);
    return enhancedAuthSession;
  }
}
