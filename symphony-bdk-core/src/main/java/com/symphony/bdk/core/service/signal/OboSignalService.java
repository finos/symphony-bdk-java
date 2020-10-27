package com.symphony.bdk.core.service.signal;

import com.symphony.bdk.gen.api.model.BaseSignal;
import com.symphony.bdk.gen.api.model.ChannelSubscriber;
import com.symphony.bdk.gen.api.model.ChannelSubscriptionResponse;
import com.symphony.bdk.gen.api.model.Signal;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

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
  List<Signal> listSignals(Integer skip, Integer limit);

  /**
   * Lists signals on behalf of the user.
   * {@link SignalService#listSignalsStream(Integer, Integer)}
   *
   * @param chunkSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @param totalSize size of elements to retrieve in one call. Optional and defaults to 50.
   * @return a {@link Stream} containing the signals.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-signals">List Signals</a>
   */
  Stream<Signal> listSignalsStream(Integer chunkSize, Integer totalSize);

  /**
   * Gets details about the specified signal.
   * {@link SignalService#getSignal(String)}
   *
   * @param id The id of the signal.
   * @return Details of the specified signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-signal">Get Signal</a>
   */
  Signal getSignal(String id);

  /**
   * Create a new signal.
   * {@link SignalService#createSignal(BaseSignal)}
   *
   * @param signal  A signal object to be created.
   * @return A new created signal object.
   * @see <a href="https://developers.symphony.com/restapi/reference#create-signal">Create Signal</a>
   */
  Signal createSignal(BaseSignal signal);

  /**
   * Update an existing signal.
   * {@link SignalService#updateSignal(String, BaseSignal)}
   *
   * @param id      The id of the signal to be updated.
   * @param signal  The signal object to be updated.
   * @return The updated signal.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-signal">Update Signal</a>
   */
  Signal updateSignal(String id, BaseSignal signal);

  /**
   * Delete an existing signal.
   * {@link SignalService#deleteSignal(String)}
   *
   * @param id  The id of the signal to be deleted.
   * @see <a href="https://developers.symphony.com/restapi/reference#delete-signal">Delete Signal</a>
   */
  void deleteSignal(String id);

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
  ChannelSubscriptionResponse subscribeSignal(String id, Boolean pushed, List<Long> userIds);

  /**
   * Unsubscribe a list of users from a signal.
   * {@link SignalService#unsubscribeSignal(String, List)}
   *
   * @param id      The id of the signal to be unsubscribed.
   * @param userIds The list of user ids to unsubscribe from the signal.
   * @return The unsubscription information.
   * @see <a href="https://developers.symphony.com/restapi/reference#unsubscribe-signal">Unsubscribe Signal</a>
   */
  ChannelSubscriptionResponse unsubscribeSignal(String id, List<Long> userIds);

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
  List<ChannelSubscriber> listSubscribers(String id, Integer skip, Integer limit);

  /**
   * Get the subscribers for a specified signal.
   * {@link SignalService#listSubscribersStream(String, Integer, Integer)}
   *
   * @param id        The id of the specified signal.
   * @param chunkSize The size of elements to retrieve in one call. Optional and defaults to 100.
   * @param totalSize The size of elements to retrieve in one call. Optional and defaults to 100.
   * @return a {@link Stream} containing the subscribers.
   * @see <a href="https://developers.symphony.com/restapi/reference#subscribers">Subscribers</a>
   */
  Stream<ChannelSubscriber> listSubscribersStream(String id, Integer chunkSize, Integer totalSize);
}
