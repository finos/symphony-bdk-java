package com.symphony.bdk.core.service.stream;

import static com.symphony.bdk.core.service.stream.util.StreamUtil.toUrlSafeId;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.OboService;
import com.symphony.bdk.core.service.pagination.PaginatedApi;
import com.symphony.bdk.core.service.pagination.PaginatedService;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
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
import com.symphony.bdk.gen.api.model.V2AdminStreamInfo;
import com.symphony.bdk.gen.api.model.V2AdminStreamList;
import com.symphony.bdk.gen.api.model.V2MemberInfo;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder)
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  public StreamService(StreamsApi streamsApi, RoomMembershipApi membershipApi, ShareApi shareApi,
      RetryWithRecoveryBuilder<?> retryBuilder) {
    this.streamsApi = streamsApi;
    this.roomMembershipApi = membershipApi;
    this.shareApi = shareApi;
    this.authSession = null;
    this.retryBuilder = RetryWithRecoveryBuilder.copyWithoutRecoveryStrategies(retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OboStreamService obo(AuthSession oboSession) {
    return new StreamService(streamsApi, roomMembershipApi, shareApi, oboSession, retryBuilder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2StreamAttributes getStream(@Nonnull String streamId) {
    return executeAndRetry("getStreamInfo", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v2StreamsSidInfoGet(toUrlSafeId(streamId), authSession.getSessionToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<StreamAttributes> listStreams(@Nullable StreamFilter filter) {
    return executeAndRetry("listStreams", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<StreamAttributes> listStreams(@Nullable StreamFilter filter, @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("listStreams", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1StreamsListPost(authSession.getSessionToken(), pagination.getSkip(), pagination.getLimit(),
            filter));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter) {
    PaginatedApi<StreamAttributes> api = (offset, limit) -> listStreams(filter, new PaginationAttribute(offset, limit));
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter,
      @Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<StreamAttributes> api = (offset, limit) -> listStreams(filter, new PaginationAttribute(offset, limit));
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMemberToRoom(@Nonnull Long userId, @Nonnull String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("addMemberToRoom", roomMembershipApi.getApiClient().getBasePath(),
        () -> roomMembershipApi.v1RoomIdMembershipAddPost(toUrlSafeId(roomId), authSession.getSessionToken(), user));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeMemberFromRoom(@Nonnull Long userId, @Nonnull String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("removeMemberFrom", roomMembershipApi.getApiClient().getBasePath(),
        () -> roomMembershipApi.v1RoomIdMembershipRemovePost(toUrlSafeId(roomId), authSession.getSessionToken(), user));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V2Message share(@Nonnull String streamId, @Nonnull ShareContent content) {
    return executeAndRetry("share", shareApi.getApiClient().getBasePath(),
        () -> shareApi.v3StreamSidSharePost(toUrlSafeId(streamId), authSession.getSessionToken(), content,
            authSession.getKeyManagerToken()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void promoteUserToRoomOwner(@Nonnull Long userId, @Nonnull String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("promoteUserToOwner", roomMembershipApi.getApiClient().getBasePath(),
        () -> roomMembershipApi.v1RoomIdMembershipPromoteOwnerPost(toUrlSafeId(roomId), authSession.getSessionToken(),
            user));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void demoteUserToRoomParticipant(@Nonnull Long userId, @Nonnull String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("demoteUserToParticipant", roomMembershipApi.getApiClient().getBasePath(),
        () -> roomMembershipApi.v1RoomIdMembershipDemoteOwnerPost(toUrlSafeId(roomId), authSession.getSessionToken(),
            user));
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

  public Stream create(@Nonnull List<Long> uids) {
    return executeAndRetry("createStreamByUserIds", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1ImCreatePost(authSession.getSessionToken(), uids));
  }

  /**
   * {@link StreamService#create(List)}
   *
   * @param uids User ids of the participant
   * @return The created IM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim">Create IM or MIM</a>
   */
  public Stream create(@Nonnull Long... uids) {
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
  public V3RoomDetail create(@Nonnull V3RoomAttributes roomAttributes) {
    return executeAndRetry("createStream", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v3RoomCreatePost(authSession.getSessionToken(), roomAttributes));
  }

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query The room searching criteria
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  public V3RoomSearchResults searchRooms(@Nonnull V2RoomSearchCriteria query) {
    return executeAndRetry("searchRooms", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v3RoomSearchPost(authSession.getSessionToken(), query, null, null));
  }

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query      The room searching criteria.
   * @param pagination The skip and limit for pagination.
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  public V3RoomSearchResults searchRooms(@Nonnull V2RoomSearchCriteria query, @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("searchRooms", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v3RoomSearchPost(authSession.getSessionToken(), query, pagination.getSkip(),
            pagination.getLimit()));
  }

  /**
   * Search rooms and return in a {@link java.util.stream.Stream} according to the specified criteria.
   *
   * @param query The room searching criteria.
   * @return A {@link java.util.stream.Stream} of rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V3RoomDetail> searchAllRooms(@Nonnull V2RoomSearchCriteria query) {
    PaginatedApi<V3RoomDetail> api =
        (offset, limit) -> searchRooms(query, new PaginationAttribute(offset, limit)).getRooms();
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  /**
   * Search rooms and return in a {@link java.util.stream.Stream} according to the specified criteria.
   *
   * @param query      The room searching criteria.
   * @param pagination The chunkSize and totalSize for stream pagination.
   * @return A {@link java.util.stream.Stream} of rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V3RoomDetail> searchAllRooms(@Nonnull V2RoomSearchCriteria query,
      @Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<V3RoomDetail> api =
        (offset, limit) -> searchRooms(query, new PaginationAttribute(offset, limit)).getRooms();
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * Get information about a particular room.
   *
   * @param roomId The room id.
   * @return The information about the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-info-v3">Room Info V3</a>
   */
  public V3RoomDetail getRoomInfo(@Nonnull String roomId) {
    return executeAndRetry("getRoomInfo", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v3RoomIdInfoGet(toUrlSafeId(roomId), authSession.getSessionToken()));
  }

  /**
   * Deactivate or reactivate a chatroom. At the creation, the chatroom is activated by default.
   *
   * @param roomId The room id
   * @param active Deactivate or activate
   * @return The information of the room after being deactivated or reactivated.
   * @see <a href="https://developers.symphony.com/restapi/reference#de-or-re-activate-room">De/Reactivate Room</a>
   */
  public RoomDetail setRoomActive(@Nonnull String roomId, @Nonnull Boolean active) {
    return executeAndRetry("setRoomActive", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1RoomIdSetActivePost(toUrlSafeId(roomId), active, authSession.getSessionToken()));
  }

  /**
   * Update the attributes of an existing chatroom.
   *
   * @param roomId         The id of the room to be updated
   * @param roomAttributes The attributes to be updated to the room
   * @return The information of the room after being updated.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-room-v3">Update Room V3</a>
   */
  public V3RoomDetail updateRoom(@Nonnull String roomId, @Nonnull V3RoomAttributes roomAttributes) {
    return executeAndRetry("updateRoom", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v3RoomIdUpdatePost(toUrlSafeId(roomId), authSession.getSessionToken(), roomAttributes));
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
  public Stream createInstantMessageAdmin(@Nonnull List<Long> uids) {
    return executeAndRetry("createInstantMessageAdmin", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1AdminImCreatePost(authSession.getSessionToken(), uids));
  }

  /**
   * Deactivate or reactivate a chatroom via AC Portal.
   *
   * @param streamId The stream id
   * @param active   Deactivate or activate
   * @return The information of the room after being deactivated or reactivated.
   */
  public RoomDetail setRoomActiveAdmin(@Nonnull String streamId, @Nonnull Boolean active) {
    return executeAndRetry("setRoomActiveAdmin", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1AdminRoomIdSetActivePost(toUrlSafeId(streamId), active, authSession.getSessionToken()));
  }

  /**
   * Retrieve all the streams across the enterprise.
   *
   * @param filter The stream searching filter
   * @return List of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  public V2AdminStreamList listStreamsAdmin(@Nullable V2AdminStreamFilter filter) {
    return executeAndRetry("listStreamsAdmin", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v2AdminStreamsListPost(authSession.getSessionToken(), null, null, filter));
  }

  /**
   * Retrieve all the streams across the enterprise.
   *
   * @param filter     The stream searching filter
   * @param pagination The skip and limit for pagination.
   * @return List of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  public V2AdminStreamList listStreamsAdmin(@Nullable V2AdminStreamFilter filter,
      @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("listStreamsAdmin", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v2AdminStreamsListPost(authSession.getSessionToken(), pagination.getSkip(),
            pagination.getLimit(), filter));
  }

  /**
   * Retrieve all the streams across the enterprise and return in a {@link java.util.stream.Stream} with default chunk size and total size equals 100.
   *
   * @param filter The stream searching filter
   * @return List of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V2AdminStreamInfo> listAllStreamsAdmin(@Nullable V2AdminStreamFilter filter) {
    PaginatedApi<V2AdminStreamInfo> api =
        (offset, limit) -> listStreamsAdmin(filter, new PaginationAttribute(offset, limit)).getStreams();
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  /**
   * Retrieve all the streams across the enterprise and return in a {@link java.util.stream.Stream}.
   *
   * @param filter     The stream searching filter
   * @param pagination The chunkSize and totalSize for stream pagination.
   * @return A {@link java.util.stream.Stream} of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V2AdminStreamInfo> listAllStreamsAdmin(@Nullable V2AdminStreamFilter filter,
      @Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<V2AdminStreamInfo> api =
        (offset, limit) -> listStreamsAdmin(filter, new PaginationAttribute(offset, limit)).getStreams();
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * List the current members of an existing stream.
   * The stream can be of type IM, MIM, or ROOM.
   *
   * @param streamId The stream id
   * @return List of member in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-members">Stream Members</a>
   */
  public V2MembershipList listStreamMembers(@Nonnull String streamId) {
    return executeAndRetry("listStreamMembers", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1AdminStreamIdMembershipListGet(toUrlSafeId(streamId), authSession.getSessionToken(), null,
            null));
  }

  /**
   * List the current members of an existing stream.
   * The stream can be of type IM, MIM, or ROOM.
   *
   * @param streamId   The stream id
   * @param pagination The skip and limit for pagination.
   * @return List of member in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-members">Stream Members</a>
   */
  public V2MembershipList listStreamMembers(@Nonnull String streamId, @Nonnull PaginationAttribute pagination) {
    return executeAndRetry("listStreamMembers", streamsApi.getApiClient().getBasePath(),
        () -> streamsApi.v1AdminStreamIdMembershipListGet(toUrlSafeId(streamId), authSession.getSessionToken(),
            pagination.getSkip(), pagination.getLimit()));
  }

  /**
   * List the current members of an existing room and return in a {@link java.util.stream.Stream} with default chunk size and total size equals 100.
   * The stream can be of type IM, MIM, or ROOM.
   *
   * @param streamId The stream id
   * @return A {@link java.util.stream.Stream} of members in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-members">Stream Members</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V2MemberInfo> listAllStreamMembers(@Nonnull String streamId) {
    PaginatedApi<V2MemberInfo> api =
        (offset, limit) -> listStreamMembers(toUrlSafeId(streamId),
            new PaginationAttribute(offset, limit)).getMembers();
    return new PaginatedService<>(api, PaginatedService.DEFAULT_PAGINATION_CHUNK_SIZE,
        PaginatedService.DEFAULT_PAGINATION_TOTAL_SIZE).stream();
  }

  /**
   * List the current members of an existing room and return in a {@link java.util.stream.Stream}.
   * The stream can be of type IM, MIM, or ROOM.
   *
   * @param streamId   The stream id
   * @param pagination The chunkSize and totalSize for stream pagination with default value equal 100.
   * @return A {@link java.util.stream.Stream} of members in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-members">Stream Members</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  public java.util.stream.Stream<V2MemberInfo> listAllStreamMembers(@Nonnull String streamId,
      @Nonnull StreamPaginationAttribute pagination) {
    PaginatedApi<V2MemberInfo> api =
        (offset, limit) -> listStreamMembers(toUrlSafeId(streamId),
            new PaginationAttribute(offset, limit)).getMembers();
    return new PaginatedService<>(api, pagination.getChunkSize(), pagination.getTotalSize()).stream();
  }

  /**
   * Lists the current members of an existing room.
   *
   * @param roomId The room stream id
   * @return List of members in the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-members">Room Members</a>
   */
  public List<MemberInfo> listRoomMembers(@Nonnull String roomId) {
    return executeAndRetry("listRoomMembers", roomMembershipApi.getApiClient().getBasePath(),
        () -> roomMembershipApi.v2RoomIdMembershipListGet(toUrlSafeId(roomId), authSession.getSessionToken()));
  }


  private <T> T executeAndRetry(String name, String address, SupplierWithApiException<T> supplier) {
    checkAuthSession(authSession);
    return RetryWithRecovery.executeAndRetry(retryBuilder, name, address, supplier);
  }
}
