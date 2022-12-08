package com.symphony.bdk.app.spring.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.service.CircleOfTrustService;
import com.symphony.bdk.core.service.health.HealthService;

import org.junit.jupiter.api.Test;

public class BdkExtAppControllerConfigTest {

  @Test
  void createSymphonyBdkHealthIndicatorTest() {
    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();
    final HealthService healthService = mock(HealthService.class);

    assertNotNull(config.symphonyBdkHealthIndicator(healthService));
  }

  @Test
  void createCircleOfTrustControllerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();
    final SymphonyBdkAppProperties props = new SymphonyBdkAppProperties();
    final CircleOfTrustService circleOfTrustService = mock(CircleOfTrustService.class);

    assertNotNull(config.circleOfTrustController(props, circleOfTrustService));
  }

  @Test
  void createErrorHandlerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();

    assertNotNull(config.globalControllerExceptionHandler());
  }
}
