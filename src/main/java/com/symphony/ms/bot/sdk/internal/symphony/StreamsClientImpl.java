package com.symphony.ms.bot.sdk.internal.symphony;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.NoContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoom;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomMember;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomSearchQuery;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyRoomSearchResult;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;
import clients.SymBotClient;
import model.Keyword;
import model.Room;
import model.RoomSearchQuery;

@Service
public class StreamsClientImpl implements StreamsClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(StreamsClientImpl.class);

  private clients.symphony.api.StreamsClient streamsClient;

  public StreamsClientImpl(SymBotClient symBotClient) {
    this.streamsClient = symBotClient.getStreamsClient();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUserIMStreamId(Long userId) throws SymphonyClientException {
    try {
      return streamsClient.getUserIMStreamId(userId);
    } catch (Exception e) {
      LOGGER.error("Error on getUserIMStreamId");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUserListIM(List<Long> userIds)
      throws SymphonyClientException {
    try {
      return streamsClient.getUserListIM(userIds);
    } catch (Exception e) {
      LOGGER.error("Error on getUserListIM");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyRoom createRoom(SymphonyRoom symphonyRoom)
      throws SymphonyClientException {
    try {
      return new SymphonyRoom(streamsClient.createRoom(
          toRoom(symphonyRoom)));
    } catch (Exception e) {
      LOGGER.error("Error on createRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMemberToRoom(String streamId, Long userId)
      throws SymphonyClientException {
    try {
      streamsClient.addMemberToRoom(streamId, userId);
    } catch (Exception e) {
      LOGGER.error("Error on addMemberToRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeMemberFromRoom(String streamId, Long userId)
      throws SymphonyClientException {
    try {
      streamsClient.removeMemberFromRoom(streamId, userId);
    } catch (Exception e) {
      LOGGER.error("Error on removeMemberFromRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyRoom getRoomInfo(String streamId)
      throws SymphonyClientException {
    try {
      return new SymphonyRoom(streamsClient.getRoomInfo(streamId));
    } catch (Exception e) {
      LOGGER.error("Error on getRoomInfo");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyRoom updateRoom(String streamId, SymphonyRoom symphonyRoom)
      throws SymphonyClientException {
    try {
      return new SymphonyRoom(
        streamsClient.updateRoom(streamId, toRoom(symphonyRoom)));
    } catch (Exception e) {
      LOGGER.error("Error on updateRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyStream getStreamInfo(String streamId)
      throws SymphonyClientException {
    try {
      return new SymphonyStream(streamsClient.getStreamInfo(streamId));
    } catch (Exception e) {
      LOGGER.error("Error on getStreamInfo");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SymphonyRoomMember> getRoomMembers(String streamId)
      throws SymphonyClientException {
    try {
      return streamsClient
          .getRoomMembers(streamId)
          .stream()
          .map(SymphonyRoomMember::new)
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error on getRoomMembers");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void activateRoom(String streamId) throws SymphonyClientException {
    try {
      streamsClient.activateRoom(streamId);
    } catch (Exception e) {
      LOGGER.error("Error on activateRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deactivateRoom(String streamId) throws SymphonyClientException {
    try {
      streamsClient.deactivateRoom(streamId);
    } catch (Exception e) {
      LOGGER.error("Error on deactivateRoom");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void promoteUserToOwner(String streamId, Long userId)
      throws SymphonyClientException {
    try {
      streamsClient.promoteUserToOwner(streamId, userId);
    } catch (Exception e) {
      LOGGER.error("Error on promoteUserToOwner");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void demoteUserFromOwner(String streamId, Long userId)
      throws SymphonyClientException {
    try {
      streamsClient.demoteUserFromOwner(streamId, userId);
    } catch (Exception e) {
      LOGGER.error("Error on demoteUserFromOwner");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyRoomSearchResult searchRooms(SymphonyRoomSearchQuery symphonySearchQuery)
      throws SymphonyClientException {
    try {
      return new SymphonyRoomSearchResult(streamsClient
          .searchRooms(toRoomSearchQuery(symphonySearchQuery), symphonySearchQuery.getSkip(),
              symphonySearchQuery.getLimit()));
    } catch (NoContentException nce) {
      return new SymphonyRoomSearchResult();
    } catch (Exception e) {
      LOGGER.error("Error on searchRooms");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SymphonyStream> getUserStreams(List<StreamType> streamTypes,
      boolean includeInactiveStreams) throws SymphonyClientException {
    try {
      List<String> streamTypeNames = streamTypes != null ? streamTypes
          .stream()
          .map(StreamType::toString)
          .collect(Collectors.toList()) : null;
      return streamsClient.getUserStreams(streamTypeNames, includeInactiveStreams)
          .stream()
          .map(SymphonyStream::new)
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error on getUserStreams");
      throw new SymphonyClientException(e);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyStream getUserWallStream() throws SymphonyClientException {
    try {
      return new SymphonyStream(streamsClient.getUserWallStream());
    } catch (Exception e) {
      LOGGER.error("Error on getUserWallStream");
      throw new SymphonyClientException(e);
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

}
