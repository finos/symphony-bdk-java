package com.symphony.bdk.app.spring.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.service.AppTokenService;
import com.symphony.bdk.app.spring.auth.service.JwtService;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.junit.jupiter.api.Test;

public class BdkExtAppControllerConfigTest {

  @Test
  void createCircleOfTrustControllerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();
    final SymphonyBdkAppProperties props = new SymphonyBdkAppProperties();
    final ExtensionAppAuthenticator authenticator = mock(ExtensionAppAuthenticator.class);
    final JwtService jwtService = mock(JwtService.class);
    final AppTokenService appTokenService = mock(AppTokenService.class);

    assertNotNull(config.circleOfTrustController(props,authenticator, jwtService, appTokenService));
  }

  @Test
  void createErrorHandlerTest() {

    final BdkExtAppControllerConfig config = new BdkExtAppControllerConfig();

    assertNotNull(config.globalControllerExceptionHandler(new SymphonyBdkCoreProperties()));
  }
}
