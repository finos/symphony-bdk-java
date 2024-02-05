package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.core.service.health.HealthService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BdkHealthIndicatorConfigTest {

  @Test
  void createSymphonyBdkHealthIndicatorTest() {
    final BdkHealthIndicatorConfig config = new BdkHealthIndicatorConfig();
    final HealthService healthService = mock(HealthService.class);

    assertNotNull(config.symphonyBdkHealthIndicator(healthService));

    assertNotNull(config.customStatusCodeMapper());
  }
}