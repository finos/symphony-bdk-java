package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.CustomEnhancedAuthAuthenticator;
import com.symphony.bdk.core.auth.CustomEnhancedAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.CustomEnhancedAuthAuthentication;
import com.symphony.bdk.core.auth.impl.EnhancedAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.client.EnhancedAuthApiClientFactory;
import com.symphony.bdk.core.config.model.BdkCustomEnhancedAuthConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.apiguardian.api.API;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@API(status = API.Status.EXPERIMENTAL)
public class BdkEnhancedAuthConfig {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "bdk.enhanced-auth.enabled", havingValue = "true")
  public ApiClientFactory apiClientFactory(SymphonyBdkCoreProperties properties,
      CustomEnhancedAuthSession enhancedAuthSession) {
    BdkCustomEnhancedAuthConfig enhancedAuthConfig = properties.getEnhancedAuth();
    return new EnhancedAuthApiClientFactory(properties,
        new CustomEnhancedAuthAuthentication(enhancedAuthConfig.getHeaderName(),
            enhancedAuthSession::getEnhancedAuthToken));
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "bdk.enhanced-auth.enabled", havingValue = "true")
  public CustomEnhancedAuthSession enhancedAuthSession(CustomEnhancedAuthAuthenticator enhancedAuthAuthenticator) {
    CustomEnhancedAuthSession enhancedAuthSession = new EnhancedAuthSession((enhancedAuthAuthenticator));
    try {
      enhancedAuthSession.refresh();
      return enhancedAuthSession;
    } catch (AuthUnauthorizedException e) {
      throw new BeanInitializationException("Unable to get enhanced authentication token.", e);
    }
  }
}
