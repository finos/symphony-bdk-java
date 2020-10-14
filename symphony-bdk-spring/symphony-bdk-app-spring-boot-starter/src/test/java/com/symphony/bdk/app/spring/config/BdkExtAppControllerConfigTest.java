package com.symphony.bdk.app.spring.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import org.junit.jupiter.api.Test;

public class BdkExtAppControllerConfigTest {

  @Test
  void createCircleOfTrustControllerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();
    final SymphonyBdkAppProperties props = new SymphonyBdkAppProperties();
    final ExtensionAppAuthenticator authenticator = mock(ExtensionAppAuthenticator.class);

    assertNotNull(config.circleOfTrustController(props,authenticator));
  }

  @Test
  void createErrorHandlerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();

    assertNotNull(config.globalControllerExceptionHandler());
  }
}
