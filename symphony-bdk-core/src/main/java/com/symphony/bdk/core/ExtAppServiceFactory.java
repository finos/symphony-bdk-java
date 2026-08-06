package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.app.AppUsersService;
import com.symphony.bdk.gen.api.AppsApi;
import com.symphony.bdk.http.api.ApiClient;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Factory responsible for creating Ext App service instances for Symphony Bdk apps entry point:
 * <ul>
 *   <li>{@link AppUsersService}</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
class ExtAppServiceFactory {

  private final ApiClient usersClient;
  private final ExtAppAuthSession authSession;
  private final BdkConfig config;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ExtAppServiceFactory(ApiClientFactory apiClientFactory, ExtAppAuthSession authSession, BdkConfig config) {
    this.config = config;
    this.usersClient = apiClientFactory.getUsersClient();
    this.authSession = authSession;
    this.retryBuilder = new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry());


  }

  /**
   * Returns a fully initialized {@link AppUsersService}.
   *
   * @return a new {@link AppUsersService} instance.
   */
  public AppUsersService getAppService() {
    return new AppUsersService(config.getApp().getAppId(), new AppsApi(usersClient), this.authSession, this.retryBuilder);
  }
}
