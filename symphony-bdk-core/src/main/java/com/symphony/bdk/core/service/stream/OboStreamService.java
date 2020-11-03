package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;

import org.apiguardian.api.API;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service interface exposing OBO-enabled endpoints to manage streams.
 */
@API(status = API.Status.STABLE)
public interface OboStreamService {

  /**
   * {@link StreamService#getStreamInfo(String)}
   *
   * @param streamId    The stream id.
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  V2StreamAttributes getStreamInfo(@Nonnull String streamId);

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param filter      The stream searching criteria.
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  List<StreamAttributes> listStreams(@Nullable StreamFilter filter);

  /**
   * {@link StreamService#listStreams(StreamFilter, PaginationAttribute)}
   *
   * @param filter      The stream searching criteria.
   * @param pagination  The skip and limit for pagination.
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  List<StreamAttributes> listStreams(@Nullable StreamFilter filter, @Nonnull PaginationAttribute pagination);

  /**
   * {@link StreamService#listAllStreams(StreamFilter)}
   *
   * @param filter The stream searching criteria.
   * @return a {@link Stream} of matching streams according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter);

  /**
   * {@link StreamService#listAllStreams(StreamFilter, StreamPaginationAttribute)}
   *
   * @param filter      The stream searching criteria.
   * @param pagination  The chunkSize and totalSize for pagination with default value equals 50.
   * @return a {@link Stream} of matching streams according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  Stream<StreamAttributes> listAllStreams(@Nullable StreamFilter filter, @Nonnull StreamPaginationAttribute pagination);

  /**
   * {@link StreamService#addMemberToRoom(Long, String)}
   *
   * @param userId      The id of the user to be added to the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#add-member">Add Member</a>
   */
  void addMemberToRoom(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#removeMemberFromRoom(Long, String)}
   *
   * @param userId      The id of the user to be removed from the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-member">Remove Member</a>
   */
  void removeMemberFromRoom(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#share(String, ShareContent)}
   *
   * @param streamId    The stream id.
   * @param content     The third-party {@link ShareContent} to be shared.
   * @return Message contains share content
   * @see <a href="https://developers.symphony.com/restapi/reference#share-v3">Share</a>
   */
  V2Message share(@Nonnull String streamId, @Nonnull ShareContent content);

  /**
   * {@link StreamService#promoteUserToRoomOwner(Long, String)}
   *
   * @param userId      The id of the user to be promoted to room owner.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#promote-owner">Promote Owner</a>
   */
  void promoteUserToRoomOwner(@Nonnull Long userId, @Nonnull String roomId);

  /**
   * {@link StreamService#demoteUserToRoomParticipant(Long, String)}
   *
   * @param userId      The id of the user to be demoted to room participant.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#demote-owner">Demote Owner</a>
   */
  void demoteUserToRoomParticipant(@Nonnull Long userId, @Nonnull String roomId);
}
