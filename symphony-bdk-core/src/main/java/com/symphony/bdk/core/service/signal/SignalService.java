package com.symphony.bdk.core.service.signal;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.pagination.PaginatedApi;
import com.symphony.bdk.core.service.pagination.PaginatedService;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.model.BaseSignal;
import com.symphony.bdk.gen.api.model.ChannelSubscriber;
import com.symphony.bdk.gen.api.model.ChannelSubscriptionResponse;
import com.symphony.bdk.gen.api.model.Signal;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public SignalService(SignalsApi signalsApi, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.signalsApi = signalsApi;
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
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
  public List<Signal> listSignals(@Nonnull PaginationAttribute pagination) {
    return executeAndRetry("listSignals",
        () -> signalsApi.v1SignalsListGet(authSession.getSessionToken(), authSession.getKeyManagerToken(),
            pagination.getSkip(), pagination.getLimit()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Signal> listSignals() {
    return executeAndRetry("listSignals",
        () -> signalsApi.v1SignalsListGet(authSession.getSessionToken(), authSession.getKeyManagerToken(), null, null));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public Stream<Signal> listAllSignals(@Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<Signal> api = (offset, limit) -> listSignals(new PaginationAttribute(offset, limit));
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public Stream<Signal> listAllSignals() {
    PaginatedApi<Signal> api = (offset, limit) -> listSignals(new PaginationAttribute(offset, limit));
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal getSignal(@Nonnull String id) {
    return executeAndRetry("getSignal",
        () -> signalsApi.v1SignalsIdGetGet(authSession.getSessionToken(), id, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal createSignal(@Nonnull BaseSignal signal) {
    return executeAndRetry("createSignal",
        () -> signalsApi.v1SignalsCreatePost(authSession.getSessionToken(), signal, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Signal updateSignal(@Nonnull String id, @Nonnull BaseSignal signal) {
    return executeAndRetry("updateSignal",
        () -> signalsApi.v1SignalsIdUpdatePost(authSession.getSessionToken(), id, signal,
            authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSignal(@Nonnull String id) {
    executeAndRetry("deleteSignal",
        () -> signalsApi.v1SignalsIdDeletePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChannelSubscriptionResponse subscribeUsersToSignal(@Nonnull String id, @Nullable Boolean pushed,
      @Nullable List<Long> userIds) {
    return executeAndRetry("subscribeUsersToSignal",
        () -> signalsApi.v1SignalsIdSubscribePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken(),
            pushed, userIds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChannelSubscriptionResponse unsubscribeUsersFromSignal(@Nonnull String id, @Nullable List<Long> userIds) {
    return executeAndRetry("unsubscribeUsersFromSignal",
        () -> signalsApi.v1SignalsIdUnsubscribePost(authSession.getSessionToken(), id, authSession.getKeyManagerToken(),
            userIds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ChannelSubscriber> listSubscribers(@Nonnull String id, @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("listSubscribers",
        () -> signalsApi.v1SignalsIdSubscribersGet(authSession.getSessionToken(), id, authSession.getKeyManagerToken(),
            pagination.getSkip(), pagination.getLimit())).getData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ChannelSubscriber> listSubscribers(@Nonnull String id) {
    return executeAndRetry("listSubscribers",
        () -> signalsApi.v1SignalsIdSubscribersGet(authSession.getSessionToken(), id, authSession.getKeyManagerToken(),
            null, null)).getData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public Stream<ChannelSubscriber> listAllSubscribers(@Nonnull String id,
      @Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<ChannelSubscriber> api =
        (((offset, limit) -> listSubscribers(id, new PaginationAttribute(offset, limit))));
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public Stream<ChannelSubscriber> listAllSubscribers(@Nonnull String id) {
    PaginatedApi<ChannelSubscriber> api =
        (((offset, limit) -> listSubscribers(id, new PaginationAttribute(offset, limit))));
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(authSession);
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, signalsApi.getApiClient().getBasePath(), supplier);
  }
}
