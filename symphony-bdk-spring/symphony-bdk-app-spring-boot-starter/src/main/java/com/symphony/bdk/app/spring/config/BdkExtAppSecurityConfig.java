package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.properties.CorsProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuration and injection of the security beans within the Spring application context.
 */
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bdk-app.auth.enabled", havingValue = "true")
public class BdkExtAppSecurityConfig {
  private final SymphonyBdkAppProperties properties;

  @Bean
  public List<CorsFilter> corsFilters() {
    return properties.getCors().keySet().stream()
        .map(mapping -> {
            final CorsProperties props = properties.getCors().get(mapping);
            var config = new CorsConfiguration();
            config.setAllowedOrigins(props.getAllowedOrigins());
            config.setAllowedHeaders(props.getAllowedHeaders());
            config.setAllowedMethods(props.getAllowedMethods());
            config.setExposedHeaders(props.getExposedHeaders());
            config.setAllowCredentials(props.getAllowCredentials());
            var source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration(mapping, config);
            return new CorsFilter(source);
        })
        .toList();
  }
}
