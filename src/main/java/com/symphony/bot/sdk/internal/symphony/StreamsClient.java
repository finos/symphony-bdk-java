package com.symphony.bot.sdk.internal.symphony;

import java.util.List;

import com.symphony.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyRoom;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyRoomMember;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyRoomSearchQuery;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyRoomSearchResult;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyStream;

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
   * @param userId
   * @return the IM stream id
   * @throws SymphonyClientException on error connecting to Symphony
   */
  String getUserIMStreamId(Long userId) throws SymphonyClientException;

  /**
   * Gets the id of an IM stream of a list of user
   *
   * @param userIds
   * @return the IM stream id
   * @throws SymphonyClientException on error connecting to Symphony
   */
  String getUserListIM(List<Long> userIds) throws SymphonyClientException;

  /**
   * Creates a room
   *
   * @param symphonyRoom
   * @return the created room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom createRoom(SymphonyRoom symphonyRoom)
      throws SymphonyClientException;

  /**
   * Adds member to room
   *
   * @param stringId
   * @param userId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void addMemberToRoom(String stringId, Long userId)
      throws SymphonyClientException;

  /**
   * Removes a member from room
   *
   * @param streamId
   * @param userId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void removeMemberFromRoom(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Gets room information
   *
   * @param streamId
   * @return the room information
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom getRoomInfo(String streamId) throws SymphonyClientException;

  /**
   * Updates room
   *
   * @param streamId
   * @param symphonyRoom
   * @return the updated room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoom updateRoom(String streamId, SymphonyRoom symphonyRoom)
      throws SymphonyClientException;

  /**
   * Gets stream information
   *
   * @param streamId
   * @return the stream info
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyStream getStreamInfo(String streamId) throws SymphonyClientException;

  /**
   * Gets room members
   *
   * @param streamId
   * @return the room members
   * @throws SymphonyClientException on error connecting to Symphony
   */
  List<SymphonyRoomMember> getRoomMembers(String streamId)
      throws SymphonyClientException;

  /**
   * Activates a room
   *
   * @param streamId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void activateRoom(String streamId) throws SymphonyClientException;

  /**
   * Deactivates a room
   *
   * @param streamId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void deactivateRoom(String streamId) throws SymphonyClientException;

  /**
   * Promotes a user to owner
   *
   * @param streamId
   * @param userId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void promoteUserToOwner(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Demotes a owner user
   *
   * @param streamId
   * @param userId
   * @throws SymphonyClientException on error connecting to Symphony
   */
  void demoteUserFromOwner(String streamId, Long userId)
      throws SymphonyClientException;

  /**
   * Searches for room
   *
   * @param symphonySearchQuery
   * @return the searched room
   * @throws SymphonyClientException on error connecting to Symphony
   */
  SymphonyRoomSearchResult searchRooms(SymphonyRoomSearchQuery symphonySearchQuery)
      throws SymphonyClientException;

  /**
   * Gets user streams
   *
   * @param streamTypes
   * @param includeInactiveStreams
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
