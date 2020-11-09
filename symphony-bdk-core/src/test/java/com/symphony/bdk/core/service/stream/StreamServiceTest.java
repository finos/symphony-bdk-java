package com.symphony.bdk.core.service.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.pagination.model.StreamPaginationAttribute;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.RoomDetail;
import com.symphony.bdk.gen.api.model.ShareContent;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.StreamType;
import com.symphony.bdk.gen.api.model.UserId;
import com.symphony.bdk.gen.api.model.V2AdminStreamFilter;
import com.symphony.bdk.gen.api.model.V2AdminStreamInfo;
import com.symphony.bdk.gen.api.model.V2AdminStreamList;
import com.symphony.bdk.gen.api.model.V2MemberInfo;
import com.symphony.bdk.gen.api.model.V2MembershipList;
import com.symphony.bdk.gen.api.model.V2Message;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V3RoomSearchResults;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamServiceTest {

  private static final String V1_IM_CREATE = "/pod/v1/im/create";
  private static final String V1_IM_CREATE_ADMIN = "/pod/v1/admin/im/create";
  private static final String V1_ROOM_SET_ACTIVE = "/pod/v1/room/{id}/setActive";
  private static final String V1_ROOM_SET_ACTIVE_ADMIN = "/pod/v1/admin/room/{id}/setActive";
  private static final String V1_STREAM_LIST = "/pod/v1/streams/list";
  private static final String V1_ADD_MEMBER_TO_ROOM = "/pod/v1/room/{id}/membership/add";
  private static final String V1_REMOVE_MEMBER_FROM_ROOM = "/pod/v1/room/{id}/membership/remove";
  private static final String V1_STREAM_MEMBERS = "/pod/v1/admin/stream/{id}/membership/list";
  private static final String V1_PROMOTE_MEMBER = "/pod/v1/room/{id}/membership/promoteOwner";
  private static final String V1_DEMOTE_MEMBER = "/pod/v1/room/{id}/membership/demoteOwner";
  private static final String V2_STREAM_INFO = "/pod/v2/streams/{sid}/info";
  private static final String V2_STREAM_LIST_ADMIN = "/pod/v2/admin/streams/list";
  private static final String V2_ROOM_MEMBERS = "/pod/v2/room/{id}/membership/list";
  private static final String V3_ROOM_CREATE = "/pod/v3/room/create";
  private static final String V3_ROOM_SEARCH = "/pod/v3/room/search";
  private static final String V3_ROOM_INFO = "/pod/v3/room/{id}/info";
  private static final String V3_ROOM_UPDATE = "/pod/v3/room/{id}/update";
  private static final String V3_SHARE = "/agent/v3/stream/{sid}/share";

  private StreamService service;
  private MockApiClient mockApiClient;
  private RoomMembershipApi spyRoomMembershipApi;

  @BeforeEach
  void setUp() {
    this.mockApiClient = new MockApiClient();
    AuthSession authSession = mock(AuthSession.class);
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    ApiClient agentClient = mockApiClient.getApiClient("/agent");
    RoomMembershipApi roomMembershipApi = new RoomMembershipApi(podClient);
    this.spyRoomMembershipApi = spy(roomMembershipApi);
    this.service =
        new StreamService(new StreamsApi(podClient), this.spyRoomMembershipApi, new ShareApi(agentClient),
            authSession, new RetryWithRecoveryBuilder<>());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void createIMorMIMTest() {
    this.mockApiClient.onPost(V1_IM_CREATE, "{\"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\"}");

    Stream stream = this.service.create(Arrays.asList(7215545078541L, 7215512356741L));

    assertEquals(stream.getId(), "xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA");
  }

  @Test
  void createIMorMIMTestFailed() {
    this.mockApiClient.onPost(400, V1_IM_CREATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.create(Arrays.asList(7215545078541L, 7215512356741L)));
  }

  @Test
  void createIMTest() {
    this.mockApiClient.onPost(V1_IM_CREATE, "{\"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\"}");

    Stream stream = this.service.create(7215545078541L);

    assertEquals(stream.getId(), "xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA");
  }

  @Test
  void createIMTestFailed() {
    this.mockApiClient.onPost(400, V1_IM_CREATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.create(7215545078541L));
  }

  @Test
  void createRoomChatTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_CREATE, JsonHelper.readFromClasspath(
        "/stream/v3_room_detail.json"));

    V3RoomDetail roomDetail = this.service.create(new V3RoomAttributes());

    assertEquals(roomDetail.getRoomAttributes().getName(), "API room");
    assertEquals(roomDetail.getRoomAttributes().getDescription(), "Created via the API");
    assertEquals(roomDetail.getRoomSystemInfo().getId(), "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA");
  }

  @Test
  void createRoomChatTestFailed() {
    this.mockApiClient.onPost(400, V3_ROOM_CREATE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.create(new V3RoomAttributes()));
  }

  @Test
  void searchRoomsTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_SEARCH, JsonHelper.readFromClasspath("/stream/room_search.json"));

    V3RoomSearchResults searchResults = this.service.searchRooms(new V2RoomSearchCriteria());

    assertEquals(searchResults.getCount(), 2);
    assertEquals(searchResults.getRooms().get(0).getRoomAttributes().getName(), "Automobile Industry Room");
  }

  @Test
  void searchRoomsTestFailed() {
    this.mockApiClient.onPost(400, V3_ROOM_SEARCH, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.searchRooms(new V2RoomSearchCriteria()));
  }

  @Test
  void searchRoomsSkipLimitTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_SEARCH, JsonHelper.readFromClasspath("/stream/room_search.json"));

    V3RoomSearchResults searchResults =
        this.service.searchRooms(new V2RoomSearchCriteria(), new PaginationAttribute(0, 100));

    assertEquals(searchResults.getCount(), 2);
    assertEquals(searchResults.getRooms().get(0).getRoomAttributes().getName(), "Automobile Industry Room");
  }

  @Test
  void searchAllRoomsTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_SEARCH, JsonHelper.readFromClasspath("/stream/room_search.json"));

    List<V3RoomDetail> searchResults =
        this.service.searchAllRooms(new V2RoomSearchCriteria()).collect(Collectors.toList());

    assertEquals(searchResults.size(), 2);
    assertEquals(searchResults.get(0).getRoomAttributes().getName(), "Automobile Industry Room");
  }

  @Test
  void searchAllRoomsStreamPaginationTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_SEARCH, JsonHelper.readFromClasspath("/stream/room_search.json"));

    List<V3RoomDetail> searchResults =
        this.service.searchAllRooms(new V2RoomSearchCriteria(), new StreamPaginationAttribute(100, 100))
            .collect(Collectors.toList());

    assertEquals(searchResults.size(), 2);
    assertEquals(searchResults.get(0).getRoomAttributes().getName(), "Automobile Industry Room");
  }

  @Test
  void getRoomInfoTest() throws IOException {
    this.mockApiClient.onGet(V3_ROOM_INFO.replace("{id}", "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA"),
        JsonHelper.readFromClasspath("/stream/v3_room_detail.json"));

    V3RoomDetail roomDetail = this.service.getRoomInfo("bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA");

    assertEquals(roomDetail.getRoomAttributes().getName(), "API room");
    assertEquals(roomDetail.getRoomAttributes().getDescription(), "Created via the API");
    assertEquals(roomDetail.getRoomSystemInfo().getId(), "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA");
  }

  @Test
  void getRoomInfoTestFailed() {
    this.mockApiClient.onGet(400, V3_ROOM_INFO.replace("{id}", "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getRoomInfo("bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA"));
  }

  @Test
  void setRoomActiveTest() throws IOException {
    this.mockApiClient.onPost(V1_ROOM_SET_ACTIVE.replace("{id}", "HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA"),
        JsonHelper.readFromClasspath("/stream/room_detail.json"));

    RoomDetail roomDetail = this.service.setRoomActive("HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA", true);

    assertEquals(roomDetail.getRoomSystemInfo().getActive(), true);
  }

  @Test
  void setRoomActiveTestFailed() {
    this.mockApiClient.onPost(400, V1_ROOM_SET_ACTIVE.replace("{id}", "HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.setRoomActive("HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA", true));
  }

  @Test
  void updateRoomTest() throws IOException {
    this.mockApiClient.onPost(V3_ROOM_UPDATE.replace("{id}", "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA"),
        JsonHelper.readFromClasspath("/stream/v3_room_detail.json"));

    V3RoomDetail roomDetail = this.service.updateRoom("bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA", new V3RoomAttributes());

    assertEquals(roomDetail.getRoomAttributes().getName(), "API room");
    assertEquals(roomDetail.getRoomAttributes().getDescription(), "Created via the API");
    assertEquals(roomDetail.getRoomSystemInfo().getId(), "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA");
  }

  @Test
  void updateRoomTestFailed() {
    this.mockApiClient.onPost(400, V3_ROOM_UPDATE.replace("{id}", "bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA"), "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.updateRoom("bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA", new V3RoomAttributes()));
  }

  @Test
  void listStreamsTest() throws IOException {
    this.mockApiClient.onPost(V1_STREAM_LIST, JsonHelper.readFromClasspath("/stream/list_stream.json"));

    List<StreamAttributes> streams =
        this.service.listStreams(new StreamFilter().addStreamTypesItem(new StreamType().type(
            StreamType.TypeEnum.IM)));

    assertEquals(streams.size(), 1);
    assertEquals(streams.get(0).getId(), "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA");
  }

  @Test
  void listStreamsTestFailed() {
    this.mockApiClient.onPost(400, V1_STREAM_LIST, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listStreams(new StreamFilter()));
  }

  @Test
  void listStreamsWithSkipLimit() throws IOException {
    this.mockApiClient.onPost(V1_STREAM_LIST, JsonHelper.readFromClasspath("/stream/list_stream.json"));

    List<StreamAttributes> streams =
        this.service.listStreams(new StreamFilter().addStreamTypesItem(new StreamType().type(
            StreamType.TypeEnum.IM)), new PaginationAttribute(0, 100));
    assertEquals(streams.size(), 1);
    assertEquals(streams.get(0).getId(), "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA");
  }

  @Test
  void listAllStreamsTest() throws IOException {
    this.mockApiClient.onPost(V1_STREAM_LIST, JsonHelper.readFromClasspath("/stream/list_stream.json"));

    List<StreamAttributes> streams =
        this.service.listAllStreams(new StreamFilter().addStreamTypesItem(new StreamType().type(
            StreamType.TypeEnum.IM))).collect(Collectors.toList());
    assertEquals(streams.size(), 1);
    assertEquals(streams.get(0).getId(), "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA");
  }

  @Test
  void listAllStreamsPaginationTest() throws IOException {
    this.mockApiClient.onPost(V1_STREAM_LIST, JsonHelper.readFromClasspath("/stream/list_stream.json"));

    List<StreamAttributes> streams =
        this.service.listAllStreams(new StreamFilter().addStreamTypesItem(new StreamType().type(
            StreamType.TypeEnum.IM)), new StreamPaginationAttribute(100, 100)).collect(Collectors.toList());
    assertEquals(streams.size(), 1);
    assertEquals(streams.get(0).getId(), "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA");
  }

  @Test
  void getStreamInfoTest() throws IOException {
    this.mockApiClient.onGet(V2_STREAM_INFO.replace("{sid}", "p9B316LKDto7iOECc8Xuz3qeWsc0bdA"),
        JsonHelper.readFromClasspath("/stream/v2_stream_attributes.json"));

    V2StreamAttributes stream = this.service.getStreamInfo("p9B316LKDto7iOECc8Xuz3qeWsc0bdA");

    assertEquals(stream.getId(), "p9B316LKDto7iOECc8Xuz3qeWsc0bdA");
    assertEquals(stream.getOrigin(), "INTERNAL");
  }

  @Test
  void getStreamInfoTestFailed() {
    this.mockApiClient.onGet(400, V2_STREAM_INFO.replace("{sid}", "p9B316LKDto7iOECc8Xuz3qeWsc0bdA"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getStreamInfo("p9B316LKDto7iOECc8Xuz3qeWsc0bdA"));
  }

  @Test
  void createAdminIMorMIMTest() {
    this.mockApiClient.onPost(V1_IM_CREATE_ADMIN, "{\n\"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\"\n}");

    Stream stream = this.service.createInstantMessageAdmin(Arrays.asList(7215545078541L, 7215545078461L));

    assertEquals(stream.getId(), "xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA");
  }

  @Test
  void createAdminIMorMIMTestFailed() {
    this.mockApiClient.onPost(400, V1_IM_CREATE_ADMIN, "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.createInstantMessageAdmin(Arrays.asList(7215545078541L, 7215545078461L)));
  }

  @Test
  void setRoomActiveAdminTest() throws IOException {
    this.mockApiClient.onPost(V1_ROOM_SET_ACTIVE_ADMIN.replace("{id}", "HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA"), JsonHelper
        .readFromClasspath("/stream/room_detail.json"));

    RoomDetail roomDetail = this.service.setRoomActiveAdmin("HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA", true);

    assertEquals(roomDetail.getRoomSystemInfo().getActive(), true);
  }

  @Test
  void setRoomActiveAdminTestFailed() {
    this.mockApiClient.onPost(400, V1_ROOM_SET_ACTIVE_ADMIN.replace("{id}", "HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA"),
        "{}");

    assertThrows(ApiRuntimeException.class,
        () -> this.service.setRoomActiveAdmin("HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA", true));
  }

  @Test
  void listStreamsAdminTest() throws IOException {
    this.mockApiClient.onPost(V2_STREAM_LIST_ADMIN, JsonHelper.readFromClasspath("/stream/v2_admin_stream_list.json"));

    V2AdminStreamList streamList = this.service.listStreamsAdmin(new V2AdminStreamFilter());

    assertEquals(streamList.getCount(), 4);
    assertEquals(streamList.getStreams().get(0).getId(), "Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA");
    assertEquals(streamList.getStreams().get(1).getId(), "_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA");
    assertEquals(streamList.getStreams().get(2).getId(), "fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA");
  }

  @Test
  void listStreamAdminTestFailed() {
    this.mockApiClient.onPost(400, V2_STREAM_LIST_ADMIN, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listStreamsAdmin(new V2AdminStreamFilter()));
  }

  @Test
  void listStreamsAdminSkipLimitTest() throws IOException {
    this.mockApiClient.onPost(V2_STREAM_LIST_ADMIN, JsonHelper.readFromClasspath("/stream/v2_admin_stream_list.json"));

    V2AdminStreamList streamList =
        this.service.listStreamsAdmin(new V2AdminStreamFilter(), new PaginationAttribute(0, 100));

    assertEquals(streamList.getCount(), 4);
    assertEquals(streamList.getStreams().get(0).getId(), "Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA");
    assertEquals(streamList.getStreams().get(1).getId(), "_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA");
    assertEquals(streamList.getStreams().get(2).getId(), "fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA");
  }

  @Test
  void listAllStreamsAdminTest() throws IOException {
    this.mockApiClient.onPost(V2_STREAM_LIST_ADMIN, JsonHelper.readFromClasspath("/stream/v2_admin_stream_list.json"));

    List<V2AdminStreamInfo> streamList =
        this.service.listAllStreamsAdmin(new V2AdminStreamFilter()).collect(Collectors.toList());

    assertEquals(streamList.size(), 4);
    assertEquals(streamList.get(0).getId(), "Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA");
    assertEquals(streamList.get(1).getId(), "_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA");
    assertEquals(streamList.get(2).getId(), "fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA");
  }

  @Test
  void listAllStreamsAdminPaginationTest() throws IOException {
    this.mockApiClient.onPost(V2_STREAM_LIST_ADMIN, JsonHelper.readFromClasspath("/stream/v2_admin_stream_list.json"));

    List<V2AdminStreamInfo> streamList =
        this.service.listAllStreamsAdmin(new V2AdminStreamFilter(), new StreamPaginationAttribute(100, 100))
            .collect(Collectors.toList());

    assertEquals(streamList.size(), 4);
    assertEquals(streamList.get(0).getId(), "Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA");
    assertEquals(streamList.get(1).getId(), "_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA");
    assertEquals(streamList.get(2).getId(), "fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA");
  }

  @Test
  void listStreamMembersTest() throws IOException {
    this.mockApiClient.onGet(V1_STREAM_MEMBERS.replace("{id}", "1234"),
        JsonHelper.readFromClasspath("/stream/v2_membership_list.json"));

    V2MembershipList membersList = this.service.listStreamMembers("1234");

    assertEquals(membersList.getCount(), 2);
    assertEquals(membersList.getMembers().get(0).getJoinDate(), 1485366753320L);
    assertEquals(membersList.getMembers().get(1).getJoinDate(), 1485366753279L);
  }

  @Test
  void listStreamMembersTestFailed() {
    this.mockApiClient.onGet(400, V1_STREAM_MEMBERS.replace("{id}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listStreamMembers("1234"));
  }

  @Test
  void listStreamMembersSkipLimitTest() throws IOException {
    this.mockApiClient.onGet(V1_STREAM_MEMBERS.replace("{id}", "1234"),
        JsonHelper.readFromClasspath("/stream/v2_membership_list.json"));

    V2MembershipList membersList = this.service.listStreamMembers("1234", new PaginationAttribute(0, 100));

    assertEquals(membersList.getCount(), 2);
    assertEquals(membersList.getMembers().get(0).getJoinDate(), 1485366753320L);
    assertEquals(membersList.getMembers().get(1).getJoinDate(), 1485366753279L);
  }

  @Test
  void listAllStreamMembersTest() throws IOException {
    this.mockApiClient.onGet(V1_STREAM_MEMBERS.replace("{id}", "1234"),
        JsonHelper.readFromClasspath("/stream/v2_membership_list.json"));

    List<V2MemberInfo> membersList = this.service.listAllStreamMembers("1234").collect(Collectors.toList());

    assertEquals(membersList.size(), 2);
    assertEquals(membersList.get(0).getJoinDate(), 1485366753320L);
    assertEquals(membersList.get(1).getJoinDate(), 1485366753279L);
  }

  @Test
  void listAllStreamMembersPaginationTest() throws IOException {
    this.mockApiClient.onGet(V1_STREAM_MEMBERS.replace("{id}", "1234"),
        JsonHelper.readFromClasspath("/stream/v2_membership_list.json"));

    List<V2MemberInfo> membersList = this.service.listAllStreamMembers("1234", new StreamPaginationAttribute(100, 100)).collect(Collectors.toList());

    assertEquals(membersList.size(), 2);
    assertEquals(membersList.get(0).getJoinDate(), 1485366753320L);
    assertEquals(membersList.get(1).getJoinDate(), 1485366753279L);
  }

  @Test
  void listRoomMemberTest() {
    this.mockApiClient.onGet(V2_ROOM_MEMBERS.replace("{id}", "1234"), "[\n"
        + "  {\n"
        + "    \"id\": 7078106103900,\n"
        + "    \"owner\": false,\n"
        + "    \"joinDate\": 1461430710531\n"
        + "  },\n"
        + "  {\n"
        + "    \"id\": 7078106103809,\n"
        + "    \"owner\": true,\n"
        + "    \"joinDate\": 1461426797875\n"
        + "  }\n"
        + "]");

    List<MemberInfo> memberInfos = this.service.listRoomMembers("1234");

    assertEquals(memberInfos.size(), 2);
    assertEquals(memberInfos.get(0).getId(), 7078106103900L);
    assertFalse(memberInfos.get(0).getOwner());
    assertEquals(memberInfos.get(1).getId(), 7078106103809L);
    assertTrue(memberInfos.get(1).getOwner());
  }

  @Test
  void listRoomMemberTestFailed() {
    this.mockApiClient.onGet(400, V2_ROOM_MEMBERS.replace("{id}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listRoomMembers("1234"));
  }

  @Test
  void addMemberToRoomTest() throws ApiException {
    this.mockApiClient.onPost(V1_ADD_MEMBER_TO_ROOM.replace("{id}", "1234"), "{}");

    this.service.addMemberToRoom(12345L, "1234");

    verify(this.spyRoomMembershipApi).v1RoomIdMembershipAddPost(eq("1234"), eq("1234"), eq(new UserId().id(12345L)));
  }

  @Test
  void addMemberToRoomTestFailed() {
    this.mockApiClient.onPost(400, V1_ADD_MEMBER_TO_ROOM.replace("{id}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.addMemberToRoom(12345L, "1234"));
  }

  @Test
  void removeMemberFromRoomTest() throws ApiException {
    this.mockApiClient.onPost(V1_REMOVE_MEMBER_FROM_ROOM.replace("{id}", "1234"), "{}");

    this.service.removeMemberFromRoom(12345L, "1234");

    verify(this.spyRoomMembershipApi).v1RoomIdMembershipRemovePost(eq("1234"), eq("1234"), eq(new UserId().id(12345L)));
  }

  @Test
  void removeMemberFromRoomTestFailed() {
    this.mockApiClient.onPost(400, V1_REMOVE_MEMBER_FROM_ROOM.replace("{id}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.removeMemberFromRoom(12345L, "1234"));
  }

  @Test
  void shareTest() throws IOException {
    this.mockApiClient.onPost(V3_SHARE.replace("{sid}", "1234"), JsonHelper.readFromClasspath("/stream/v3_share.json"));

    V2Message message = this.service.share("1234", new ShareContent());

    assertEquals(message.getId(), "HsaTBf7ClJRWvzNWaCp_4H___qlrh4WVdA");
    assertEquals(message.getFromUserId(), 7696581430532L);
    assertEquals(message.getStreamId(), "7w68A8sAG_qv1GwVc9ODzX___ql_RJ6zdA");
  }

  @Test
  void shareTestFailed() {
    this.mockApiClient.onPost(400, V3_SHARE.replace("{sid}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.share("1234", new ShareContent()));
  }

  @Test
  void demoteUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_DEMOTE_MEMBER.replace("{id}", "1234"), "{}");

    this.service.demoteUserToRoomParticipant(12345L, "1234");

    verify(this.spyRoomMembershipApi).v1RoomIdMembershipDemoteOwnerPost(eq("1234"), eq("1234"),
        eq(new UserId().id(12345L)));
  }

  @Test
  void demoteUserTestFailed() {
    this.mockApiClient.onPost(400, V1_DEMOTE_MEMBER.replace("{id}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.demoteUserToRoomParticipant(12345L, "1234"));
  }

  @Test
  void promoteUserTest() throws ApiException {
    this.mockApiClient.onPost(V1_PROMOTE_MEMBER.replace("{id}", "1234"), "{}");

    this.service.promoteUserToRoomOwner(12345L, "1234");

    verify(this.spyRoomMembershipApi).v1RoomIdMembershipPromoteOwnerPost(eq("1234"), eq("1234"),
        eq(new UserId().id(12345L)));
  }
}
