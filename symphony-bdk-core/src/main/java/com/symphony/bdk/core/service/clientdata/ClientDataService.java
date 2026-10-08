package com.symphony.bdk.core.service.clientdata;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.gen.api.UserOrganisationsApi;
import com.symphony.bdk.gen.api.model.BlastList;
import com.symphony.bdk.gen.api.model.OrganisationStructure;
import com.symphony.bdk.gen.api.model.UserOrganisationsResponse;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Service to retrieve the authenticated user's client data organization structures &mdash;
 * custom folders, multi-chat workspaces, and distribution lists &mdash; from the
 * {@code GET /v5/users/organisations} endpoint.
 */
@API(status = API.Status.STABLE)
public class ClientDataService implements OboClientDataService, OboService<OboClientDataService> {

  private final ApiClient apiClient;
  private final UserOrganisationsApi userOrganisationsApi;
  private final @Nullable AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ClientDataService(ApiClient apiClient, AuthSession authSession,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.userOrganisationsApi = new UserOrganisationsApi(apiClient);
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public ClientDataService(ApiClient apiClient, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.userOrganisationsApi = new UserOrganisationsApi(apiClient);
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  @Override
  public OboClientDataService obo(AuthSession oboSession) {
    return new ClientDataService(this.apiClient, oboSession, this.retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserOrganisationsResponse getClientData() {
    return executeAndRetry("getUserOrganisations",
        () -> this.userOrganisationsApi.getUserOrganisations(this.authSession.getSessionToken(), null));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<OrganisationStructure> getUserDirectories() {
    return getClientData().getDirectories();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<OrganisationStructure> getUserWorkspaces() {
    return getClientData().getWorkspaces();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<BlastList> getUserDistributionLists() {
    return getClientData().getBlastLists();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(this.authSession);
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, this.apiClient.getBasePath(), supplier);
  }
}
