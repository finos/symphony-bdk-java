package com.symphony.bdk.core.service.app;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;

import com.symphony.bdk.gen.api.AppsApi;
import com.symphony.bdk.gen.api.model.AppUsersResponse;

import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Service class for fetching app users.
 * <p>
 * This service is used for retrieving information about users that have the application installed:
 * <p><ul>
 * <li>App product information</li>
 * <li>Whether the app is enabled</li>
 * </ul></p>
 * </p>
 */
@API(status = API.Status.STABLE)
public class AppUsersService {

  private final static Integer DEFAULT_PAGE_SIZE = 100;

  private final AppsApi appsApi;
  private final ExtAppAuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;
  private final String appId;


  public AppUsersService(String appId,
                         AppsApi appsApi,
                         ExtAppAuthSession authSession,
                         RetryWithRecoveryBuilder<?> retryBuilder) {
    this.appsApi = appsApi;
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);;
    this.appId = appId;
  }

  /**
   * Get app users
   * @param page to be returned
   * @param size number of result per page
   * @param sort sorting parameters
   * @return {@link AppUsersResponse}
   */
  public AppUsersResponse listAppUsers(Integer page, Integer size, List<String> sort) {
    String session = authSession.getAppSession();
    return executeAndRetry("listAppUsers", appsApi.getApiClient().getBasePath(),
        () -> appsApi.findAppUsers(session, appId, true, page, size, sort));
  }

  /**
   * Get app users
   * @param page to be returned
   * @param size number of result per page
   * @return {@link AppUsersResponse}
   */
  public AppUsersResponse listAppUsers(Integer page, Integer size) {
    return listAppUsers(page, size, null);
  }

  /**
   * Get app users
   * @param page to be returned
   * @return {@link AppUsersResponse}
   */
  public AppUsersResponse listAppUsers(Integer page) {
    return listAppUsers(page, DEFAULT_PAGE_SIZE, null);
  }

  /**
   * Get app users
   * @return {@link AppUsersResponse}
   */
  public AppUsersResponse listAppUsers() {
    return listAppUsers(null, DEFAULT_PAGE_SIZE, null);
  }

  private <T> T executeAndRetry(String name, String address, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, address, supplier);
  }
}
