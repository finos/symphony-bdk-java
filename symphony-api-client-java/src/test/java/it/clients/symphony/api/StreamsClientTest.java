package it.clients.symphony.api;

import authentication.AuthEndpointConstants;
import clients.symphony.api.StreamsClient;
import clients.symphony.api.constants.PodConstants;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import exceptions.SymClientException;
import it.commons.BotTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import model.*;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class StreamsClientTest extends BotTest {
  private StreamsClient streamsClient;

  @Before
  public void initClient() {
    streamsClient = new StreamsClient(symBotClient);
  }

  @Test
  public void getUserListIMSuccess() {
    stubFor(post(urlEqualTo(PodConstants.GETIM))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\" }")));

    List<Long> userIdList = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    String streamId = streamsClient.getUserListIM(userIdList);

    assertNotNull(streamId);
  }

  @Test
  public void createRoomSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.CREATEROOM))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/create_room.json"))));

    Room room = new Room();
    room.setName("API room");
    room.setPublic(false);
    room.setReadOnly(false);
    RoomInfo roomInfo = streamsClient.createRoom(room);

    assertNotNull(roomInfo);
    assertNotNull(roomInfo.getRoomAttributes());
    assertEquals("API room", roomInfo.getRoomAttributes().getName());
    assertEquals(false, roomInfo.getRoomAttributes().getPublic());
    assertEquals(false, roomInfo.getRoomAttributes().getReadOnly());
  }

  @Test
  public void addMemberToRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ADDMEMBER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"format\": \"TEXT\", \"message\": \"Member added\" }")));

    streamsClient.addMemberToRoom("1", 1L);
  }

  @Test
  public void removeMemberFromRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.REMOVEMEMBER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"format\": \"TEXT\", \"message\": \"Member removed\" }")));

    streamsClient.removeMemberFromRoom("1", 1L);
  }

  @Test
  public void getRoomInfoSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETROOMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/room_info.json"))));

    RoomInfo roomInfo = streamsClient.getRoomInfo("1");

    assertNotNull(roomInfo);
    assertNotNull(roomInfo.getRoomAttributes());
    assertEquals("Room V3", roomInfo.getRoomAttributes().getName());
    assertEquals(false, roomInfo.getRoomAttributes().getPublic());
    assertEquals(false, roomInfo.getRoomAttributes().getReadOnly());
  }

  @Test
  public void updateRoomSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.UPDATEROOMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/update_room.json"))));

    Room room = new Room();
    room.setName("Updating room");
    room.setPublic(true);
    room.setReadOnly(false);
    RoomInfo roomInfo = streamsClient.updateRoom("1", room);

    assertNotNull(roomInfo);
    assertNotNull(roomInfo.getRoomAttributes());
    assertEquals("Updating room", roomInfo.getRoomAttributes().getName());
    assertEquals(true, roomInfo.getRoomAttributes().getPublic());
    assertEquals(false, roomInfo.getRoomAttributes().getReadOnly());
  }

  @Test
  public void getStreamInfoSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETSTREAMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/stream_info.json"))));

    StreamInfo streamInfo = streamsClient.getStreamInfo("1");

    assertNotNull(streamInfo);
    assertEquals("p9B316LKDto7iOECc8Xuz3qeWsc0bdA", streamInfo.getId());
  }

  @Test
  public void getRoomMembersSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETROOMMEMBERS.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/get_room_member.json"))));

    List<RoomMember> membership = streamsClient.getRoomMembers("1");

    assertNotNull(membership);
    assertEquals(2, membership.size());
  }

  @Test
  public void activateRoomSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.SETACTIVE.replace("{id}", "1").concat("?active=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("active", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/set_room_active.json"))));

    streamsClient.activateRoom("1");
  }

  @Test
  public void deactivateRoomSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.SETACTIVE.replace("{id}", "1").concat("?active=false")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("active", equalTo("false"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/set_room_active.json"))));

    streamsClient.deactivateRoom("1");
  }

  @Test
  public void promoteUserToOwnerSuccess() {
    stubFor(post(urlEqualTo(PodConstants.PROMOTEOWNER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"format\": \"TEXT\", \"message\": \"Member promoted to owner\" }")));

    streamsClient.promoteUserToOwner("1", 1L);
  }

  @Test
  public void demoteUserFromOwnerSuccess() {
    stubFor(post(urlEqualTo(PodConstants.DEMOTEOWNER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"format\": \"TEXT\", \"message\": \"Member demoted to participant\" }")));

    streamsClient.demoteUserFromOwner("1", 1L);
  }

  @Test
  public void searchRoomsSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.SEARCHROOMS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/search_room.json"))));

    RoomSearchQuery query = new RoomSearchQuery();
    query.setQuery("automobile");
    query.setLabels(Stream.of("industry").collect(Collectors.toList()));
    query.setActive(true);

    try {
      RoomSearchResult result = streamsClient.searchRooms(query, 0, 0);
      assertNotNull(result);
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserStreamsSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.LISTUSERSTREAMS + "?skip=0&limit=50"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/list_user_streams.json"))));

    List<String> types = Stream.of("MIM", "ROOM", "POST").collect(Collectors.toList());
    List<StreamListItem> streamsList = streamsClient.getUserStreams(types, true);

    assertNotNull(streamsList);
    assertEquals(1, streamsList.size());
  }

  @Test
  public void getListUserStreamsUnAuthorizedSuccessRetry() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.LISTUSERSTREAMS + "?skip=0&limit=50"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get List User Streams")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{ \"message\":  \"Can't retrieve session from ID 688787d8ff144c502c7\" }"))
        .willSetStateTo("Failed first time"));

    stubPost(AuthEndpointConstants.KEY_AUTH_PATH_RSA,
        "{ \"token\": \"0100e4feOiJSUzUxMiJ97oqGf729d1866f\", \"name\": \"sessionToken\" }");
    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"token\": \"eyJhbGciOiJSUzUxMiJ97oqG1Kd28l1FpQ\", \"name\": \"sessionToken\" }");

    stubFor(post(urlEqualTo(PodConstants.LISTUSERSTREAMS + "?skip=0&limit=50"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .inScenario("Get List User Streams")
        .whenScenarioStateIs("Failed first time")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/streams/list_user_streams.json"))));

    List<String> streamTypes = Arrays.asList("IM", "POST");
    List<StreamListItem> streams = streamsClient.getUserStreams(streamTypes, true);
    assertEquals(streams.size(), 1);
    assertEquals(streams.get(0).getId(), "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA");
  }

  @Test(expected = SymClientException.class)
  public void getListUserStreamsUnauthorized() {
    stubFor(post(urlEqualTo(PodConstants.LISTUSERSTREAMS + "?skip=0&limit=50"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withBody("{ \"message\":  \"Can't retrieve session from ID 688787d8ff144c502c7f5cffaafe2cc588d86079f9de88304c26b0cb99ce91c6\" }")));

    stubPost(AuthEndpointConstants.SESSION_AUTH_PATH_RSA,
        "{ \"error\": \"Service unavailable\" }",
        503);
    List<String> streamTypes = Arrays.asList("IM", "POST");
    streamsClient.getUserStreams(streamTypes, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void failToGetUserStreamsWithIllegalSkipValue() {
    this.streamsClient.getUserStreams(Collections.emptyList(), true, -1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void failToGetUserStreamsWithIllegalLimitValue() {
    this.streamsClient.getUserStreams(Collections.emptyList(), true, 0, -1);
  }
}
