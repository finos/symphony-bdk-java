package com.symphony.bdk.app.spring.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;

import com.symphony.bdk.app.spring.properties.CorsProperties;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

public class BdkExtAppSecurityConfigTest {

  @Test
  void createCorsConfigurer() {

    final BdkExtAppSecurityConfig config = new BdkExtAppSecurityConfig();
    final SymphonyBdkAppProperties props = new SymphonyBdkAppProperties();
    final CorsProperties cors = new CorsProperties();
    cors.setAllowedMethods(Collections.singletonList("GET"));
    cors.setAllowCredentials(false);
    cors.setExposedHeaders(Arrays.asList("header1", "header2"));
    cors.setAllowedHeaders(Arrays.asList("header1", "header2"));
    props.setCors(Collections.singletonMap("*", cors));

    WebMvcConfigurer configurer = config.corsConfigurer(props);
    CorsRegistry registry = mock(CorsRegistry.class);
    CorsRegistration registration = mock(CorsRegistration.class);
    when(registry.addMapping(any())).thenReturn(registration);
    when(registration.allowedOrigins(any())).thenReturn(registration);
    when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
    when(registration.allowedHeaders(any())).thenReturn(registration);
    when(registration.allowedMethods(any())).thenReturn(registration);
    when(registration.exposedHeaders(any())).thenReturn(registration);
    configurer.addCorsMappings(registry);

    verify(registry).addMapping("*");
    verify(registration).allowedMethods("GET");
    verify(registration).exposedHeaders("header1", "header2");
    verify(registration).allowCredentials(false);
    verify(registration).allowedOrigins("/**");
    verify(registration).allowedHeaders("header1", "header2");
  }

  @Test
  void createCorsConfigurer_withBackwardCompatibility() {

    final BdkExtAppSecurityConfig config = new BdkExtAppSecurityConfig();
    final SymphonyBdkAppProperties props = new SymphonyBdkAppProperties();
    final CorsProperties cors = new CorsProperties();
    cors.setAllowedMethod(Collections.singletonList("POST"));
    cors.setAllowedMethods(Collections.singletonList("GET"));
    cors.setAllowedCredentials(true);
    cors.setAllowCredentials(false);
    cors.setExposedHeaders(Arrays.asList("header1", "header2"));
    cors.setAllowedHeaders(Arrays.asList("header1", "header2"));
    props.setCors(Collections.singletonMap("*", cors));

    WebMvcConfigurer configurer = config.corsConfigurer(props);
    CorsRegistry registry = mock(CorsRegistry.class);
    CorsRegistration registration = mock(CorsRegistration.class);
    when(registry.addMapping(any())).thenReturn(registration);
    when(registration.allowedOrigins(any())).thenReturn(registration);
    when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
    when(registration.allowedHeaders(any())).thenReturn(registration);
    when(registration.allowedMethods(any())).thenReturn(registration);
    when(registration.exposedHeaders(any())).thenReturn(registration);
    configurer.addCorsMappings(registry);

    verify(registry).addMapping("*");
    verify(registration).allowedMethods("POST");
    verify(registration).exposedHeaders("header1", "header2");
    verify(registration).allowCredentials(true);
    verify(registration).allowedOrigins("/**");
    verify(registration).allowedHeaders("header1", "header2");
  }
}
