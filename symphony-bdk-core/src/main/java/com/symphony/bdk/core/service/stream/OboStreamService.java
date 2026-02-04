package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V3RoomSearchResults;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Service interface exposing OBO-enabled endpoints to manage streams.
 */
@API(status = API.Status.STABLE)
public interface OboStreamService {

  /**
   * {@link StreamService#create(List)}
   *
   * @param uids User ids of the participant
   * @return The created IM
   * @see <a href="https://developers.symphony.com/restapi/reference/create-im-or-mim">Create IM or MIM</a>
   */
   Stream create(@Nonnull Long... uids);

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
   * @see <a href="https://developers.symphony.com/restapi/reference/create-im-or-mim">Create IM or MIM</a>
   */
  Stream create(@Nonnull List<Long> uids);

  /**
   * Create a new chatroom.
   * If no  attributes are specified, the room is created as a private chatroom.
   *
   * @param roomAttributes Attributes of the created room
   * @return The created chatroom
   * @see <a href="https://developers.symphony.com/restapi/reference/create-room-v3">Create Room V3</a>
   */
  V3RoomDetail create(@Nonnull V3RoomAttributes roomAttributes);

  /**
   * {@link StreamService#getStream(String)}
   *
   * @param streamId    The stream id.
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference/stream-info-v2">Stream Info V2</a>
   */
  V2StreamAttributes getStream(@Nonnull String streamId);

  /**
   * Get information about a particular room.
   *
   * @param roomId The room id.
   * @return The information about the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-info-v3">Room Info V3</a>
   */
  V3RoomDetail getRoomInfo(@Nonnull String roomId);

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param filter      The stream searching criteria.
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/list-user-streams">List Streams</a>
   */
  List<StreamAttributes> listStreams(@Nullable StreamFilter filter);

  /**
   * {@link StreamService#listStreams(StreamFilter, PaginationAttribute)}
   *
   * @param filter      The stream searching criteria.
   * @param pagination  The skip and limit for pagination.
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/list-user-streams">List Streams</a>
   */
  List<StreamAttributes> listStreams(@Nullable StreamFilter filter, @Nonnull PaginationAttribute pagination);

  /**
   * {@link StreamService#listAllStreams(StreamFilter)}
   *
   * @param filter The stream searching criteria.
   * @return a {@link java.util.stream.Stream} of matching streams according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/list-user-streams">List Streams</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  java.util.stream.Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter);

  /**
   * {@link StreamService#listAllStreams(StreamFilter, StreamPaginationAttribute)}
   *
   * @param filter      The stream searching criteria.
   * @param pagination  The chunkSize and totalSize for pagination.
   * @return a {@link java.util.stream.Stream} of matching streams according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/list-user-streams">List Streams</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  java.util.stream.Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter,
      @Nonnull StreamPaginationAttribute pagination);

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query The room searching criteria
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/search-rooms-v3">Search Rooms V3</a>
   */
  V3RoomSearchResults searchRooms(@Nonnull V2RoomSearchCriteria query);

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query      The room searching criteria.
   * @param pagination The skip and limit for pagination.
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/search-rooms-v3">Search Rooms V3</a>
   */
  V3RoomSearchResults searchRooms(@Nonnull V2RoomSearchCriteria query, @Nonnull PaginationAttribute pagination);

  /**
   * Search rooms and return in a {@link java.util.stream.Stream} according to the specified criteria.
   *
   * @param query The room searching criteria.
   * @return A {@link java.util.stream.Stream} of rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/search-rooms-v3">Search Rooms V3</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  java.util.stream.Stream<V3RoomDetail> searchAllRooms(@Nonnull V2RoomSearchCriteria query);

  /**
   * Search rooms and return in a {@link java.util.stream.Stream} according to the specified criteria.
   *
   * @param query      The room searching criteria.
   * @param pagination The chunkSize and totalSize for stream pagination.
   * @return A {@link java.util.stream.Stream} of rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference/search-rooms-v3">Search Rooms V3</a>
   */
  @API(status = API.Status.EXPERIMENTAL)
  java.util.stream.Stream<V3RoomDetail> searchAllRooms(@Nonnull V2RoomSearchCriteria query,
      @Nonnull StreamPaginationAttribute pagination);

  /**
   * Update the attributes of an existing chatroom.
   *
   * @param roomId         The id of the room to be updated
   * @param roomAttributes The attributes to be updated to the room
   * @return The information of the room after being updated.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-room-v3">Update Room V3</a>
   */
  V3RoomDetail updateRoom(@Nonnull String roomId, @Nonnull V3RoomAttributes roomAttributes);

  /**
   * {@link StreamService#addMemberToRoom(Long, String)}
   *
   * @param userId      The id of the user to be added to the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference/add-member">Add Member</a>
   */
  void addMemberToRoom(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#removeMemberFromRoom(Long, String)}
   *
   * @param userId      The id of the user to be removed from the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference/remove-member">Remove Member</a>
   */
  void removeMemberFromRoom(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#share(String, ShareContent)}
   *
   * @param streamId    The stream id.
   * @param content     The third-party {@link ShareContent} to be shared.
   * @return Message contains share content
   * @see <a href="https://developers.symphony.com/restapi/reference/share-v3">Share</a>
   */
  V2Message share(@Nonnull String streamId, @Nonnull ShareContent content);

  /**
   * {@link StreamService#promoteUserToRoomOwner(Long, String)}
   *
   * @param userId      The id of the user to be promoted to room owner.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference/promote-owner">Promote Owner</a>
   */
  void promoteUserToRoomOwner(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#demoteUserToRoomParticipant(Long, String)}
   *
   * @param userId      The id of the user to be demoted to room participant.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference/demote-owner">Demote Owner</a>
   */
  void demoteUserToRoomParticipant(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * Lists the current members of an existing room.
   *
   * @param roomId The room stream id
   * @return List of members in the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-members">Room Members</a>
   */
  List<MemberInfo> listRoomMembers(@Nonnull String roomId);
}
