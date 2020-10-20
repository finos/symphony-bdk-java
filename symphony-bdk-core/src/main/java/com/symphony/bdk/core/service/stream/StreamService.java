package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
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
import com.symphony.bdk.gen.api.model.UserId;
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
public class StreamService implements OboStreamService, OboService<OboStreamService> {

  private final StreamsApi streamsApi;
  private final RoomMembershipApi roomMembershipApi;
  private final ShareApi shareApi;
  private final AuthSession authSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public StreamService(StreamsApi streamsApi, RoomMembershipApi membershipApi, ShareApi shareApi,
      AuthSession authSession, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.streamsApi = streamsApi;
    this.roomMembershipApi = membershipApi;
    this.shareApi = shareApi;
    this.authSession = authSession;
    this.retryBuilder = retryBuilder;
  }

  @Override
  /**
   * {@inheritDoc}
   */
  public OboStreamService obo(AuthSession oboSession) {
    return new StreamService(streamsApi, roomMembershipApi, shareApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  public V2StreamAttributes getStreamInfo(String streamId) {
    return executeAndRetry("getStreamInfo",
        () -> streamsApi.v2StreamsSidInfoGet(streamId, authSession.getSessionToken()));
  }

  /**
   * {@inheritDoc}
   */
  public List<StreamAttributes> listStreams(StreamFilter filter) {
    return executeAndRetry("listStreams",
        () -> streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter));
  }

  /**
   * {@inheritDoc}
   */
  public void addMemberToRoom( Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("addMemberToRoom",
        () -> roomMembershipApi.v1RoomIdMembershipAddPost(roomId, authSession.getSessionToken(), user));
  }

  /**
   * {@inheritDoc}
   */
  public void removeMemberFromRoom(Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("removeMemberFrom",
        () -> roomMembershipApi.v1RoomIdMembershipRemovePost(roomId, authSession.getSessionToken(), user));
  }

  /**
   * {@inheritDoc}
   */
  public V2Message share(String streamId, ShareContent content) {
    return executeAndRetry("share",
        () -> shareApi.v3StreamSidSharePost(streamId, authSession.getSessionToken(), content, authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  public void promoteUserToRoomOwner(Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("promoteUserToOwner",
        () -> roomMembershipApi.v1RoomIdMembershipPromoteOwnerPost(roomId, authSession.getSessionToken(), user));
  }

  /**
   * {@inheritDoc}
   */
  public void demoteUserToRoomParticipant(Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("demoteUserToParticipant",
        () -> roomMembershipApi.v1RoomIdMembershipDemoteOwnerPost(roomId, authSession.getSessionToken(), user));
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
    return executeAndRetry("createStreamByUserIds",
        () -> streamsApi.v1ImCreatePost(authSession.getSessionToken(), uids));
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

  private <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies() // to remove refresh on bot session put by default
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
