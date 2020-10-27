package com.symphony.bdk.core.service.signal;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.pagination.PaginatedApi;
import com.symphony.bdk.core.service.pagination.PaginatedService;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.model.BaseSignal;
import com.symphony.bdk.gen.api.model.ChannelSubscriber;
import com.symphony.bdk.gen.api.model.ChannelSubscriptionResponse;
import com.symphony.bdk.gen.api.model.Signal;

import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service class for managing signal information.
 * <p>
 * This service is used for listing signals related to the user, get information of a specified signal
 * or perform some actions related to the signal like:
 * <p><ul>
 * <li>Create a signal</li>
 * <li>Update a signal</li>
 * <li>Delete a signal</li>
 * <li>Subscribe or unsubscribe a signal</li>
 * <li></li>
 * </ul></p>
 */
@API(status = API.Status.STABLE)
public class SignalService implements OboSignalService, OboService<OboSignalService> {

  private final SignalsApi signalsApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public SignalService(SignalsApi signalsApi, AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.signalsApi = signalsApi;
    this.authSession = authSession;
    this.retryBuilder = retryBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OboSignalService obo(AuthSession oboSession) {
    return new SignalService(signalsApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Signal> listSignals(Integer skip, Integer limit) {
    return executeAndRetry("listSignals",
        () -> signalsApi.v1SignalsListGet(authSession.getSessionToken(), authSession.getKeyManagerToken(), skip, limit));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Stream<Signal> listSignalsStream(Integer chunkSize, Integer totalSize) {
    PaginatedApi<Signal> api = ((this::listSignals));

    final int actualChunkSize = chunkSize == null ? 50 : chunkSize;
    final int actualTotalSize = totalSize == null ? 50 : totalSize;

    return new PaginatedService<>(api, actualChunkSize, actualTotalSize).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal getSignal(String id) {
    return executeAndRetry("getSignal",
        () -> signalsApi.v1SignalsIdGetGet(authSession.getSessionToken(), id, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal createSignal(BaseSignal signal) {
    return executeAndRetry("createSignal",
        () -> signalsApi.v1SignalsCreatePost(authSession.getSessionToken(), signal, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal updateSignal(String id, BaseSignal signal) {
    return executeAndRetry("updateSignal",
        () -> signalsApi.v1SignalsIdUpdatePost(authSession.getSessionToken(), id, signal, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSignal(String id) {
    executeAndRetry("deleteSignal",
        () -> signalsApi.v1SignalsIdDeletePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChannelSubscriptionResponse subscribeSignal(String id, Boolean pushed, List<Long> userIds) {
    return executeAndRetry("subscribeSignal",
        () -> signalsApi.v1SignalsIdSubscribePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken(), pushed, userIds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChannelSubscriptionResponse unsubscribeSignal(String id, List<Long> userIds) {
    return executeAndRetry("unsubscribeSignal",
        () -> signalsApi.v1SignalsIdUnsubscribePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken(), userIds));
  }

  /**
   * {@inheritDoc}
   * @return
   */
  @Override
  public List<ChannelSubscriber> subscribers(String id, Integer skip, Integer limit) {
    return executeAndRetry("subscribers",
        () -> signalsApi.v1SignalsIdSubscribersGet(authSession.getSessionToken(), id, authSession.getKeyManagerToken(), skip, limit)).getData();
  }

  /**
   * {@inheritDoc}
   * @return
   */
  @Override
  public Stream<ChannelSubscriber> subscribersStream(String id, Integer chunkSize, Integer totalSize) {
    PaginatedApi<ChannelSubscriber> api = (((offset, limit) -> subscribers(id, offset, limit)));

    final int actualChunkSize = chunkSize == null ? 100 : chunkSize;
    final int actualTotalSize = totalSize == null ? 100 : totalSize;

    return new PaginatedService<>(api, actualChunkSize, actualTotalSize).stream();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies()
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
