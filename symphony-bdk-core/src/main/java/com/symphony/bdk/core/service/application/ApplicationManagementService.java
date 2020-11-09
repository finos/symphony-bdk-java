package com.symphony.bdk.core.service.application;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.model.ApplicationDetail;
import com.symphony.bdk.gen.api.model.PodAppEntitlement;
import com.symphony.bdk.gen.api.model.UserAppEntitlement;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Service class for managing the applications and the application entitlements.
 * <p>
 * This services used for retrieving information about a particular application or application entitlements,
 * performing some actions related to the applications like:
 * <p><ul>
 * <li>Create an application</li>
 * <li>Update an existing application</li>
 * <li>Delete an existing application</li>
 * <li>Get the information of an existing application</li>
 * <li>Update application entitlements</li>
 * <li>Update user applications</li>
 * </ul></p>
 * </p>
 */
@API(status = API.Status.STABLE)
public class ApplicationManagementService {

  private final ApplicationApi applicationApi;
  private final AppEntitlementApi appEntitlementApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ApplicationManagementService(ApplicationApi applicationApi, AppEntitlementApi appEntitlementApi,
      AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.applicationApi = applicationApi;
    this.appEntitlementApi = appEntitlementApi;
    this.authSession = authSession;
    this.retryBuilder = retryBuilder;
  }

  /**
   * Create a new application.
   *
   * @param applicationDetail Contains the following fields for creating an application: appId, name, appUrl, domain, and publisher.
   *                          Note that appUrl is not required.
   * @return The created application.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#create-app">Create Application</a>
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#create-application-with-an-rsa-public-key">Create Application with an RSA Public Key</a>
   */
  public ApplicationDetail createApplication(@Nonnull ApplicationDetail applicationDetail) {
    return executeAndRetry("createApplication",
        () -> applicationApi.v1AdminAppCreatePost(authSession.getSessionToken(), applicationDetail));
  }

  /**
   * Update an existing application.
   *
   * @param appId             Id of the application needs to be updated.
   * @param applicationDetail Contains the following fields for creating an application: appId, name, appUrl, domain, and publisher.
   *                          Note that appUrl is not required.
   * @return The updated application.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#update-application">Update Application</a>
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#update-application-with-an-rsa-public-key">Update Application with an RSA Public Key</a>
   */
  public ApplicationDetail updateApplication(@Nonnull String appId, @Nonnull ApplicationDetail applicationDetail) {
    return executeAndRetry("updateApplication",
        () -> applicationApi.v1AdminAppIdUpdatePost(authSession.getSessionToken(), appId, applicationDetail));
  }

  /**
   * Delete an existing application.
   *
   * @param appId Id of the application needs to be deleted.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#delete-application">Delete Application</a>
   */
  public void deleteApplication(@Nonnull String appId) {
    executeAndRetry("deleteApplication",
        () -> applicationApi.v1AdminAppIdDeletePost(authSession.getSessionToken(), appId));
  }

  /**
   * Gets an existing application.
   *
   * @param appId Id of the application.
   * @return The detail of the lookup application.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#get-application">Get Application</a>
   */
  public ApplicationDetail getApplication(@Nonnull String appId) {
    return executeAndRetry("getApplication",
        () -> applicationApi.v1AdminAppIdGetGet(authSession.getSessionToken(), appId));
  }

  /**
   * Get the list of application entitlements for the company.
   *
   * @return The list of application entitlements.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#list-app-entitlements">List App Entitlements</a>
   */
  public List<PodAppEntitlement> listAppEntitlements() {
    return executeAndRetry("listAppEntitlements",
        () -> appEntitlementApi.v1AdminAppEntitlementListGet(authSession.getSessionToken()));
  }

  /**
   * Update the list of application entitlements for the company.
   *
   * @param entitlementList The list of entitlements to be updated by.
   * @return The updated list of entitlements.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#update-application-entitlements">Update App Entitlements</a>
   */
  public List<PodAppEntitlement> updateAppEntitlements(@Nonnull List<PodAppEntitlement> entitlementList) {
    return executeAndRetry("updateAppEntitlements",
        () -> appEntitlementApi.v1AdminAppEntitlementListPost(authSession.getSessionToken(), entitlementList));
  }

  /**
   * Get the list of Symphony application entitlements for a particular user.
   *
   * @param userId User id.
   * @return The list of Symphony application entitlements for this user.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#user-apps">User Apps</a>
   */
  public List<UserAppEntitlement> listUserApplications(@Nonnull Long userId) {
    return executeAndRetry("listUserApplications",
        () -> appEntitlementApi.v1AdminUserUidAppEntitlementListGet(authSession.getSessionToken(), userId));
  }

  /**
   * Update the application entitlements for a particular user.
   *
   * @param userId                 User Id.
   * @param userAppEntitlementList The list of App Entitlements needs to be updated.
   * @return The updated list of Symphony application entitlements for this user.
   * @see <a href="https://developers.symphony.com/restapi/v20.9/reference#update-user-apps">Update User Apps</a>
   */
  public List<UserAppEntitlement> updateUserApplications(@Nonnull Long userId,
      @Nonnull List<UserAppEntitlement> userAppEntitlementList) {
    return executeAndRetry("updateUserApplications",
        () -> appEntitlementApi.v1AdminUserUidAppEntitlementListPost(authSession.getSessionToken(), userId,
            userAppEntitlementList));
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies()
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
