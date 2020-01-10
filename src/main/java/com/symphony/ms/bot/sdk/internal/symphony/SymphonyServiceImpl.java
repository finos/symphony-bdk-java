package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.lib.restclient.RestClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.AppAuthenticateException;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SendMessageException;
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

import authentication.SymExtensionAppRSAAuth;
import clients.SymBotClient;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import model.AppAuthResponse;
import model.HealthcheckResponse;
import model.Keyword;
import model.OutboundMessage;
import model.Room;
import model.RoomSearchQuery;
import model.UserFilter;
import model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.NoContentException;

@Service
public class SymphonyServiceImpl implements SymphonyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyServiceImpl.class);
  private static final String HEALTH_POD_ENDPOINT = "pod/v1/podcert";

  private SymBotClient symBotClient;
  private SymExtensionAppRSAAuth symExtensionAppRSAAuth;
  private RestClient restClient;
  private String userDisplayName;

  public SymphonyServiceImpl(SymBotClient symBotClient,
      SymExtensionAppRSAAuth symExtensionAppRSAAuth, RestClient restClient) {
    this.symBotClient = symBotClient;
    this.symExtensionAppRSAAuth = symExtensionAppRSAAuth;
    this.restClient = restClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerIMListener(IMListener imListener) {
    LOGGER.info("Adding IM listener");
    symBotClient.getDatafeedEventsService().addIMListener(imListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerRoomListener(RoomListener roomListener) {
    LOGGER.info("Adding Room listener");
    symBotClient.getDatafeedEventsService().addRoomListener(roomListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerElementsListener(ElementsListener elementsListener) {
    LOGGER.info("Adding Elements listener");
    symBotClient.getDatafeedEventsService().addElementsListener(
        elementsListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getBotUserId() {
    return symBotClient.getBotUserInfo().getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getBotDisplayName() {
    return Optional.ofNullable(userDisplayName).orElseGet(() -> {
      userDisplayName = symBotClient.getBotUserInfo().getDisplayName();
      return userDisplayName;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HealthCheckInfo healthCheck() {
    return new HealthCheckInfo(checkAgentStatus(), checkPodStatus());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(String streamId, String message, String jsonData) {
    OutboundMessage outMessage = null;
    if (jsonData == null) {
      outMessage = new OutboundMessage(message);
    } else {
      outMessage = new OutboundMessage(message, jsonData);
    }

    internalSendMessage(streamId, outMessage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticateResponse appAuthenticate(String appId) {
    try {
      AppAuthResponse appAuthToken = symExtensionAppRSAAuth.appAuthenticate();
      return new AuthenticateResponse(appId, appAuthToken.getAppToken(),
          appAuthToken.getSymphonyToken());
    } catch (Exception e) {
      LOGGER.error("Error authentication extension app: {}\n{}", appId, e);
      throw new AppAuthenticateException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    return symExtensionAppRSAAuth.validateTokens(appToken, symphonyToken);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long verifyJWT(String jwt) {
    UserInfo userInfo = symExtensionAppRSAAuth.verifyJWT(jwt);
    if (userInfo != null) {
      return userInfo.getId();
    }
    throw new AppAuthenticateException();
  }

  @Override
  public String getUserIMStreamId(Long userId) {
    return symBotClient.getStreamsClient().getUserIMStreamId(userId);
  }

  @Override
  public String getUserListIM(List<Long> userIds) {
    return symBotClient.getStreamsClient().getUserListIM(userIds);
  }

  @Override
  public SymphonyRoom createRoom(SymphonyRoom symphonyRoom) {
    return new SymphonyRoom(symBotClient.getStreamsClient().createRoom(toRoom(symphonyRoom)));
  }

  @Override
  public void addMemberToRoom(String stringId, Long userId) {

  }

  @Override
  public void removeMemberFromRoom(String streamId, Long userId) {
    symBotClient.getStreamsClient().removeMemberFromRoom(streamId, userId);
  }

  @Override
  public SymphonyRoom getRoomInfo(String streamId) {
    return new SymphonyRoom(symBotClient.getStreamsClient().getRoomInfo(streamId));
  }

  @Override
  public SymphonyRoom updateRoom(String streamId, SymphonyRoom symphonyRoom) {
    return new SymphonyRoom(
        symBotClient.getStreamsClient().updateRoom(streamId, toRoom(symphonyRoom)));
  }

  @Override
  public SymphonyStream getStreamInfo(String streamId) {
    return new SymphonyStream(symBotClient.getStreamsClient().getStreamInfo(streamId));
  }

  @Override
  public List<SymphonyRoomMember> getRoomMembers(String streamId) {
    return symBotClient.getStreamsClient()
        .getRoomMembers(streamId)
        .stream()
        .map(SymphonyRoomMember::new)
        .collect(Collectors.toList());
  }

  @Override
  public void activateRoom(String streamId) {
    symBotClient.getStreamsClient().activateRoom(streamId);
  }

  @Override
  public void deactivateRoom(String streamId) {
    symBotClient.getStreamsClient().deactivateRoom(streamId);
  }

  @Override
  public void promoteUserToOwner(String streamId, Long userId) {
    symBotClient.getStreamsClient().promoteUserToOwner(streamId, userId);
  }

  @Override
  public void demoteUserFromOwner(String streamId, Long userId) {
    symBotClient.getStreamsClient().demoteUserFromOwner(streamId, userId);
  }

  @Override
  public SymphonyRoomSearchResult searchRooms(SymphonyRoomSearchQuery symphonySearchQuery)
      throws NoContentException {
    return new SymphonyRoomSearchResult(symBotClient.getStreamsClient()
        .searchRooms(toRoomSearchQuery(symphonySearchQuery), symphonySearchQuery.getSkip(),
            symphonySearchQuery.getLimit()));
  }

  @Override
  public List<SymphonyStream> getUserStreams(List<StreamType> streamTypes,
      boolean includeInactiveStreams) {
    List<String> streamTypeNames = streamTypes != null ? streamTypes
        .stream()
        .map(StreamType::toString)
        .collect(Collectors.toList()) : null;
    return symBotClient.getStreamsClient().getUserStreams(streamTypeNames, includeInactiveStreams)
        .stream()
        .map(SymphonyStream::new)
        .collect(Collectors.toList());

  }

  @Override
  public SymphonyStream getUserWallStream() {
    return new SymphonyStream(symBotClient.getStreamsClient().getUserWallStream());
  }

  private String getPodHealthUrl() {
    String hostUrl = symBotClient.getConfig().getPodHost();

    return (hostUrl.startsWith("https://") ? "" : "https://")
        + (hostUrl.endsWith("/") ? hostUrl : hostUrl + "/") + HEALTH_POD_ENDPOINT;
  }

  @Override
  public SymphonyUser getUserFromUsername(String username) throws NoContentException {
    return new SymphonyUser(symBotClient.getUsersClient().getUserFromUsername(username));
  }

  @Override
  public SymphonyUser getUserFromEmail(String email, Boolean local) throws NoContentException {
    return new SymphonyUser(symBotClient.getUsersClient().getUserFromEmail(email, local));
  }

  @Override
  public SymphonyUser getUserFromId(Long userId, Boolean local) throws NoContentException {
    return new SymphonyUser(symBotClient.getUsersClient().getUserFromId(userId, local));
  }

  @Override
  public List<SymphonyUser> getUsersFromIdList(List<Long> userIds, Boolean local)
      throws NoContentException {
    return symBotClient.getUsersClient()
        .getUsersFromIdList(userIds, local)
        .stream()
        .map(SymphonyUser::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<SymphonyUser> getUsersFromEmailList(List<String> emails, Boolean local)
      throws NoContentException {
    return symBotClient.getUsersClient()
        .getUsersFromEmailList(emails, local)
        .stream()
        .map(SymphonyUser::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<SymphonyUser> getUsersV3(List<String> emails, List<Long> userIds, Boolean local)
      throws NoContentException {
    return symBotClient.getUsersClient()
        .getUsersV3(emails, userIds, local)
        .stream()
        .map(SymphonyUser::new)
        .collect(Collectors.toList());
  }

  @Override
  public SymphonyUserSearchResult searchUsers(SymphonyUserFilter userFilter)
      throws NoContentException {
    return new SymphonyUserSearchResult(symBotClient.getUsersClient()
        .searchUsers(userFilter.getQuery(), userFilter.isLocal(), userFilter.getSkip(),
            userFilter.getLimit(), toUserFilter(userFilter)));
  }

  @Override
  public SymphonyUser getSessionUser() {
    return new SymphonyUser(symBotClient.getUsersClient().getSessionUser());
  }

  private boolean checkPodStatus() {
    boolean isPodUp = false;
    try {
      restClient.getRequest(getPodHealthUrl(), String.class);
      isPodUp = true;
    } catch (Exception e) {
      LOGGER.error("Error getting pod health status", e);
    }

    return isPodUp;
  }

  private HealthcheckResponse checkAgentStatus() {
    try {
      return symBotClient.getHealthcheckClient().performHealthCheck();
    } catch (Exception e) {
      LOGGER.error("Error getting agent health status");
    }
    return null;
  }

  private void internalSendMessage(String streamId, OutboundMessage message) {
    LOGGER.debug("Sending message to stream: {}", streamId);
    try {
      symBotClient.getMessagesClient().sendMessage(streamId, message);
    } catch (Exception e) {
      LOGGER.error("Error sending message to stream: {}\n{}", streamId, e);
      throw new SendMessageException();
    }
  }

  private Room toRoom(SymphonyRoom symphonyRoom) {
    Room room = new Room();
    room.setViewHistory(symphonyRoom.getViewHistory());
    room.setCopyProtected(symphonyRoom.getCopyProtected());
    room.setCrossPod(symphonyRoom.getCrossPod());
    room.setDescription(symphonyRoom.getDescription());
    room.setDiscoverable(symphonyRoom.getDiscoverable());
    room.setKeywords(symphonyRoom.getKeywords().entrySet().stream().map(entry -> {
      Keyword keyword = new Keyword();
      keyword.setKey(entry.getKey());
      keyword.setValue(entry.getValue());
      return keyword;
    }).collect(
        Collectors.toList()));
    room.setMembersCanInvite(symphonyRoom.getMembersCanInvite());
    room.setMultiLateralRoom(symphonyRoom.getMultiLateralRoom());
    room.setName(symphonyRoom.getName());
    room.setPublic(symphonyRoom.getPublicRoom());
    room.setReadOnly(symphonyRoom.getReadOnly());
    return room;
  }

  private RoomSearchQuery toRoomSearchQuery(SymphonyRoomSearchQuery symphonyRoomSearchQuery) {
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    roomSearchQuery.setActive(symphonyRoomSearchQuery.getActive());
    roomSearchQuery.setCreator(symphonyRoomSearchQuery.getCreator());
    roomSearchQuery.setLabels(symphonyRoomSearchQuery.getLabels());
    roomSearchQuery.setMember(symphonyRoomSearchQuery.getMember());
    roomSearchQuery.setOwner(symphonyRoomSearchQuery.getOwner());
    roomSearchQuery.setPrivate(symphonyRoomSearchQuery.getPrivateRoom());
    return roomSearchQuery;
  }

  private UserFilter toUserFilter(SymphonyUserFilter symphonyUserFilter) {
    UserFilter userFilter = new UserFilter();
    userFilter.setCompany(symphonyUserFilter.getCompany());
    userFilter.setLocation(symphonyUserFilter.getLocation());
    userFilter.setTitle(symphonyUserFilter.getTitle());
    return userFilter;
  }

}
