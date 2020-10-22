package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.properties.CorsProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * Configuration and injection of the security beans within the Spring application context.
 */
public class BdkExtAppSecurityConfig {

  @Bean
  @ConditionalOnMissingBean
  public WebMvcConfigurer corsConfigurer(SymphonyBdkAppProperties properties) {
    return new BdkExtAppWebMvcConfigurer(properties);
  }

  static class BdkExtAppWebMvcConfigurer implements WebMvcConfigurer {
    private final SymphonyBdkAppProperties properties;

    public BdkExtAppWebMvcConfigurer(SymphonyBdkAppProperties properties) {
      this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      for (Map.Entry<String, CorsProperties> urlMapping : properties.getCors().entrySet()) {
        CorsProperties corsProperties = urlMapping.getValue();
        registry.addMapping(urlMapping.getKey())
            .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
            .allowCredentials(corsProperties.getAllowedCredentials())
            .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
            .allowedMethods(corsProperties.getAllowedMethod().toArray(new String[0]))
            .exposedHeaders(corsProperties.getExposedHeaders().toArray(new String[0]));
      }
    }
  }
}
