package com.symphony.bdk.core.service.user;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.gen.api.UserOrganisationsApi;
import com.symphony.bdk.gen.api.model.OrganisationStructure;
import com.symphony.bdk.gen.api.model.UserOrganisationsResponse;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Service to retrieve the authenticated user's personal chat organization structures &mdash;
 * custom directories/folders, multi-chat workspaces, and blast distribution lists &mdash; from the
 * {@code GET /v5/users/organisations} endpoint.
 */
@API(status = API.Status.EXPERIMENTAL)
public class UserOrganizationService implements OboService<UserOrganizationService> {

  private final ApiClient apiClient;
  private final UserOrganisationsApi userOrganisationsApi;
  private final @Nullable AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public UserOrganizationService(ApiClient apiClient, AuthSession authSession,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.userOrganisationsApi = new UserOrganisationsApi(apiClient);
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public UserOrganizationService(ApiClient apiClient, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.userOrganisationsApi = new UserOrganisationsApi(apiClient);
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  @Override
  public UserOrganizationService obo(AuthSession oboSession) {
    return new UserOrganizationService(this.apiClient, oboSession, this.retryBuilder);
  }

  /**
   * Retrieves the authenticated user's aggregated organization data (directories, workspaces, and
   * blast distribution lists) for the default {@code symphonyPrime} application.
   *
   * @return a {@link UserOrganisationsResponse} containing the user's organization data.
   */
  public UserOrganisationsResponse getUserOrganization() {
    return executeAndRetry("getUserOrganisations",
        () -> this.userOrganisationsApi.getUserOrganisations(this.authSession.getSessionToken(), null));
  }

  /**
   * Retrieves the authenticated user's custom directories/folders.
   *
   * @return a list of {@link OrganisationStructure} directories.
   */
  public List<OrganisationStructure> getUserDirectories() {
    return getUserOrganization().getDirectories();
  }

  /**
   * Retrieves the authenticated user's workspaces.
   *
   * @return a list of {@link OrganisationStructure} workspaces.
   */
  public List<OrganisationStructure> getUserWorkspaces() {
    return getUserOrganization().getWorkspaces();
  }

  /**
   * Retrieves the authenticated user's blast distribution lists.
   *
   * @return a list of {@link OrganisationStructure} blast lists.
   */
  public List<OrganisationStructure> getUserDistributionLists() {
    return getUserOrganization().getBlastLists();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(this.authSession);
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, this.apiClient.getBasePath(), supplier);
  }
}
