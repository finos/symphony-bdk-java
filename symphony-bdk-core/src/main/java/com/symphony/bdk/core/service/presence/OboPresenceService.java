package com.symphony.bdk.core.service.presence;

import com.symphony.bdk.core.service.presence.constant.PresenceStatus;
import com.symphony.bdk.gen.api.model.V2Presence;

import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service interface exposing OBO-enabled endpoints to manage user presence information.
 */
@API(status = API.Status.STABLE)
public interface OboPresenceService {

  /**
   * Get the online status (presence info) of the calling user.
   * {@link PresenceService#getPresence()}
   *
   * @return Presence info of the calling user.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-presence">Get Presence</a>
   */
  V2Presence getPresence();

  /**
   * Get the presence info of all users in a pod.
   * {@link PresenceService#listPresences(Long, Integer)}
   *
   * @param lastUserId  Last user ID retrieved, used for paging. If provided, results skip users with IDs less than
   *                    this parameter.
   * @param limit       Maximum number of records to return. The maximum supported value is 5000.
   * @return List of presence info of all users in a pod.
   * @see <a href="https://developers.symphony.com/restapi/reference#get-all-presence">Get All Presence</a>
   */
  List<V2Presence> listPresences(@Nullable Long lastUserId, @Nullable Integer limit);

  /**
   * Get the presence info of a specified user.
   * {@link PresenceService#getUserPresence(Long, Boolean)}
   *
   * @param userId  User Id
   * @param local   If true then Perform a local query and set the presence to OFFLINE for users who are not local to
   *                the calling user’s pod. If false or absent then query the presence of all local and external users
   *                who are connected to the calling user.
   * @return Presence info of the looked up user.
   * @see <a href="https://developers.symphony.com/restapi/reference#user-presence-v3">Get User Presence</a>
   */
  V2Presence getUserPresence(@Nonnull Long userId, @Nullable Boolean local);

  /**
   * Register interest in a list of external users to get their presence info.
   * {@link PresenceService#externalPresenceInterest(List)}
   *
   * @param userIds List of user ids to be registered.
   * @see <a href="https://developers.symphony.com/restapi/reference#register-user-presence-interest">External Presence Interest</a>
   */
  void externalPresenceInterest(@Nonnull List<Long> userIds);

  /**
   * Set the presence info of the calling user.
   * {@link PresenceService#setPresence(PresenceStatus, Boolean)}
   *
   * @param status  The new presence state for the user.
   *                Possible values are AVAILABLE, BUSY, AWAY, ON_THE_PHONE, BE_RIGHT_BACK, IN_A_MEETING, OUT_OF_OFFICE, OFF_WORK.
   * @param soft    If true, the user's current status is taken into consideration. If the user is currently OFFLINE,
   *                the user's presence will still be OFFLINE, but the new presence will take effect when the
   *                user comes online. If the user is currently online, the user's activity state will be
   *                applied to the presence if applicable. (e.g. if you are setting their presence to AVAILABLE,
   *                but the user is currently idle, their status will be represented as AWAY)
   * @return Presence info of the calling user.
   * @see <a href="https://developers.symphony.com/restapi/reference#set-presence">Set Presence</a>
   */
  V2Presence setPresence(@Nonnull PresenceStatus status, @Nullable Boolean soft);

  /**
   * Creates a new stream capturing online status changes ("presence feed") for the company (pod) and returns the ID of
   * the new feed. The feed will return the presence of users whose presence status has changed since it was last read.
   * {@link PresenceService#createPresenceFeed()}
   *
   * @return Presence feed Id
   * @see <a href="https://developers.symphony.com/restapi/reference#create-presence-feed">Create Presence Feed</a>
   */
  String createPresenceFeed();

  /**
   * Reads the specified presence feed that was created.
   * The feed returned includes the user presence statuses that have changed since they were last read.
   * {@link PresenceService#readPresenceFeed(String)}
   *
   * @param feedId The presence feed id to be read.
   * @return The list of user presences has changed since the last presence read.
   * @see <a href="https://developers.symphony.com/restapi/reference#read-presence-feed">Read Presence Feed</a>
   */
  List<V2Presence> readPresenceFeed(@Nonnull String feedId);

  /**
   * Delete the specified presence feed that was created.
   * {@link PresenceService#deletePresenceFeed(String)}
   *
   * @param feedId The presence feed id to be deleted.
   * @return The id of the deleted presence feed.
   */
  String deletePresenceFeed(@Nonnull String feedId);

  /**
   * Set the presence state of a another user.
   * {@link PresenceService#setUserPresence(Long, PresenceStatus, Boolean)}
   *
   * @param userId  The id of the specified user.
   * @param status  Presence state to set.
   *                Possible values are AVAILABLE, BUSY, AWAY, ON_THE_PHONE, BE_RIGHT_BACK, IN_A_MEETING, OUT_OF_OFFICE, OFF_WORK.
   * @param soft    If true, the user's current status is taken into consideration. If the user is currently OFFLINE,
   *                the user's presence will still be OFFLINE, but the new presence will take effect when the
   *                user comes online. If the user is currently online, the user's activity state will be
   *                applied to the presence if applicable. (e.g. if you are setting their presence to AVAILABLE,
   *                but the user is currently idle, their status will be represented as AWAY)
   * @return The presence info of the specified user.
   */
  V2Presence setUserPresence(@Nonnull Long userId, @Nonnull PresenceStatus status, @Nullable Boolean soft);
}
