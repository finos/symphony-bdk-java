package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.api.invoker.ApiRuntimeException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.stream.constant.AttachmentSort;
import com.symphony.bdk.gen.api.StreamsApi;

import com.symphony.bdk.gen.api.model.RoomDetail;
import com.symphony.bdk.gen.api.model.Stream;

import com.symphony.bdk.gen.api.model.StreamAttachmentItem;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamList;
import com.symphony.bdk.gen.api.model.V2MembershipList;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;

import com.symphony.bdk.gen.api.model.V3RoomDetail;

import com.symphony.bdk.gen.api.model.V3RoomSearchResults;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for managing streams.
 *
 * This service is used for retrieving information about a particular stream or
 * chatroom, searching streams, listing members, attachments of a particular stream,
 * perform some action related to a stream like:
 * <p><ul>
 *   <li>Create a IM or MIM</li>
 *   <li>Create a chatroom</li>
 *   <li>Activate or Deactivate a chatroom</li>
 *   <li></li>
 * </ul></p>
 *
 */
@Slf4j
public class StreamService {

  private final StreamsApi streamsApi;
  private final AuthSession authSession;

  public StreamService(StreamsApi streamsApi, AuthSession authSession) {
    this.streamsApi = streamsApi;
    this.authSession = authSession;
  }

  /**
   * Create a new single or multi party instant message conversation between the caller and specified users.
   *
   * The caller is implicitly included in the members of the created chat.
   *
   * Duplicate users will be included in the membership of the chat but
   * the duplication will be silently ignored.
   *
   * If there is an existing IM conversation with the same set of participants then
   * the id of that existing stream will be returned.
   *
   * @param uids List of user ids of the participants
   * @return The created IM or MIM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim">Create IM or MIM</a>
   */
  public Stream createIMorMIM(List<Long> uids) {
    try {
      return streamsApi.v1ImCreatePost(authSession.getSessionToken(), uids);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Create a new chatroom.
   * If no  attributes are specified, the room is created as a private chatroom.
   *
   * @param roomAttributes Attributes of the created room
   * @return The created chatroom
   * @see <a href="https://developers.symphony.com/restapi/reference#create-room-v3">Create Room V3</a>
   */
  public V3RoomDetail createRoomChat(V3RoomAttributes roomAttributes) {
    try {
      return streamsApi.v3RoomCreatePost(authSession.getSessionToken(), roomAttributes);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Search rooms according to the specified criteria.
   *
   * @param query The room searching criteria
   * @return The rooms returned according to the given criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#search-rooms-v3">Search Rooms V3</a>
   */
  public V3RoomSearchResults searchRooms(V2RoomSearchCriteria query) {
    try {
      return streamsApi.v3RoomSearchPost(authSession.getSessionToken(), query, null, null);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get information about a particular room.
   *
   * @param roomId The room id.
   * @return The information about the room with the given room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#room-info-v3">Room Info V3</a>
   */
  public V3RoomDetail getRoomInfo(String roomId) {
    try {
      return streamsApi.v3RoomIdInfoGet(roomId, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
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
    try {
      return streamsApi.v1RoomIdSetActivePost(roomId, active, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Update the attributes of an existing chatroom.
   *
   * @param roomId The id of the room to be updated
   * @param roomAttributes The attributes to be updated to the room
   * @return The information of the room after being updated.
   * @see <a href="https://developers.symphony.com/restapi/reference#update-room-v3">Update Room V3</a>
   */
  public V3RoomDetail updateRoom(String roomId, V3RoomAttributes roomAttributes) {
    try {
      return streamsApi.v3RoomIdUpdatePost(roomId, authSession.getSessionToken(), roomAttributes);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
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
    try {
      return streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Get information about a particular stream.
   *
   * @param streamId The stream id
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  public V2StreamAttributes getStreamInfo(String streamId) {
    try {
      return streamsApi.v2StreamsSidInfoGet(streamId, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * List attachments in a particular stream.
   *
   * @param streamId The stream id
   * @param sinceInMillis Timestamp in millis of first required attachment
   * @param toInMillis Timestamp in millis of last required attachment
   * @param sort Attachment date sort direction : ASC or DESC (default to ASC)
   * @return List of attachments in the stream with the given stream id.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-attachments">List Attachments</a>
   */
  public List<StreamAttachmentItem> getAttachmentsOfStream(String streamId, Long sinceInMillis, Long toInMillis, AttachmentSort sort) {
    try {
      return streamsApi.v1StreamsSidAttachmentsGet(streamId, authSession.getSessionToken(), sinceInMillis, toInMillis, null, sort.name());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Create a new single or multi party instant message conversation.
   * At least two user IDs must be provided or an error response will be sent.
   *
   * The caller is not included in the members of the created chat.
   *
   * Duplicate users will be included in the membership of the chat but the
   * duplication will be silently ignored.
   *
   * If there is an existing IM conversation with the same set of participants then
   * the id of that existing stream will be returned.
   *
   * @param uids List of user IDs of participants. At least two user IDs must be provided
   * @return The created IM or MIM
   * @see <a href="https://developers.symphony.com/restapi/reference#create-im-or-mim-admin">Create IM or MIM Non-inclusive</a>
   */
  public Stream createAdminIMorMIM(List<Long> uids) {
    try {
      return streamsApi.v1AdminImCreatePost(authSession.getSessionToken(), uids);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Deactivate or reactivate a chatroom via AC Portal.
   *
   * @param streamId The stream id
   * @param active Deactivate or activate
   * @return The information of the room after being deactivated or reactivated.
   */
  public RoomDetail setRoomActiveAdmin(String streamId, Boolean active) {
    try {
      return streamsApi.v1AdminRoomIdSetActivePost(streamId, active, authSession.getSessionToken());
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }

  /**
   * Retrieve all the streams across the enterprise.
   *
   * @param filter The stream searching filter
   * @return List of streams returned according the given filter.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-streams-for-enterprise-v2">List Streams for Enterprise V2</a>
   */
  public V2AdminStreamList listStreamsAdmin(V2AdminStreamFilter filter) {
    try {
      return streamsApi.v2AdminStreamsListPost(authSession.getSessionToken(), null, null, filter);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
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
    try {
      return streamsApi.v1AdminStreamIdMembershipListGet(streamId, authSession.getSessionToken(), null, null);
    } catch (ApiException apiException) {
      throw new ApiRuntimeException(apiException);
    }
  }
}
