package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;
import com.symphony.bdk.core.service.app.AppUsersService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtAppServicesTest {

  private ExtAppServices extAppServices;

  @BeforeEach
  void setUp() {
    final BdkConfig config = new BdkConfig();
    config.setApp(new BdkExtAppConfig());
    config.getApp().setAppId("test-app");

    this.extAppServices = new ExtAppServices(mock(ApiClientFactory.class), mock(ExtAppAuthSession.class), config);
  }

  @Test
  void testAppUsers() {
    AppUsersService appUsersService = this.extAppServices.appUsers();
    assertNotNull(appUsersService);
  }
}
