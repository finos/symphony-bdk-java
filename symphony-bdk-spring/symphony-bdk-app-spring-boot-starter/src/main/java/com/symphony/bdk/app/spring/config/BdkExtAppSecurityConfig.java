package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class BdkExtAppSecurityConfig {

  @Bean
  @ConditionalOnProperty("bdk-app.cors")
  public WebMvcConfigurer corsConfigurer(SymphonyBdkAppProperties properties) {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(properties.getCors().getUrlMapping()).allowedOrigins(properties.getCors().getAllowedOrigin());
      }
    };
  }
}
