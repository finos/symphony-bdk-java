package com.symphony.bdk.core.service.presence;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.presence.constant.PresenceStatus;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.PresenceApi;
import com.symphony.bdk.gen.api.model.V2Presence;
import com.symphony.bdk.gen.api.model.V2PresenceStatus;
import com.symphony.bdk.gen.api.model.V2UserPresence;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service class for managing user presence information.
 * <p>
 * This service is used for retrieving information about the presence of the calling user,
 * a specified user or all users in the pod, and perform some actions related to the user presence information like:
 * <p><ul>
 * <li>Set Presence to calling user</li>
 * <li>Set Presence to a specified user</li>
 * <li>Create a presence feed</li>
 * <li>Read a created presence feed</li>
 * <li>Delete a created presence feed</li>
 * <li></li>
 * </ul></p>
 */
@API(status = API.Status.STABLE)
public class PresenceService implements OboPresenceService, OboService<OboPresenceService> {

  private final PresenceApi presenceApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public PresenceService(PresenceApi presenceApi, AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.presenceApi = presenceApi;
    this.authSession = authSession;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public PresenceService(PresenceApi presenceApi, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.presenceApi = presenceApi;
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OboPresenceService obo(AuthSession oboSession) {
    return new PresenceService(presenceApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2Presence getPresence() {
    return executeAndRetry("getPresence",
        () -> presenceApi.v2UserPresenceGet(authSession.getSessionToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<V2Presence> listPresences(@Nullable Long lastUserId, @Nullable Integer limit) {
    return executeAndRetry("listPresences",
        () -> presenceApi.v2UsersPresenceGet(authSession.getSessionToken(), lastUserId, limit));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2Presence getUserPresence(@Nonnull Long userId, @Nullable Boolean local) {
    return executeAndRetry("getUserPresence",
        () -> presenceApi.v3UserUidPresenceGet(userId, authSession.getSessionToken(), local));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void externalPresenceInterest(@Nonnull List<Long> userIds) {
    executeAndRetry("externalPresenceInterest",
        () -> presenceApi.v1UserPresenceRegisterPost(authSession.getSessionToken(), userIds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2Presence setPresence(@Nonnull PresenceStatus status, @Nullable Boolean soft) {
    V2PresenceStatus presenceStatus = new V2PresenceStatus().category(status.name());
    return executeAndRetry("setPresence",
        () -> presenceApi.v2UserPresencePost(authSession.getSessionToken(), presenceStatus, soft));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createPresenceFeed() {
    return executeAndRetry("createPresenceFeed",
        () -> presenceApi.v1PresenceFeedCreatePost(authSession.getSessionToken())).getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<V2Presence> readPresenceFeed(@Nonnull String feedId) {
    return executeAndRetry("readPresenceFeed",
        () -> presenceApi.v1PresenceFeedFeedIdReadGet(authSession.getSessionToken(), feedId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String deletePresenceFeed(@Nonnull String feedId) {
    return executeAndRetry("deletePresenceFeed",
        () -> presenceApi.v1PresenceFeedFeedIdDeletePost(authSession.getSessionToken(), feedId)).getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2Presence setUserPresence(@Nonnull Long userId, @Nonnull PresenceStatus status, @Nullable Boolean soft) {
    V2UserPresence userPresence = new V2UserPresence().userId(userId).category(status.name());
    return executeAndRetry("setUserPresence",
        () -> presenceApi.v3UserPresencePost(authSession.getSessionToken(), userPresence, soft));
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    checkAuthSession(authSession);
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, presenceApi.getApiClient().getBasePath(), supplier);
  }
}
