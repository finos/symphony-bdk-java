package com.symphony.bdk.examples.app.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // This configuration is not recommended in production setup
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    //TODO: Fix this bean method
    return null;
  }

}
