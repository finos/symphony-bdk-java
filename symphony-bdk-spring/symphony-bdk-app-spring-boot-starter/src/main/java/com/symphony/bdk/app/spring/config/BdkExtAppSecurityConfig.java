package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.properties.CorsProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
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

    private static final String WARN_MSG = "CORS property '{}' (mapping '{}') is now deprecated and has been replaced by '{}'. "
        + "Please update your application.yaml accordingly.";

    private final SymphonyBdkAppProperties properties;

    public BdkExtAppWebMvcConfigurer(SymphonyBdkAppProperties properties) {
      this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      for (Map.Entry<String, CorsProperties> urlMapping : this.properties.getCors().entrySet()) {
        final CorsProperties corsProperties = urlMapping.getValue();
        registry.addMapping(urlMapping.getKey())
            .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
            .allowCredentials(getAllowCredentials(urlMapping.getKey(), corsProperties))
            .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
            .allowedMethods(getAllowedMethods(urlMapping.getKey(), corsProperties).toArray(new String[0]))
            .exposedHeaders(corsProperties.getExposedHeaders().toArray(new String[0]));
      }
    }

    /**
     * Preserve backward compatibility after renaming 'allowed-method' property to 'allowed-methods'
     */
    private static List<String> getAllowedMethods(String urlMapping, CorsProperties corsProperties) {

      if (corsProperties.getAllowedMethod() != null) {
        log.warn(WARN_MSG, "allowed-method", urlMapping, "allowed-methods");
        return corsProperties.getAllowedMethod();
      }

      return corsProperties.getAllowedMethods();
    }

    /**
     * Preserve backward compatibility after renaming 'allowed-credentials' property to 'allow-credentials'
     */
    private static boolean getAllowCredentials(String urlMapping, CorsProperties corsProperties) {

      if (corsProperties.getAllowedCredentials() != null) {
        log.warn(WARN_MSG, "allowed-credentials", urlMapping, "allow-credentials");
        return corsProperties.getAllowedCredentials();
      }

      return corsProperties.getAllowCredentials();
    }
  }
}
