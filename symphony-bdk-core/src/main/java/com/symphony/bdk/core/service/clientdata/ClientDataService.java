package com.symphony.bdk.core.service.clientdata;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.gen.api.ClientDataApi;
import com.symphony.bdk.gen.api.model.ClientDataResponse;
import com.symphony.bdk.gen.api.model.DistributionLists;
import com.symphony.bdk.gen.api.model.ClientDataStructure;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Service to retrieve the authenticated user's client data organization structures &mdash;
 * custom folders, multi-chat workspaces, and distribution lists &mdash; from the
 * {@code GET /v5/users/clientdata} endpoint.
 */
@API(status = API.Status.STABLE)
public class ClientDataService implements OboClientDataService, OboService<OboClientDataService> {

  private final ApiClient apiClient;
  private final ClientDataApi clientDataApi;
  private final @Nullable AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ClientDataService(ApiClient apiClient, AuthSession authSession,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.clientDataApi = new ClientDataApi(apiClient);
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public ClientDataService(ApiClient apiClient, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.apiClient = apiClient;
    this.clientDataApi = new ClientDataApi(apiClient);
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
  public ClientDataResponse getClientData() {
    return executeAndRetry("getClientData",
        () -> this.clientDataApi.getClientData(this.authSession.getSessionToken(), null));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ClientDataStructure> getUserFolders() {
    return getClientData().getFolders();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ClientDataStructure> getUserWorkspaces() {
    return getClientData().getWorkspaces();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<DistributionLists> getUserDistributionLists() {
    return getClientData().getDistributionLists();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(this.authSession);
    return RetryWithRecovery.executeAndRetry(this.retryBuilder, name, this.apiClient.getBasePath(), supplier);
  }
}
