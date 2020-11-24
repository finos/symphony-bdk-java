package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.filter.TracingFilter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Configuration of the {@link TracingFilter}, that is activated by default.
 */
@ConditionalOnProperty(name = "bdk-app.tracing.enabled", havingValue = "true", matchIfMissing = true)
public class BdkExtAppTracingFilterConfig {

  @Bean
  public FilterRegistrationBean<TracingFilter> tracingFilter(SymphonyBdkAppProperties properties) {
    final FilterRegistrationBean<TracingFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new TracingFilter());
    registrationBean.addUrlPatterns(getUrlPatterns(properties));

    return registrationBean;
  }

  private static String[] getUrlPatterns(SymphonyBdkAppProperties properties) {
    return properties.getTracing().getUrlPatterns().toArray(new String[0]);
  }
}
