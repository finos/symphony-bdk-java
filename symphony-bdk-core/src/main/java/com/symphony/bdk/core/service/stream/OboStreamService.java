package com.symphony.bdk.core.service.stream;

import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Service interface exposing OBO-enabled endpoints to manage streams.
 */
@API(status = API.Status.STABLE)
public interface OboStreamService {

  /**
   * {@link StreamService#getStreamInfo(String)}
   *
   * @param streamId    The stream id
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  V2StreamAttributes getStreamInfo(String streamId);

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param filter      The stream searching criteria
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  List<StreamAttributes> listStreams(StreamFilter filter);

  /**
   * {@link StreamService#addMemberToRoom(Long, String)}
   *
   * @param userId      The id of the user to be added to the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#add-member">Add Member</a>
   */
  void addMemberToRoom( Long userId, String roomId);

  /**
   * {@link StreamService#removeMemberFromRoom(Long, String)}
   *
   * @param userId      The id of the user to be removed from the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-member">Remove Member</a>
   */
  void removeMemberFromRoom(Long userId, String roomId);

  /**
   * {@link StreamService#share(String, ShareContent)}
   *
   * @param streamId    The stream id.
   * @param content     The third-party {@link ShareContent} to be shared.
   * @return Message contains share content
   * @see <a href="https://developers.symphony.com/restapi/reference#share-v3">Share</a>
   */
  V2Message share(String streamId, ShareContent content);

  /**
   * {@link StreamService#promoteUserToRoomOwner(Long, String)}
   *
   * @param userId      The id of the user to be promoted to room owner.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#promote-owner">Promote Owner</a>
   */
  void promoteUserToRoomOwner(Long userId, String roomId);

  /**
   * {@link StreamService#demoteUserToRoomParticipant(Long, String)}
   *
   * @param userId      The id of the user to be demoted to room participant.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#demote-owner">Demote Owner</a>
   */
  void demoteUserToRoomParticipant(Long userId, String roomId);
}
