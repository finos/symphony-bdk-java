package com.symphony.bdk.app.spring.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.filter.TracingFilter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

class BdkExtAppTracingFilterConfigTest {

  @Test
  void checkFilterOrder() {
    final FilterRegistrationBean<TracingFilter> tracingFilterRegistration =
        new BdkExtAppTracingFilterConfig().tracingFilter(new SymphonyBdkAppProperties());

    assertEquals("/*", tracingFilterRegistration.getUrlPatterns().stream().findFirst().get());
    assertEquals(Ordered.HIGHEST_PRECEDENCE, tracingFilterRegistration.getOrder());
  }
}
