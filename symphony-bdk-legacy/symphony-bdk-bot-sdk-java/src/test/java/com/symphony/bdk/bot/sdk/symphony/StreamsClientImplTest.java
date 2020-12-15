package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoom;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomMember;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomSearchQuery;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyRoomSearchResult;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyStream;

import clients.SymBotClient;
import clients.symphony.api.StreamsClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.Keyword;
import model.NumericId;
import model.Room;
import model.RoomInfo;
import model.RoomMember;
import model.RoomName;
import model.RoomSearchQuery;
import model.RoomSearchResult;
import model.RoomSystemInfo;
import model.StreamAttributes;
import model.StreamInfo;
import model.StreamListItem;
import model.StreamType;
import model.StreamTypes;
import model.TypeObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.NoContentException;

public class StreamsClientImplTest {

  private StreamsClientImpl streamsClientImpl;
  private StreamsClient streamsClient;
  private SymBotClient symBotClient;

  @Before
  public void initBot(){
    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);

    this.streamsClient = Mockito.mock(StreamsClient.class);

    Mockito.when(this.symBotClient.getStreamsClient()).thenReturn(this.streamsClient);

    this.streamsClientImpl = new StreamsClientImpl(this.symBotClient);
  }

  @Test
  public void testGetUserIMStreamId() throws SymphonyClientException {
    Mockito.when(this.streamsClient.getUserIMStreamId(anyLong())).thenReturn("test_id");
    assertEquals("test_id", this.streamsClientImpl.getUserIMStreamId(1L));
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserIMStreamIdWithException() throws SymphonyClientException {
    Mockito.when(this.streamsClient.getUserIMStreamId(anyLong())).thenThrow(SymClientException.class);
    this.streamsClientImpl.getUserIMStreamId(1L);
  }

  @Test
  public void testGetUserListIM() throws SymphonyClientException {
    final List<Long> listIds = Arrays.asList(34558909L, 234576L, 4567L);

    Mockito.when(this.streamsClient.getUserListIM(anyList())).thenReturn("test_list_im");
    assertEquals("test_list_im", this.streamsClientImpl.getUserListIM(listIds));
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserListIMWithException() throws SymphonyClientException {
    final List<Long> listIds = Arrays.asList(34558909L, 234576L, 4567L);

    Mockito.when(this.streamsClient.getUserListIM(anyList())).thenThrow(SymClientException.class);
    this.streamsClientImpl.getUserListIM(listIds);
  }

  @Test
  public void testCreateRoom() throws SymphonyClientException {
    final SymphonyRoom symphonyRoom = this.initSymphonyRoom();

    final RoomInfo roomInfo = this.initRoomInfo("Name");

    Mockito.when(this.streamsClient.createRoom(any(Room.class))).thenReturn(roomInfo);

    final SymphonyRoom symphonyRoomResult = this.streamsClientImpl.createRoom(symphonyRoom);
    assertEquals(symphonyRoomResult, symphonyRoom);
  }

  @Test(expected = SymphonyClientException.class)
  public void testCreateRoomWithException() throws SymphonyClientException {
    final SymphonyRoom symphonyRoom = this.initSymphonyRoom();

    Mockito.when(this.streamsClient.createRoom(any(Room.class))).thenThrow(SymClientException.class);
    this.streamsClientImpl.createRoom(symphonyRoom);
  }

  @Test
  public void testAddMemberToRoom() throws SymphonyClientException {
    final Long userId = 1L;
    final String streamId = "strId";

    Mockito.doNothing().when(this.streamsClient).addMemberToRoom(streamId, userId);
    this.streamsClientImpl.addMemberToRoom(streamId, userId);
  }

  @Test(expected = SymphonyClientException.class)
  public void testAddMemberToRoomException() throws SymphonyClientException {
    final Long userId = 1L;
    final String streamId = "strId";

    Mockito.doThrow(SymClientException.class).when(this.streamsClient).addMemberToRoom(streamId, userId);
    this.streamsClientImpl.addMemberToRoom(streamId, userId);
  }

  @Test
  public void testRemoveMemberFromRoom() throws SymphonyClientException {
    final Long userId = 1L;
    final String streamId = "strId";

    Mockito.doNothing().when(this.streamsClient).removeMemberFromRoom(streamId, userId);
    this.streamsClientImpl.removeMemberFromRoom(streamId, userId);
  }

  @Test(expected = SymphonyClientException.class)
  public void testRemoveMemberFromRoomException() throws SymphonyClientException {
    final Long userId = 1L;
    final String streamId = "strId";

    Mockito.doThrow(SymClientException.class).when(this.streamsClient).removeMemberFromRoom(streamId, userId);
    this.streamsClientImpl.removeMemberFromRoom(streamId, userId);
  }

  @Test
  public void testGetRoomInfo() throws SymphonyClientException {
    final String streamId = "streamId";

    final SymphonyRoom symphonyRoom = this.initSymphonyRoom();

    Mockito.when(this.streamsClient.getRoomInfo(streamId)).thenReturn(this.initRoomInfo("Name"));
    final SymphonyRoom symphonyRoomResult = this.streamsClientImpl.getRoomInfo(streamId);

    assertEquals(symphonyRoomResult, symphonyRoom);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetRoomInfoWithException() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.when(this.streamsClient.getRoomInfo(streamId)).thenThrow(SymClientException.class);
    this.streamsClientImpl.getRoomInfo(streamId);
  }

  @Test
  public void testUpdateRoom() throws SymphonyClientException {
    final String steamId = "streamId";
    final SymphonyRoom symphonyRoom = this.initSymphonyRoom();
    final RoomInfo roomInfo = this.initRoomInfo("Name");

    Mockito.when(this.streamsClient.updateRoom(anyString(), any(Room.class))).thenReturn(roomInfo);

    final SymphonyRoom symphonyRoomResult = this.streamsClientImpl.updateRoom(steamId, symphonyRoom);
    assertEquals(symphonyRoomResult, symphonyRoom);
  }

  @Test(expected = SymphonyClientException.class)
  public void testUpdateRoomWithException() throws SymphonyClientException {
    final String steamId = "streamId";
    final SymphonyRoom symphonyRoom = this.initSymphonyRoom();

    Mockito.when(this.streamsClient.updateRoom(anyString(), any(Room.class))).thenThrow(SymClientException.class);
    this.streamsClientImpl.updateRoom(steamId, symphonyRoom);
  }

  @Test
  public void testGetStreamInfo() throws SymphonyClientException {
    final String streamId = "streamId";
    final StreamInfo streamInfo = this.initStreamInfo();
    final SymphonyStream symphonyStream = new SymphonyStream(streamInfo);

    Mockito.when(this.streamsClient.getStreamInfo(anyString())).thenReturn(streamInfo);

    final SymphonyStream symphonyStreamResult = this.streamsClientImpl.getStreamInfo(streamId);
    assertEquals(symphonyStreamResult, symphonyStream);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetStreamInfoWithException() throws SymphonyClientException {
    final String streamId = "streamId";
    final StreamInfo streamInfo = this.initStreamInfo();

    Mockito.when(this.streamsClient.getStreamInfo(anyString())).thenThrow(SymClientException.class);
    this.streamsClientImpl.getStreamInfo(streamId);
  }

  @Test
  public void testGetRoomMembers() throws SymphonyClientException {
    final String streamId = "streamId";

    final List<SymphonyRoomMember> symphonyRoomMembersList = this.initListSymphonyRoomMembers();
    final List<RoomMember> roomMembersList = this.initListRoomMembers();

    Mockito.when(this.streamsClient.getRoomMembers(streamId)).thenReturn(roomMembersList);

    final List<SymphonyRoomMember> symphonyRoomMembersListResult = this.streamsClientImpl.getRoomMembers(streamId);
    assertEquals(symphonyRoomMembersListResult, symphonyRoomMembersList);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetRoomMembersWithException() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.when(this.streamsClient.getRoomMembers(streamId)).thenThrow(SymClientException.class);
    this.streamsClientImpl.getRoomMembers(streamId);
  }

  @Test
  public void testActivateRoom() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.doNothing().when(this.streamsClient).activateRoom(streamId);
    this.streamsClientImpl.activateRoom(streamId);
  }

  @Test(expected = SymphonyClientException.class)
  public void testActivateRoomWithException() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.doThrow(SymClientException.class).when(this.streamsClient).activateRoom(streamId);
    this.streamsClientImpl.activateRoom(streamId);
  }

  @Test
  public void testDeactivateRoom() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.doNothing().when(this.streamsClient).deactivateRoom(streamId);
    this.streamsClientImpl.deactivateRoom(streamId);
  }

  @Test(expected = SymphonyClientException.class)
  public void testDeactivateRoomWithException() throws SymphonyClientException {
    final String streamId = "streamId";

    Mockito.doThrow(SymClientException.class).when(this.streamsClient).deactivateRoom(streamId);
    this.streamsClientImpl.deactivateRoom(streamId);
  }

  @Test
  public void testDemoteUserFromOwner() throws SymphonyClientException {
    final String streamId = "streamId";
    final Long userId = 1L;

    Mockito.doNothing().when(this.streamsClient).demoteUserFromOwner(streamId, userId);
    this.streamsClientImpl.demoteUserFromOwner(streamId, userId);
  }

  @Test(expected = SymphonyClientException.class)
  public void testDemoteUserFromOwnerWithException() throws SymphonyClientException {
    final String streamId = "streamId";
    final Long userId = 1L;

    Mockito.doThrow(SymClientException.class).when(this.streamsClient).demoteUserFromOwner(streamId, userId);
    this.streamsClientImpl.demoteUserFromOwner(streamId, userId);
  }

  @Test
  public void testSearchRooms() throws SymphonyClientException, NoContentException {
    final RoomSearchQuery roomSearchQuery = this.initRoomSearchQuery();
    final SymphonyRoomSearchQuery symphonyRoomSearchQuery = new SymphonyRoomSearchQuery(roomSearchQuery, 1, 2);
    final RoomSearchResult roomSearchResult = this.initRoomSearchResult(roomSearchQuery);
    final SymphonyRoomSearchResult symphonyRoomSearchResultExpected = new SymphonyRoomSearchResult(roomSearchResult);

    Mockito.when(this.streamsClient.searchRooms(any(RoomSearchQuery.class), anyInt(), anyInt())).thenReturn(roomSearchResult);
    final SymphonyRoomSearchResult symphonyRoomSearchResult = this.streamsClientImpl.searchRooms(symphonyRoomSearchQuery);
    assertEquals(symphonyRoomSearchResultExpected, symphonyRoomSearchResult);
  }

  @Test
  public void testSearchRoomsWithNoContent() throws SymphonyClientException, NoContentException {
    final RoomSearchQuery roomSearchQuery = this.initRoomSearchQuery();
    final SymphonyRoomSearchQuery symphonyRoomSearchQuery = new SymphonyRoomSearchQuery(roomSearchQuery, 1, 2);
    final SymphonyRoomSearchResult symphonyRoomSearchResultExpected = new SymphonyRoomSearchResult();

    Mockito.when(this.streamsClient.searchRooms(any(RoomSearchQuery.class), anyInt(), anyInt())).thenThrow(NoContentException.class);
    final SymphonyRoomSearchResult symphonyRoomSearchResult = this.streamsClientImpl.searchRooms(symphonyRoomSearchQuery);
    assertEquals(symphonyRoomSearchResultExpected, symphonyRoomSearchResult);
  }

  @Test(expected = SymphonyClientException.class)
  public void testSearchRoomsWithException() throws SymphonyClientException, NoContentException {
    final RoomSearchQuery roomSearchQuery = this.initRoomSearchQuery();
    final SymphonyRoomSearchQuery symphonyRoomSearchQuery = new SymphonyRoomSearchQuery(roomSearchQuery, 1, 2);

    Mockito.when(this.streamsClient.searchRooms(roomSearchQuery, 1, 2)).thenThrow(SymClientException.class);
    this.streamsClientImpl.searchRooms(symphonyRoomSearchQuery);
  }

  @Test
  public void testGetUserStreamsSuccess() throws SymphonyClientException {
    testUsersStreamsSuccess(true);
    testUsersStreamsSuccess(false);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserStreamsWithException() throws SymphonyClientException {
    testUsersStreamsWithException(true);
    testUsersStreamsWithException(false);
  }

  @Test
  public void testGetUserWallStream() throws SymphonyClientException {
    final com.symphony.bdk.bot.sdk.symphony.model.StreamType streamType = this.initStreamTypeBDK("Room");
    final TypeObject typeObject = this.initTypeObjet("Room");
    final SymphonyStream symphonyStream = this.initSymphonyStream(true, "1", "name", streamType);
    final StreamAttributes streamAttributes = this.initStreamAttributes();
    final RoomName roomName = this.initRoomName("name");
    final StreamListItem streamListItem = this.initStreamListItem("1", true, true, typeObject, streamAttributes, roomName);

    Mockito.when(this.streamsClient.getUserWallStream()).thenReturn(streamListItem);

    final SymphonyStream symphonyStreamResult = this.streamsClientImpl.getUserWallStream();
    assertEquals(symphonyStreamResult, symphonyStream);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserWallStreamWithException() throws SymphonyClientException {

    Mockito.when(this.streamsClient.getUserWallStream()).thenThrow(SymClientException.class);

    this.streamsClientImpl.getUserWallStream();
  }

  private void testUsersStreamsWithException(final boolean includeInactiveStreams) throws SymphonyClientException {
    final List<com.symphony.bdk.bot.sdk.symphony.model.StreamType> streamTypes = getStreamTypes();

    Mockito.when(this.streamsClient.getUserStreams(anyList(), anyBoolean())).thenThrow(SymClientException.class);

    this.streamsClientImpl.getUserStreams(streamTypes, includeInactiveStreams);
  }

  private void testUsersStreamsSuccess(final boolean includeInactiveStreams) throws SymphonyClientException {
    final List<com.symphony.bdk.bot.sdk.symphony.model.StreamType> streamTypes = this.getStreamTypes();
    final List<SymphonyStream> symphonyStreams = this.initSymphonyStreamsList(true, streamTypes);
    final TypeObject typeObject1 = this.initTypeObjet("Room");
    final TypeObject typeObject2 = this.initTypeObjet("IM");
    final TypeObject typeObject3 = this.initTypeObjet("MIM");
    final List<TypeObject> typeObjects = Arrays.asList(typeObject1, typeObject2, typeObject3);
    final List<StreamListItem> streamListItems = this.initListStreamListItem(typeObjects);

    Mockito.when(this.streamsClient.getUserStreams(anyList(), anyBoolean())).thenReturn(streamListItems);

    final List<SymphonyStream> symphonyStreamsResult = this.streamsClientImpl.getUserStreams(streamTypes, includeInactiveStreams);
    assertEquals(symphonyStreamsResult, symphonyStreams);
  }

  private List<com.symphony.bdk.bot.sdk.symphony.model.StreamType> getStreamTypes() {
    final com.symphony.bdk.bot.sdk.symphony.model.StreamType streamType1 = this.initStreamTypeBDK("Room");
    final com.symphony.bdk.bot.sdk.symphony.model.StreamType streamType2 = this.initStreamTypeBDK("IM");
    final com.symphony.bdk.bot.sdk.symphony.model.StreamType streamType3 = this.initStreamTypeBDK("MIM");
    return Arrays.asList(streamType1, streamType2, streamType3);
  }

  private List<StreamListItem> initListStreamListItem(final List<TypeObject> streamTypes) {
    final StreamAttributes streamAttributes = this.initStreamAttributes();
    final RoomName roomName = this.initRoomName("Room");
    final StreamListItem streamListItem1 = this.initStreamListItem("1", true, true, streamTypes.get(0), streamAttributes, roomName);
    final StreamListItem streamListItem2 = this.initStreamListItem("2", true, true, streamTypes.get(1), streamAttributes, roomName);
    final StreamListItem streamListItem3 = this.initStreamListItem("3", true, true, streamTypes.get(2), streamAttributes, roomName);
    final List<StreamListItem> listStreamListItem = Arrays.asList(streamListItem1, streamListItem2, streamListItem3);
    return listStreamListItem;
  }

  private StreamListItem initStreamListItem(final String id, final boolean crossPod, final boolean active, final TypeObject streamType, final StreamAttributes streamAttributes, final RoomName roomName) {
    final StreamListItem streamListItem = new StreamListItem();
    streamListItem.setId(id);
    streamListItem.setCrossPod(crossPod);
    streamListItem.setActive(active);
    streamListItem.setStreamType(streamType);
    streamListItem.setStreamAttributes(streamAttributes);
    streamListItem.setRoomAttributes(roomName);

    return streamListItem;
  }

  private TypeObject initTypeObjet(final String type) {
    final TypeObject typeObject = new TypeObject();
    typeObject.setType(type);
    return typeObject;
  }

  private RoomName initRoomName(final String name) {
    final RoomName roomName = new RoomName();
    roomName.setName(name);
    return roomName;
  }

  private List<SymphonyStream> initSymphonyStreamsList(final boolean isForUser, final List<com.symphony.bdk.bot.sdk.symphony.model.StreamType> streamTypes) {
    final SymphonyStream symphonyStream1 = this.initSymphonyStream(isForUser, "1", "Room", streamTypes.get(0));
    final SymphonyStream symphonyStream2 = this.initSymphonyStream(isForUser, "2", "Room", streamTypes.get(1));
    final SymphonyStream symphonyStream3 = this.initSymphonyStream(isForUser, "3", "Room", streamTypes.get(2));
    final List<SymphonyStream> symphonyStreams = Arrays.asList(symphonyStream1, symphonyStream2, symphonyStream3);
    return symphonyStreams;
  }

  private SymphonyStream initSymphonyStream(final boolean isForUser, final String streamId, final String roomName, final com.symphony.bdk.bot.sdk.symphony.model.StreamType streamType) {
    final List<Long> members = Arrays.asList(1L, 2L, 3L);
    final Long lastMessageDate = isForUser ? null : 3456L;
    final String origin = isForUser ? null : "origin";
    final SymphonyStream symphonyStream = new SymphonyStream(streamId, true, origin, true, lastMessageDate, streamType, members, roomName);
    return symphonyStream;
  }

  private com.symphony.bdk.bot.sdk.symphony.model.StreamType initStreamTypeBDK(final String streamType) {
    return com.symphony.bdk.bot.sdk.symphony.model.StreamType.value(streamType);
  }

  private StreamType initStreamType(final String streamType) {
    final StreamType streamTypeResult = new StreamType();
    streamTypeResult.setType(StreamTypes.valueOf(streamType.toUpperCase()));
    return streamTypeResult;
  }

  private RoomSearchResult initRoomSearchResult(final RoomSearchQuery roomSearchQuery) {
    final RoomSearchResult roomSearchResult = new RoomSearchResult();
    roomSearchResult.setCount(1);
    roomSearchResult.setSkip(1);
    roomSearchResult.setLimit(1);
    roomSearchResult.setQuery(roomSearchQuery);

    final RoomInfo roomInfo1 = this.initRoomInfo("room1");
    final RoomInfo roomInfo2 = this.initRoomInfo("room2");
    final List<RoomInfo> roomInfos = Arrays.asList(roomInfo1, roomInfo2);
    roomSearchResult.setRooms(roomInfos);

    return roomSearchResult;
  }

  private RoomSearchQuery initRoomSearchQuery() {
    final RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    roomSearchQuery.setQuery("query");

    final List<String> labels = new ArrayList<>();
    labels.add("label1");
    labels.add("label2");
    labels.add("label3");
    roomSearchQuery.setLabels(labels);

    roomSearchQuery.setActive(true);
    roomSearchQuery.setPrivate(false);
    roomSearchQuery.setCreator(new NumericId(5L));
    roomSearchQuery.setOwner(new NumericId(1L));
    roomSearchQuery.setMember(new NumericId(2L));
    roomSearchQuery.setSortOrder("asc");
    roomSearchQuery.setSubType("type");

    return roomSearchQuery;
  }

  private List<SymphonyRoomMember> initListSymphonyRoomMembers() {
    final RoomMember roomMember1 = createRoomMember(1L, true, 5678L);
    final RoomMember roomMember2 = createRoomMember(2L, false, 8590L);
    final RoomMember roomMember3 = createRoomMember(3L, false, 53647L);

    final SymphonyRoomMember symphonyRoomMember1 = new SymphonyRoomMember(roomMember1);
    final SymphonyRoomMember symphonyRoomMember2 = new SymphonyRoomMember(roomMember2);
    final SymphonyRoomMember symphonyRoomMember3 = new SymphonyRoomMember(roomMember3);

    final List<SymphonyRoomMember> symphonyRoomMembers = Arrays.asList(symphonyRoomMember1, symphonyRoomMember2, symphonyRoomMember3);
    return symphonyRoomMembers;
  }

  private List<RoomMember> initListRoomMembers() {
    final RoomMember roomMember1 = createRoomMember(1L, true, 5678L);
    final RoomMember roomMember2 = createRoomMember(2L, false, 8590L);
    final RoomMember roomMember3 = createRoomMember(3L, false, 53647L);

    final List<RoomMember> symphonyRoomMembers = Arrays.asList(roomMember1, roomMember2, roomMember3);
    return symphonyRoomMembers;
  }

  private RoomMember createRoomMember(final long id, final boolean isOwner, final long joinDate) {
    final RoomMember roomMember1 = new RoomMember();
    roomMember1.setId(id);
    roomMember1.setOwner(isOwner);
    roomMember1.setJoinDate(joinDate);
    return roomMember1;
  }

  private StreamInfo initStreamInfo() {
    final StreamInfo streamInfo = new StreamInfo();
    streamInfo.setId("streamId");
    streamInfo.setCrossPod(true);
    streamInfo.setOrigin("origin");
    streamInfo.setActive(true);
    streamInfo.setLastMessageDate(456789L);

    final StreamType streamType = this.initStreamType("Room");
    streamInfo.setStreamType(streamType);

    final StreamAttributes streamAttributes = initStreamAttributes();
    streamInfo.setStreamAttributes(streamAttributes);

    final RoomName roomAttributes = new RoomName();
    roomAttributes.setName("room");
    streamInfo.setRoomAttributes(roomAttributes);

    return streamInfo;
  }

  private StreamAttributes initStreamAttributes() {
    final StreamAttributes streamAttributes = new StreamAttributes();
    final List<Long> members = Arrays.asList(1L, 2L, 3L);
    streamAttributes.setMembers(members);
    return streamAttributes;
  }

  private RoomInfo initRoomInfo(final String roomName) {
    final RoomInfo roomInfo = new RoomInfo();
    final Room roomAttributes = this.initRoomAttributes(roomName);
    final RoomSystemInfo roomSystemInfo = this.initRoomSystemInfo();

    roomInfo.setRoomAttributes(roomAttributes);
    roomInfo.setRoomSystemInfo(roomSystemInfo);

    return roomInfo;
  }

  private RoomSystemInfo initRoomSystemInfo() {
    final RoomSystemInfo roomSystemInfo = new RoomSystemInfo();
    roomSystemInfo.setId("id");
    roomSystemInfo.setCreationDate(56789L);
    roomSystemInfo.setCreatedByUserId(8L);
    roomSystemInfo.setActive(true);
    return roomSystemInfo;
  }

  private Room initRoomAttributes(final String name) {
    final Room room = new Room();
    room.setName(name);
    room.setDescription("Description");
    room.setMembersCanInvite(true);
    room.setDiscoverable(true);
    room.setPublic(true);
    room.setReadOnly(true);
    room.setCopyProtected(true);
    room.setCrossPod(true);
    room.setViewHistory(true);
    room.setMultiLateralRoom(true);

    final Keyword keyword1 = new Keyword();
    keyword1.setKey("key1");
    keyword1.setValue("value1");

    final Keyword keyword2 = new Keyword();
    keyword2.setKey("key2");
    keyword2.setValue("value2");

    final Keyword keyword3 = new Keyword();
    keyword3.setKey("key3");
    keyword3.setValue("value3");

    final List<Keyword> keywords = Arrays.asList(keyword1, keyword2, keyword3);

    room.setKeywords(keywords);

    return room;
  }

  private SymphonyRoom initSymphonyRoom(){
    final Map<String,String> keywords = new HashMap<>();
    keywords.put("key1", "value1");
    keywords.put("key2", "value2");
    keywords.put("key3", "value3");

    return new SymphonyRoom("Name",
        "Description",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        keywords,
        "id",
        56789L,
        8L,
        true);
  }
}
