package it.clients.symphony.api;

import clients.symphony.api.StreamsClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
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
            .withBody("{\r\n" +
                "  \"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\"\r\n" +
                "}")));

    List<Long> userIdList = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    String streamId = streamsClient.getUserListIM(userIdList);

    assertNotNull(streamId);
  }

  @Test
  public void createRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.CREATEROOM))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"roomAttributes\": {\r\n" +
                "        \"name\": \"API room\",\r\n" +
                "        \"keywords\": [\r\n" +
                "            {\r\n" +
                "                \"key\": \"region\",\r\n" +
                "                \"value\": \"EMEA\"\r\n" +
                "            },\r\n" +
                "            {\r\n" +
                "                \"key\": \"lead\",\r\n" +
                "                \"value\": \"Bugs Bunny\"\r\n" +
                "            }\r\n" +
                "        ],\r\n" +
                "        \"description\": \"Created via the API\",\r\n" +
                "        \"membersCanInvite\": true,\r\n" +
                "        \"discoverable\": false,\r\n" +
                "        \"readOnly\": false,\r\n" +
                "        \"copyProtected\": false,\r\n" +
                "        \"crossPod\": true,\r\n" +
                "        \"viewHistory\": false,\r\n" +
                "        \"multiLateralRoom\": false,\r\n" +
                "        \"public\": false\r\n" +
                "    },\r\n" +
                "    \"roomSystemInfo\": {\r\n" +
                "        \"id\": \"bjHSiY4iz3ar4iIh6-VzCX___peoM7cPdA\",\r\n" +
                "        \"creationDate\": 1547661232368,\r\n" +
                "        \"createdByUserId\": 14362370637825,\r\n" +
                "        \"active\": true\r\n" +
                "    }\r\n" +
                "}")));

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
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"Member added\"\r\n" +
                "}")));

    streamsClient.addMemberToRoom("1", 1L);
  }

  @Test
  public void removeMemberFromRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.REMOVEMEMBER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"Member removed\"\r\n" +
                "}")));

    streamsClient.removeMemberFromRoom("1", 1L);
  }

  @Test
  public void getRoomInfoSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETROOMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"roomAttributes\": {\r\n" +
                "        \"name\": \"Room V3\",\r\n" +
                "        \"keywords\": [\r\n" +
                "        {\r\n" +
                "          \"key\": \"region\",\r\n" +
                "          \"value\": \"Value One\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "          \"key\": \"lead\",\r\n" +
                "          \"value\": \"Value Two\"\r\n" +
                "        }\r\n" +
                "      ],\r\n" +
                "        \"description\": \"test iframs\",\r\n" +
                "        \"membersCanInvite\": false,\r\n" +
                "        \"discoverable\": false,\r\n" +
                "        \"readOnly\": false,\r\n" +
                "        \"copyProtected\": false,\r\n" +
                "        \"crossPod\": false,\r\n" +
                "        \"viewHistory\": true,\r\n" +
                "        \"multiLateralRoom\": false,\r\n" +
                "        \"public\": false\r\n" +
                "    },\r\n" +
                "    \"roomSystemInfo\": {\r\n" +
                "        \"id\": \"OaFJ7w1FQcIyftjSaS2oXn___ppvml0-dA\",\r\n" +
                "        \"creationDate\": 1535725904577,\r\n" +
                "        \"createdByUserId\": 14362370637865,\r\n" +
                "        \"active\": true\r\n" +
                "    }\r\n" +
                "}")));

    RoomInfo roomInfo = streamsClient.getRoomInfo("1");

    assertNotNull(roomInfo);
    assertNotNull(roomInfo.getRoomAttributes());
    assertEquals("Room V3", roomInfo.getRoomAttributes().getName());
    assertEquals(false, roomInfo.getRoomAttributes().getPublic());
    assertEquals(false, roomInfo.getRoomAttributes().getReadOnly());
  }

  @Test
  public void updateRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEROOMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"roomAttributes\": {\r\n" +
                "        \"name\": \"Updating room\",\r\n" +
                "        \"keywords\": [\r\n" +
                "            {\r\n" +
                "                \"key\": \"region\",\r\n" +
                "                \"value\": \"EMEA\"\r\n" +
                "            },\r\n" +
                "            {\r\n" +
                "                \"key\": \"lead\",\r\n" +
                "                \"value\": \"Daffy Duck\"\r\n" +
                "            }\r\n" +
                "        ],\r\n" +
                "        \"description\": \"Updated via the API\",\r\n" +
                "        \"membersCanInvite\": true,\r\n" +
                "        \"discoverable\": true,\r\n" +
                "        \"readOnly\": false,\r\n" +
                "        \"copyProtected\": true,\r\n" +
                "        \"crossPod\": false,\r\n" +
                "        \"viewHistory\": true,\r\n" +
                "        \"multiLateralRoom\": false,\r\n" +
                "        \"public\": true\r\n" +
                "    },\r\n" +
                "    \"roomSystemInfo\": {\r\n" +
                "        \"id\": \"BhhJqemEYlPZmMknkfKVN3___pg5G0aDdA\",\r\n" +
                "        \"creationDate\": 1545230137724,\r\n" +
                "        \"createdByUserId\": 14362370637825,\r\n" +
                "        \"active\": true\r\n" +
                "    }\r\n" +
                "}")));

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
  public void getStreamInfoSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETSTREAMINFO.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": \"p9B316LKDto7iOECc8Xuz3___qeWsc0bdA\",\r\n" +
                "  \"crossPod\": false,\r\n" +
                "  \"origin\" : \"INTERNAL\",\r\n" +
                "  \"active\": true,\r\n" +
                "  \"lastMessageDate\":1518735448055,\r\n" +
                "  \"streamType\": {\r\n" +
                "    \"type\": \"IM\"\r\n" +
                "  },\r\n" +
                "  \"streamAttributes\": {\r\n" +
                "    \"members\": [\r\n" +
                "      7627861917905,\r\n" +
                "      7627861925698\r\n" +
                "    ]\r\n" +
                "  },\r\n" +
                "  \"roomAttributes\": {\r\n" +
                "    \"name\": \"Room Name\"\r\n" +
                "  }\r\n" +
                "}")));

    StreamInfo streamInfo = streamsClient.getStreamInfo("1");

    assertNotNull(streamInfo);
    assertEquals("p9B316LKDto7iOECc8Xuz3___qeWsc0bdA", streamInfo.getId());
  }

  @Test
  public void getRoomMembersSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETROOMMEMBERS.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"id\": 7078106103900,\r\n" +
                "    \"owner\": false,\r\n" +
                "    \"joinDate\": 1461430710531\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"id\": 7078106103809,\r\n" +
                "    \"owner\": true,\r\n" +
                "    \"joinDate\": 1461426797875\r\n" +
                "  }\r\n" +
                "]")));

    List<RoomMember> membership = streamsClient.getRoomMembers("1");

    assertNotNull(membership);
    assertEquals(2, membership.size());
  }

  @Test
  public void activateRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SETACTIVE.replace("{id}", "1").concat("?active=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("active", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"roomAttributes\": {\r\n" +
                "    \"name\": \"API room\",\r\n" +
                "    \"description\": \"Updated via the API\",\r\n" +
                "    \"membersCanInvite\": true,\r\n" +
                "    \"discoverable\": true\r\n" +
                "  },\r\n" +
                "  \"roomSystemInfo\": {\r\n" +
                "    \"id\": \"HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA\",\r\n" +
                "    \"creationDate\": 1461426797875,\r\n" +
                "    \"createdByUserId\": 7078106103809,\r\n" +
                "    \"active\": false\r\n" +
                "  },\r\n" +
                "  \"immutableRoomAttributes\": {\r\n" +
                "    \"readOnly\": false,\r\n" +
                "    \"copyProtected\": false,\r\n" +
                "    \"public\": false\r\n" +
                "  }\r\n" +
                "}")));

    streamsClient.activateRoom("1");
  }

  @Test
  public void deactivateRoomSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SETACTIVE.replace("{id}", "1").concat("?active=false")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("active", equalTo("false"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"roomAttributes\": {\r\n" +
                "    \"name\": \"API room\",\r\n" +
                "    \"description\": \"Updated via the API\",\r\n" +
                "    \"membersCanInvite\": true,\r\n" +
                "    \"discoverable\": true\r\n" +
                "  },\r\n" +
                "  \"roomSystemInfo\": {\r\n" +
                "    \"id\": \"HNmksPVAR6-f14WqKXmqHX___qu8LMLgdA\",\r\n" +
                "    \"creationDate\": 1461426797875,\r\n" +
                "    \"createdByUserId\": 7078106103809,\r\n" +
                "    \"active\": false\r\n" +
                "  },\r\n" +
                "  \"immutableRoomAttributes\": {\r\n" +
                "    \"readOnly\": false,\r\n" +
                "    \"copyProtected\": false,\r\n" +
                "    \"public\": false\r\n" +
                "  }\r\n" +
                "}")));

    streamsClient.deactivateRoom("1");
  }

  @Test
  public void promoteUserToOwnerSuccess() {
    stubFor(post(urlEqualTo(PodConstants.PROMOTEOWNER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"Member promoted to owner\"\r\n" +
                "}")));

    streamsClient.promoteUserToOwner("1", 1L);
  }

  @Test
  public void demoteUserFromOwnerSuccess() {
    stubFor(post(urlEqualTo(PodConstants.DEMOTEOWNER.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"Member demoted to participant\"\r\n" +
                "}")));

    streamsClient.demoteUserFromOwner("1", 1L);
  }

  @Test
  public void searchRoomsSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SEARCHROOMS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"count\": 2,\r\n" +
                "  \"skip\": 0,\r\n" +
                "  \"limit\": 10,\r\n" +
                "  \"query\": {\r\n" +
                "    \"query\": \"automobile\",\r\n" +
                "    \"labels\": [\r\n" +
                "      \"industry\"\r\n" +
                "    ],\r\n" +
                "    \"active\": true,\r\n" +
                "    \"creator\": {\r\n" +
                "      \"id\": 7696581411197\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"rooms\": [\r\n" +
                "    {\r\n" +
                "      \"roomAttributes\": {\r\n" +
                "        \"name\": \"Automobile Industry Room\",\r\n" +
                "        \"description\": \"Room to discuss car companies\",\r\n" +
                "        \"membersCanInvite\": true,\r\n" +
                "        \"readOnly\": false,\r\n" +
                "        \"copyProtected\": false,\r\n" +
                "        \"crossPod\": false,\r\n" +
                "        \"viewHistory\": false,\r\n" +
                "        \"public\": false\r\n" +
                "      },\r\n" +
                "      \"roomSystemInfo\": {\r\n" +
                "        \"id\": \"tzwvAZIdDMG3ZPRxv+xsgH///qr+JJkWdA==\",\r\n" +
                "        \"creationDate\": 1464615003895,\r\n" +
                "        \"createdByUserId\": 7696581411197,\r\n" +
                "        \"active\": true\r\n" +
                "      }\r\n" +
                "    },\r\n" +
                "    {\r\n" +
                "      \"roomAttributes\": {\r\n" +
                "        \"name\": \"Tesla Room\",\r\n" +
                "        \"keywords\": [\r\n" +
                "          {\r\n" +
                "            \"key\": \"industry\",\r\n" +
                "            \"value\": \"automobile\"\r\n" +
                "          }\r\n" +
                "        ],\r\n" +
                "        \"description\": \"Discussions on TSLA\",\r\n" +
                "        \"membersCanInvite\": true,\r\n" +
                "        \"readOnly\": false,\r\n" +
                "        \"copyProtected\": false,\r\n" +
                "        \"crossPod\": false,\r\n" +
                "        \"viewHistory\": false,\r\n" +
                "        \"public\": false\r\n" +
                "      },\r\n" +
                "      \"roomSystemInfo\": {\r\n" +
                "        \"id\": \"o6UkQ1TEmU0Tf/DHUlZrCH///qr+JQowdA==\",\r\n" +
                "        \"creationDate\": 1464614974947,\r\n" +
                "        \"createdByUserId\": 7696581411197,\r\n" +
                "        \"active\": true\r\n" +
                "      }\r\n" +
                "    }\r\n" +
                "  ],\r\n" +
                "  \"facetedMatchCount\": [\r\n" +
                "    {\r\n" +
                "      \"facet\": \"industry\",\r\n" +
                "      \"count\": 1\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}")));

    RoomSearchQuery query = new RoomSearchQuery();
    query.setQuery("automobile");
    query.setLabels(Stream.of("industry").collect(Collectors.toList()));
    query.setActive(true);

    try {
      RoomSearchResult result = streamsClient.searchRooms(query, 0, 0 );
      assertNotNull(result);
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserStreamsSuccess() {
    stubFor(post(urlEqualTo(PodConstants.LISTUSERSTREAMS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"id\": \"iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA\",\r\n" +
                "    \"crossPod\": false,\r\n" +
                "    \"active\": true,\r\n" +
                "    \"streamType\": {\r\n" +
                "      \"type\": \"POST\"\r\n" +
                "    },\r\n" +
                "    \"streamAttributes\": {\r\n" +
                "      \"members\": [\r\n" +
                "        7215545078229\r\n" +
                "      ]\r\n" +
                "    }\r\n" +
                "  }\r\n" +
                "]")));

    List<String> types = Stream.of("MIM", "ROOM", "POST").collect(Collectors.toList());
    List<StreamListItem> streamsList = streamsClient.getUserStreams(types, true);

    assertNotNull(streamsList);
    assertEquals(1, streamsList.size());
  }
}
