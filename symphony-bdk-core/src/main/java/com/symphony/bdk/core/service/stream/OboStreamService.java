package com.symphony.bdk.core.service.stream;


import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.UserId;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.http.api.ApiException;

import java.util.List;

class OboStreamService {

  protected final StreamsApi streamsApi;
  protected final RoomMembershipApi roomMembershipApi;
  protected final ShareApi shareApi;
  protected final RetryWithRecoveryBuilder<?> retryBuilder;

  protected OboStreamService(StreamsApi streamsApi, RoomMembershipApi roomMembershipApi, ShareApi shareApi, RetryWithRecoveryBuilder<?> retryBuilder) {
    this.streamsApi = streamsApi;
    this.roomMembershipApi = roomMembershipApi;
    this.shareApi = shareApi;
    this.retryBuilder = retryBuilder;
  }

  /**
   * {@link StreamService#getStreamInfo(String)}
   *
   * @param authSession Bot Session or Obo Session
   * @param streamId    The stream id
   * @return The information about the stream with the given id.
   * @see <a href="https://developers.symphony.com/restapi/reference#stream-info-v2">Stream Info V2</a>
   */
  public V2StreamAttributes getStreamInfo(AuthSession authSession, String streamId) {
    return executeAndRetry("getStreamInfo",
        () -> streamsApi.v2StreamsSidInfoGet(streamId, authSession.getSessionToken()), authSession);
  }

  /**
   * {@link StreamService#listStreams(StreamFilter)}
   *
   * @param authSession Bot Session or Obo Session
   * @param filter      The stream searching criteria
   * @return The list of streams retrieved according to the searching criteria.
   * @see <a href="https://developers.symphony.com/restapi/reference#list-user-streams">List Streams</a>
   */
  public List<StreamAttributes> listStreams(AuthSession authSession, StreamFilter filter) {
    return executeAndRetry("listStreams",
        () -> streamsApi.v1StreamsListPost(authSession.getSessionToken(), null, null, filter), authSession);
  }

  /**
   * {@link StreamService#addMemberToRoom(Long, String)}
   *
   * @param authSession Bot Session or Obo Session
   * @param userId      The id of the user to be added to the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#add-member">Add Member</a>
   */
  public void addMemberToRoom(AuthSession authSession, Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("addMemberToRoom",
        () -> roomMembershipApi.v1RoomIdMembershipAddPost(roomId, authSession.getSessionToken(), user), authSession);
  }

  /**
   * {@link StreamService#removeMemberFromRoom(Long, String)}
   *
   * @param authSession Bot Session or Obo Session
   * @param userId      The id of the user to be removed from the given room
   * @param roomId      The room id
   * @see <a href="https://developers.symphony.com/restapi/reference#remove-member">Remove Member</a>
   */
  public void removeMemberFromRoom(AuthSession authSession, Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("removeMemberFrom",
        () -> roomMembershipApi.v1RoomIdMembershipRemovePost(roomId, authSession.getSessionToken(), user), authSession);
  }

  /**
   * {@link StreamService#share(String, ShareContent)}
   *
   * @param authSession Bot Session or Obo Session
   * @param streamId    The stream id.
   * @param content     The third-party {@link ShareContent} to be shared.
   * @return Message contains share content
   * @see <a href="https://developers.symphony.com/restapi/reference#share-v3">Share</a>
   */
  public V2Message share(AuthSession authSession, String streamId, ShareContent content) {
    return executeAndRetry("share",
        () -> shareApi.v3StreamSidSharePost(streamId, authSession.getSessionToken(), content, authSession.getKeyManagerToken()), authSession);
  }

  /**
   * {@link StreamService#promoteUserToRoomOwner(Long, String)}
   *
   * @param authSession Bot Session or Obo Session.
   * @param userId      The id of the user to be promoted to room owner.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#promote-owner">Promote Owner</a>
   */
  public void promoteUserToRoomOwner(AuthSession authSession, Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("promoteUserToOwner",
        () -> roomMembershipApi.v1RoomIdMembershipPromoteOwnerPost(roomId, authSession.getSessionToken(), user), authSession);
  }

  /**
   * {@link StreamService#demoteUserToRoomParticipant(Long, String)}
   *
   * @param authSession Bot Session or Obo Session.
   * @param userId      The id of the user to be demoted to room participant.
   * @param roomId      The room id.
   * @see <a href="https://developers.symphony.com/restapi/reference#demote-owner">Demote Owner</a>
   */
  public void demoteUserToRoomParticipant(AuthSession authSession, Long userId, String roomId) {
    UserId user = new UserId().id(userId);
    executeAndRetry("demoteUserToParticipant",
        () -> roomMembershipApi.v1RoomIdMembershipDemoteOwnerPost(roomId, authSession.getSessionToken(), user), authSession);
  }

  protected <T> T executeAndRetry(String name, SupplierWithApiException<T> supplier, AuthSession authSession) {
    final RetryWithRecoveryBuilder<?> retryBuilderWithAuthSession = RetryWithRecoveryBuilder.from(retryBuilder)
        .clearRecoveryStrategies() // to remove refresh on bot session put by default
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
    return RetryWithRecovery.executeAndRetry(retryBuilderWithAuthSession, name, supplier);
  }
}
