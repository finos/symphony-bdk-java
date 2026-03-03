package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.app.AppUsersService;

import org.apiguardian.api.API;

/**
 * Entry point for external application services relying on the App session token
 */
@API(status = API.Status.STABLE)
public class ExtAppServices {
  AppUsersService appUsersService;

  public ExtAppServices(ApiClientFactory apiClientFactory, ExtAppAuthSession authSession, BdkConfig config) {
    ExtAppServiceFactory extAppServiceFactory = new ExtAppServiceFactory(apiClientFactory, authSession, config);
    this.appUsersService = extAppServiceFactory.getAppService();
  }

    /**
   * Get the {@link AppUsersService}.
   *
   * @return an {@link AppUsersService} instance.
   */
  public AppUsersService appUsers() {
    return this.appUsersService;
  };
}
