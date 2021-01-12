package com.symphony.bdk.core.service.connection;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.connection.constant.ConnectionStatus;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.model.UserConnection;
import com.symphony.bdk.gen.api.model.UserConnectionRequest;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Service class for managing connection status.
 * <p>
 * This service is used for retrieving the connection status if the calling user with a specified user or with many
 * other internal or external users in the pod, and perform some actions related to the connection status like:
 * <p><ul>
 * <li>Send a connection request to an user</li>
 * <li>Accept a connection request from an user</li>
 * <li>Reject a connection request from an user</li>
 * <li>Remove a connection with an user</li>
 * </ul></p>
 * </p>
 */
@API(status = API.Status.STABLE)
public class ConnectionService implements OboConnectionService, OboService<OboConnectionService> {

  private final ConnectionApi connectionApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ConnectionService(ConnectionApi connectionApi, AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.connectionApi = connectionApi;
    this. authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public ConnectionService(ConnectionApi connectionApi, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.connectionApi = connectionApi;
    this. authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OboConnectionService obo(AuthSession oboSession) {
    return new ConnectionService(connectionApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserConnection getConnection(@Nonnull Long userId) {
    return executeAndRetry("getConnection",
        () -> connectionApi.v1ConnectionUserUserIdInfoGet(authSession.getSessionToken(), String.valueOf(userId)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserConnection> listConnections(@Nonnull ConnectionStatus status, @Nonnull List<Long> userIdList) {
    String userIds = userIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
    return executeAndRetry("listConnection",
        () -> connectionApi.v1ConnectionListGet(authSession.getSessionToken(), status.name(), userIds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserConnection createConnection(@Nonnull Long userId) {
    UserConnectionRequest connectionRequest = new UserConnectionRequest().userId(userId);
    return executeAndRetry("createConnection",
        () -> connectionApi.v1ConnectionCreatePost(authSession.getSessionToken(), connectionRequest));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserConnection acceptConnection(@Nonnull Long userId) {
    UserConnectionRequest connectionRequest = new UserConnectionRequest().userId(userId);
    return executeAndRetry("acceptConnection",
        () -> connectionApi.v1ConnectionAcceptPost(authSession.getSessionToken(), connectionRequest));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserConnection rejectConnection(@Nonnull Long userId) {
    UserConnectionRequest connectionRequest = new UserConnectionRequest().userId(userId);
    return executeAndRetry("rejectConnection",
        () -> connectionApi.v1ConnectionRejectPost(authSession.getSessionToken(), connectionRequest));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeConnection(@Nonnull Long userId) {
    executeAndRetry("removeConnection",
        () -> connectionApi.v1ConnectionUserUidRemovePost(authSession.getSessionToken(), userId));
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies() // to remove refresh on bot session put by default
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
