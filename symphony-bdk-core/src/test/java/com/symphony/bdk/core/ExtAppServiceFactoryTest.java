package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.app.AppUsersService;
import com.symphony.bdk.http.api.ApiClient;

import org.junit.jupiter.api.Test;

class ExtAppServiceFactoryTest {

  @Test
  void testGetAppService() {
    final ApiClientFactory apiClientFactory = mock(ApiClientFactory.class);
    final ExtAppAuthSession authSession = mock(ExtAppAuthSession.class);
    final BdkConfig config = new BdkConfig();
    config.getApp().setAppId("testApp");

    when(apiClientFactory.getUsersClient()).thenReturn(mock(ApiClient.class));

    final ExtAppServiceFactory serviceFactory = new ExtAppServiceFactory(apiClientFactory, authSession, config);
    final AppUsersService appUsersService = serviceFactory.getAppService();

    assertNotNull(appUsersService);
  }
}
