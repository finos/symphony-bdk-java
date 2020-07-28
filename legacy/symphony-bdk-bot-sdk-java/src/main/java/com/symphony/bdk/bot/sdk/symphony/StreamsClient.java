package com.symphony.bdk.bot.sdk.symphony;

import java.util.List;

import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.StreamType;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoom;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomMember;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomSearchQuery;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomSearchResult;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyStream;

/**
 * Retrieves stream-related details
 *
 * @author Gabriel Berberian
 *
 */
public interface StreamsClient {

  /**
   * Gets the id of an IM stream of a user with the bot
   *
   * @param userId the user ID
   * @return the IM stream ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  String getUserIMStreamId(Long userId) throws SymphonyClientException;

  /**
   * Gets the id of an IM stream of a list of user
   *
   * @param userIds the user IDs list
   * @return the IM stream ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  String getUserListIM(List<Long> userIds) throws SymphonyClientException;

  /**
   * Creates a room
   *
   * @param symphonyRoom the Symphony room details
   * @return the created room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom createRoom(SymphonyRoom symphonyRoom)
      throws SymphonyClientException;

  /**
   * Adds member to room
   *
   * @param streamId the stream ID to add member to 
   * @param userId the user ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void addMemberToRoom(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Removes a member from room
   *
   * @param streamId the stream ID to remove member from
   * @param userId the user ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void removeMemberFromRoom(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Gets room information
   *
   * @param streamId the stream ID
   * @return the room information
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom getRoomInfo(String streamId) throws SymphonyClientException;

  /**
   * Updates room
   *
   * @param streamId the stream ID
   * @param symphonyRoom the Symphony room details
   * @return the updated room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom updateRoom(String streamId, SymphonyRoom symphonyRoom)
      throws SymphonyClientException;

  /**
   * Gets stream information
   *
   * @param streamId the stream ID
   * @return the stream info
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyStream getStreamInfo(String streamId) throws SymphonyClientException;

  /**
   * Gets room members
   *
   * @param streamId the stream ID
   * @return the room members
   * @throws SymphonyClientException on error connecting to Symphony
   */
  List<SymphonyRoomMember> getRoomMembers(String streamId)
      throws SymphonyClientException;

  /**
   * Activates a room
   *
   * @param streamId the stream ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void activateRoom(String streamId) throws SymphonyClientException;

  /**
   * Deactivates a room
   *
   * @param streamId the stream ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void deactivateRoom(String streamId) throws SymphonyClientException;

  /**
   * Promotes a user to owner
   *
   * @param streamId the stream ID
   * @param userId the user ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void promoteUserToOwner(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Demotes a owner user
   *
   * @param streamId the stream ID
   * @param userId the user ID
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void demoteUserFromOwner(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Searches for room
   *
   * @param symphonySearchQuery the search query
   * @return the searched room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoomSearchResult searchRooms(SymphonyRoomSearchQuery symphonySearchQuery)
      throws SymphonyClientException;

  /**
   * Gets user streams
   *
   * @param streamTypes the stream types to be considered in search
   * @param includeInactiveStreams whether to include inactive streams in search
   * @return list of user streams
   * @throws SymphonyClientException on error connecting to Symphony
   */
  List<SymphonyStream> getUserStreams(List<StreamType> streamTypes,
      boolean includeInactiveStreams) throws SymphonyClientException;

  /**
   * Gets user wall stream
   *
   * @return the user wall stream
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyStream getUserWallStream() throws SymphonyClientException;

}
