package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.bot.sdk.internal.symphony.model.HealthCheckInfo;
import com.symphony.ms.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoom;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomMember;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomSearchQuery;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomSearchResult;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUser;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUserFilter;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUserSearchResult;

import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;

import java.util.List;

import javax.ws.rs.core.NoContentException;

/**
 * Exposes simple Symphony-specific services abstracting any complexities to talk to Symphony APIs.
 *
 * @author Marcus Secato
 */
public interface SymphonyService {

  /**
   * Registers a listener for Symphony IM events
   *
   * @param imListener
   */
  void registerIMListener(IMListener imListener);

  /**
   * Registers a listener for Symphony room events
   *
   * @param roomListener
   */
  void registerRoomListener(RoomListener roomListener);

  /**
   * Registers a listener for Symphony Elements events
   *
   * @param elementsListener
   */
  void registerElementsListener(ElementsListener elementsListener);

  /**
   * @return bot user id
   */
  Long getBotUserId();

  /**
   * @return bot display name
   */
  String getBotDisplayName();

  /**
   * Retrieves health details for Symphony components (e.g. POD, agent)
   *
   * @return health check info
   */
  HealthCheckInfo healthCheck();

  /**
   * Sends message to Symphony stream
   *
   * @param streamId
   * @param message
   * @param jsonData
   */
  void sendMessage(String streamId, String message, String jsonData);

  /**
   * Initializes the process of authenticating an extension app
   *
   * @param appId
   * @return authenticate response
   */
  AuthenticateResponse appAuthenticate(String appId);

  /**
   * Validates tokens as part of extension app authentication process
   *
   * @param appToken
   * @param symphonyToken
   * @return true if tokens are valid, false otherwise
   */
  boolean validateTokens(String appToken, String symphonyToken);

  /**
   * Verifies the given JWT
   *
   * @param jwt
   * @return userId
   */
  Long verifyJWT(String jwt);

  /**
   * Gets the id of an IM stream of a user with the bot
   *
   * @param userId
   * @return the IM stream id
   */
  String getUserIMStreamId(Long userId);

  /**
   * Gets the id of an IM stream of a list of user
   *
   * @param userIds
   * @return the IM stream id
   */
  String getUserListIM(List<Long> userIds);

  /**
   * Creates a room
   *
   * @param symphonyRoom
   * @return the created room
   */
  SymphonyRoom createRoom(SymphonyRoom symphonyRoom);

  /**
   * Adds member to room
   *
   * @param stringId
   * @param userId
   */
  void addMemberToRoom(String stringId, Long userId);

  /**
   * Removes a member from room
   *
   * @param streamId
   * @param userId
   */
  void removeMemberFromRoom(String streamId, Long userId);

  /**
   * Gets room information
   *
   * @param streamId
   * @return the room information
   */
  SymphonyRoom getRoomInfo(String streamId);

  /**
   * Updates room
   *
   * @param streamId
   * @param symphonyRoom
   * @return the updated room
   */
  SymphonyRoom updateRoom(String streamId, SymphonyRoom symphonyRoom);

  /**
   * Gets stream information
   *
   * @param streamId
   * @return the stream info
   */
  SymphonyStream getStreamInfo(String streamId);

  /**
   * Gets room members
   *
   * @param streamId
   * @return the room members
   */
  List<SymphonyRoomMember> getRoomMembers(String streamId);

  /**
   * Activates a room
   *
   * @param streamId
   */
  void activateRoom(String streamId);

  /**
   * Deactivates a room
   *
   * @param streamId
   */
  void deactivateRoom(String streamId);

  /**
   * Promotes a user to owner
   *
   * @param streamId
   * @param userId
   */
  void promoteUserToOwner(String streamId, Long userId);

  /**
   * Demotes a owner user
   *
   * @param streamId
   * @param userId
   */
  void demoteUserFromOwner(String streamId, Long userId);

  /**
   * Searches for room
   *
   * @param symphonySearchQuery
   * @return the searched room
   * @throws NoContentException
   */
  SymphonyRoomSearchResult searchRooms(SymphonyRoomSearchQuery symphonySearchQuery)
      throws NoContentException;

  /**
   * Gets user streams
   *
   * @param streamTypes
   * @param includeInactiveStreams
   * @return
   */
  List<SymphonyStream> getUserStreams(List<StreamType> streamTypes, boolean includeInactiveStreams);

  /**
   * Gets user wall stream
   *
   * @return the user wall stream
   */
  SymphonyStream getUserWallStream();

  /**
   * Gets user given an username
   *
   * @param username
   * @return the user
   * @throws NoContentException
   */
  SymphonyUser getUserFromUsername(String username) throws NoContentException;

  /**
   * Gets user given an email
   *
   * @param email
   * @param local
   * @return the user
   * @throws NoContentException
   */
  SymphonyUser getUserFromEmail(String email, Boolean local) throws NoContentException;

  /**
   * Gets user given an user userId
   *
   * @param userId
   * @param local
   * @return the user
   * @throws NoContentException
   */
  SymphonyUser getUserFromId(Long userId, Boolean local) throws NoContentException;

  /**
   * Gets users given some user ids
   *
   * @param userIds
   * @param local
   * @return the users
   * @throws NoContentException
   */
  List<SymphonyUser> getUsersFromIdList(List<Long> userIds, Boolean local)
      throws NoContentException;

  /**
   * Gets users given some emails
   *
   * @param emails
   * @param local
   * @return the users
   * @throws NoContentException
   */
  List<SymphonyUser> getUsersFromEmailList(List<String> emails, Boolean local)
      throws NoContentException;

  /**
   * Gets users given some emails and userIds
   *
   * @param emails
   * @param userIds
   * @param local
   * @return the users
   * @throws NoContentException
   */
  List<SymphonyUser> getUsersV3(List<String> emails, List<Long> userIds, Boolean local)
      throws NoContentException;

  /**
   * Searcher for user given an filter
   *
   * @param userFilter
   * @return the user search result
   * @throws NoContentException
   */
  SymphonyUserSearchResult searchUsers(SymphonyUserFilter userFilter)
      throws NoContentException;

  /**
   * Gets the bot
   *
   * @return the bot
   */
  SymphonyUser getSessionUser();
}
