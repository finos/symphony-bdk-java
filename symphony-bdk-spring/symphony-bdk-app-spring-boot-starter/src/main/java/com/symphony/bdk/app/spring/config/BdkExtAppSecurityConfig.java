package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.properties.CorsProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * Configuration and injection of the security beans within the Spring application context.
 */
@Slf4j
public class BdkExtAppSecurityConfig {

  @Bean
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
      for (Map.Entry<String, CorsProperties> urlMapping : this.properties.getCors().entrySet()) {
        final CorsProperties corsProperties = urlMapping.getValue();
        warnDeprecatedProperties(corsProperties);
        registry.addMapping(urlMapping.getKey())
            .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
            .allowCredentials(corsProperties.getAllowCredentials())
            .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
            .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
            .exposedHeaders(corsProperties.getExposedHeaders().toArray(new String[0]));
      }
    }

    private static void warnDeprecatedProperties(CorsProperties corsProperties) {
      final String msg = "CORS property '{}' is now deprecated and has been replaced by '{}'. "
          + "Please update your application.yaml accordingly.";

      if (corsProperties.getAllowedCredentials() != null) {
        log.warn(msg, "allowed-credentials", "allow-credentials");
      }
      if (corsProperties.getAllowedMethod() != null) {
        log.warn(msg, "allowed-method", "allowed-methods");
      }
    }
  }
}
