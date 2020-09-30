package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.RoomDetail;
import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamList;
import com.symphony.bdk.gen.api.model.V2MembershipList;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V3RoomSearchResults;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;

/**
 * Service class for managing streams.
 * <p>
 * This service is used for retrieving information about a particular stream or
 * chatroom, searching streams, listing members, attachments of a particular stream,
 * perform some action related to a stream like:
 * <p><ul>
 * <li>Create a IM or MIM</li>
 * <li>Create a chatroom</li>
 * <li>Activate or Deactivate a chatroom</li>
 * <li></li>
 * </ul></p>
 */
@Slf4j
@API(status = API.Status.STABLE)
public class StreamService extends OboStreamService {

  private final AuthSession authSession;

  public StreamService(StreamsApi streamsApi, RoomMembershipApi membershipApi, ShareApi shareApi,
      AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    super(streamsApi, membershipApi, shareApi, retryBuilder);
    this.authSession = authSession;
  }

  /**
   * Create a new single or multi party instant message conversation between the caller and specified users.
   * <p>
   * The caller is implicitly included in the members of the created chat.
   * <p>
   * Duplicate users will be included in the membership of the chat but
   * the duplication will be silently ignored.
   * <p>
   * If there is an existing IM conversation with the same set of participants then
   * the id of that existing stream will be returned.
   * <p>
   * If the given list of user ids contains only one id, an IM will be created, otherwise, a MIM will be created.
   *
   * @param uids List of user ids of the participants.
   * @return The created IM or MIM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim">Create IM or MIM</a>
   */
  public Stream create(List<Long> uids) {
    try {
      return streamsApi.v1ImCreatePost(authSession.getSessionToken(), uids);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * {@link StreamService#create(List)}
   *
   * @param uids User ids of the participant
   * @return The created IM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim">Create IM or MIM</a>
   */
  public Stream create(Long... uids) {
    return this.create(Arrays.asList(uids));
  }

  /**
   * Create a new chatroom.
   * If no  attributes are specified, the room is created as a private chatroom.
   *
   * @param roomAttributes Attributes of the created room
   * @return The created chatroom
   * @see <a href="https://developers.symphony.com/restapi/reference#create-room-v3">Create Room V3</a>
   */
  public V3RoomDetail create(V3RoomAttributes roomAttributes) {
    return executeAndRetry("createStream",
        () -> streamsApi.v3RoomCreatePost(authSession.getSessionToken(), roomAttributes));
  }

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query The room searching criteria
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  public V3RoomSearchResults searchRooms(V2RoomSearchCriteria query) {
    return executeAndRetry("searchRooms",
        () -> streamsApi.v3RoomSearchPost(authSession.getSessionToken(), query, null, null));
  }

  /**
   * Get information about a particular room.
   *
   * @param roomId The room id.
   * @return The information about the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-info-v3">Room Info V3</a>
   */
  public V3RoomDetail getRoomInfo(String roomId) {
    return executeAndRetry("getRoomInfo",
        () -> streamsApi.v3RoomIdInfoGet(roomId, authSession.getSessionToken()));
  }

  /**
   * Deactivate or reactivate a chatroom. At the creation, the chatroom is activated by default.
   *
   * @param roomId The room id
   * @param active Deactivate or activate
   * @return The information of the room after being deactivated or reactivated.
   * @see <a href="https://developers.symphony.com/restapi/reference#de-or-re-activate-room">De/Reactivate Room</a>
   */
  public RoomDetail setRoomActive(String roomId, Boolean active) {
    return executeAndRetry("setRoomActive",
        () -> streamsApi.v1RoomIdSetActivePost(roomId, active, authSession.getSessionToken()));
  }

  /**
   * Update the attributes of an existing chatroom.
   *
   * @param roomId         The id of the room to be updated
   * @param roomAttributes The attributes to be updated to the room
   * @return The information of the room after being updated.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-room-v3">Update Room V3</a>
   */
  public V3RoomDetail updateRoom(String roomId, V3RoomAttributes roomAttributes) {
    return executeAndRetry("updateRoom",
        () -> streamsApi.v3RoomIdUpdatePost(roomId, authSession.getSessionToken(), roomAttributes));
  }

  /**
   * Retrieve a list of all streams of which the requesting user is a member,
   * sorted by creation date (ascending).
   *
   * @param filter The stream searching criteria
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  public List<StreamAttributes> listStreams(StreamFilter filter) {
    return this.listStreams(this.authSession, filter);
  }

  /**
   * Get information about a particular stream.
   *
   * @param streamId The stream id
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  public V2StreamAttributes getStreamInfo(String streamId) {
    return this.getStreamInfo(this.authSession, streamId);
  }

  /**
   * Create a new single or multi party instant message conversation.
   * At least two user IDs must be provided or an error response will be sent.
   * <p>
   * The caller is not included in the members of the created chat.
   * <p>
   * Duplicate users will be included in the membership of the chat but the
   * duplication will be silently ignored.
   * <p>
   * If there is an existing IM conversation with the same set of participants then
   * the id of that existing stream will be returned.
   *
   * @param uids List of user IDs of participants. At least two user IDs must be provided
   * @return The created IM or MIM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim-admin">Create IM or MIM Non-inclusive</a>
   */
  public Stream createInstantMessageAdmin(List<Long> uids) {
    return executeAndRetry("createInstantMessageAdmin",
        () -> streamsApi.v1AdminImCreatePost(authSession.getSessionToken(), uids));
  }

  /**
   * Deactivate or reactivate a chatroom via AC Portal.
   *
   * @param streamId The stream id
   * @param active   Deactivate or activate
   * @return The information of the room after being deactivated or reactivated.
   */
  public RoomDetail setRoomActiveAdmin(String streamId, Boolean active) {
    return executeAndRetry("setRoomActiveAdmin",
        () -> streamsApi.v1AdminRoomIdSetActivePost(streamId, active, authSession.getSessionToken()));
  }

  /**
   * Retrieve all the streams across the enterprise.
   *
   * @param filter The stream searching filter
   * @return List of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  public V2AdminStreamList listStreamsAdmin(V2AdminStreamFilter filter) {
    return executeAndRetry("listStreamsAdmin",
        () -> streamsApi.v2AdminStreamsListPost(authSession.getSessionToken(), null, null, filter));
  }

  /**
   * List the current members of an existing stream.
   * The stream can be of type IM, MIM, or ROOM.
   *
   * @param streamId The stream id
   * @return List of member in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-members">Stream Members</a>
   */
  public V2MembershipList listStreamMembers(String streamId) {
    return executeAndRetry("listStreamMembers",
        () -> streamsApi.v1AdminStreamIdMembershipListGet(streamId, authSession.getSessionToken(), null, null));
  }

  /**
   * Lists the current members of an existing room.
   *
   * @param roomId The room stream id
   * @return List of members in the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-members">Room Members</a>
   */
  public List<MemberInfo> listRoomMembers(String roomId) {
    return executeAndRetry("listRoomMembers",
        () -> roomMembershipApi.v2RoomIdMembershipListGet(roomId, authSession.getSessionToken()));
  }

  /**
   * Adds a new member to an existing room.
   *
   * @param userId The id of the user to be added to the given room
   * @param roomId The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#add-member">Add Member</a>
   */
  public void addMemberToRoom(Long userId, String roomId) {
    this.addMemberToRoom(this.authSession, userId, roomId);
  }

  /**
   * Removes an existing member from an existing room.
   *
   * @param userId The id of the user to be removed from the given room
   * @param roomId The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-member">Remove Member</a>
   */
  public void removeMemberFromRoom(Long userId, String roomId) {
    this.removeMemberFromRoom(this.authSession, userId, roomId);
  }

  /**
   * Share third-party content, such as a news article, into the specified stream.
   * The stream can be a chat room, an IM, or an MIM.
   *
   * @param streamId The stream id.
   * @param content  The third-party {@link ShareContent} to be shared.
   * @return Message contains share content
   * @see <a href="https://developers.symphony.com/restapi/reference#share-v3">Share</a>
   */
  public V2Message share(String streamId, ShareContent content) {
    return this.share(this.authSession, streamId, content);
  }

  /**
   * Promotes user to owner of the chat room.
   *
   * @param userId The id of the user to be promoted to room owner.
   * @param roomId The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#promote-owner">Promote Owner</a>
   */
  public void promoteUserToRoomOwner(Long userId, String roomId) {
    this.promoteUserToRoomOwner(this.authSession, userId, roomId);
  }

  /**
   * Demotes room owner to a participant in the chat room.
   *
   * @param userId The id of the user to be demoted to room participant.
   * @param roomId The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#demote-owner">Demote Owner</a>
   */
  public void demoteUserToRoomParticipant(Long userId, String roomId) {
    this.demoteUserToRoomParticipant(this.authSession, userId, roomId);
  }

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, supplier);
  }
}
