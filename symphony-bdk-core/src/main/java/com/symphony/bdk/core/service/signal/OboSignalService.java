package com.symphony.bdk.core.service.signal;

import com.symphony.bdk.gen.api.model.BaseSignal;
import com.symphony.bdk.gen.api.model.ChannelSubscriber;
import com.symphony.bdk.gen.api.model.ChannelSubscriptionResponse;
import com.symphony.bdk.gen.api.model.Signal;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service interface exposing OBO-enabled endpoints to manage signals information.
 */
@API(status = API.Status.STABLE)
public interface OboSignalService {

  /**
   * Lists signals on behalf of the user.
   * {@link SignalService#listSignals(Integer, Integer)}
   *
   * @param skip  The number of signals to skip.
   * @param limit Maximum number of signals to return. Default is 50, maximum value is 500.
   * @return List of signals that the user has created and public signals to which they have subscribed.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-signals">List Signals</a>
   */
  List<Signal> listSignals(@Nullable Integer skip, @Nullable Integer limit);

  /**
   * Lists signals on behalf of the user with default limit equal 50.
   * {@link SignalService#listSignals()}
   *
   * @return List of signals that the user has created and public signals to which they have subscribed.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-signals">List Signals</a>
   */
  List<Signal> listSignals();

  /**
   * Lists paginated stream of signals on behalf of the user.
   * {@link SignalService#listSignalsStream(Integer, Integer)}
   *
   * @param chunkSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @param totalSize Total maximum number of signals to return. Optional and defaults to 50.
   * @return a {@link Stream} containing the signals.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-signals">List Signals</a>
   */
  Stream<Signal> listSignalsStream(@Nullable Integer chunkSize, @Nullable Integer totalSize);

  /**
   * Lists paginated stream of signals on behalf of the user with the default chunkSize and totalSize equal 50.
   * {@link SignalService#listSignalsStream()}
   *
   * @return a {@link Stream} containing the signals.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-signals">List Signals</a>
   */
  Stream<Signal> listSignalsStream();

  /**
   * Gets details about the specified signal.
   * {@link SignalService#getSignal(String)}
   *
   * @param id The id of the signal.
   * @return Details of the specified signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-signal">Get Signal</a>
   */
  Signal getSignal(@Nonnull String id);

  /**
   * Create a new signal.
   * {@link SignalService#createSignal(BaseSignal)}
   *
   * @param signal  A signal object to be created.
   * @return A new created signal object.
   * @see <a href="https://developers.symphony.com/restapi/reference#create-signal">Create Signal</a>
   */
  Signal createSignal(@Nonnull BaseSignal signal);

  /**
   * Update an existing signal.
   * {@link SignalService#updateSignal(String, BaseSignal)}
   *
   * @param id      The id of the signal to be updated.
   * @param signal  The signal object to be updated.
   * @return The updated signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-signal">Update Signal</a>
   */
  Signal updateSignal(@Nonnull String id, @Nonnull BaseSignal signal);

  /**
   * Delete an existing signal.
   * {@link SignalService#deleteSignal(String)}
   *
   * @param id  The id of the signal to be deleted.
   * @see <a href="https://developers.symphony.com/restapi/reference#delete-signal">Delete Signal</a>
   */
  void deleteSignal(@Nonnull String id);

  /**
   * Subscribe a list of users to a signal.
   * {@link SignalService#subscribeSignal(String, Boolean, List)}
   *
   * @param id      The id of the signal to be subscribed.
   * @param pushed  Prevents the user from unsubscribing from the Signal (only for bulk subscriptions).
   *                Requires the canManageSignalSubscription entitlement.
   * @param userIds List of user ids to subscribe to the signal
   * @return The subscription information.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribe-signal">Subscribe Signal</a>
   */
  ChannelSubscriptionResponse subscribeSignal(@Nonnull String id, @Nullable Boolean pushed, @Nullable List<Long> userIds);

  /**
   * Unsubscribe a list of users from a signal.
   * {@link SignalService#unsubscribeSignal(String, List)}
   *
   * @param id      The id of the signal to be unsubscribed.
   * @param userIds The list of user ids to unsubscribe from the signal.
   * @return The unsubscription information.
   * @see <a href="https://developers.symphony.com/restapi/reference#unsubscribe-signal">Unsubscribe Signal</a>
   */
  ChannelSubscriptionResponse unsubscribeSignal(@Nonnull String id, @Nullable List<Long> userIds);

  /**
   * Get the subscribers for a specified signal.
   * {@link SignalService#listSubscribers(String, Integer, Integer)}
   *
   * @param id      The id of the specified signal.
   * @param skip    The number of results to skip.
   * @param limit   The maximum number of subscribers to return. If no value is provided, 100 is the default.
   * @return List of subscribers of the signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribers">Subscribers</a>
   */
  List<ChannelSubscriber> listSubscribers(@Nonnull String id, @Nullable Integer skip, @Nullable Integer limit);

  /**
   * Get the subscribers for a specified signal with default limit equal to 100.
   * {@link SignalService#listSubscribers(String)}
   *
   * @param id      The id of the specified signal.
   * @return List of subscribers of the signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribers">Subscribers</a>
   */
  List<ChannelSubscriber> listSubscribers(@Nonnull String id);

  /**
   * Get the paginated stream of subscribers for a specified signal.
   * {@link SignalService#listSubscribersStream(String, Integer, Integer)}
   *
   * @param id        The id of the specified signal.
   * @param chunkSize The size of elements to retrieve in one call. Optional and defaults to 100.
   * @param totalSize Total maximum number of subscribers to return. Optional and defaults to 100.
   * @return a {@link Stream} containing the subscribers.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribers">Subscribers</a>
   */
  Stream<ChannelSubscriber> listSubscribersStream(@Nonnull String id, @Nullable Integer chunkSize, @Nullable Integer totalSize);

  /**
   * Get the paginated stream of subscribers for a specified signal with the default chunkSize and totalSize equal to 100.
   * {@link SignalService#listSubscribersStream(String)}
   *
   * @param id  The id of the specified signal.
   * @return a {@link Stream} containing the subscribers.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribers">Subscribers</a>
   */
  Stream<ChannelSubscriber> listSubscribersStream(@Nonnull String id);
}
