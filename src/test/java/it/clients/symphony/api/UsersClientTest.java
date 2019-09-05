package it.clients.symphony.api;

import clients.symphony.api.UsersClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import model.UserInfo;
import model.UserSearchResult;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class UsersClientTest extends BotTest {
  private UsersClient usersClient;

  @Before
  public void initClient() {
    usersClient = new UsersClient(symBotClient);
  }

  @Test
  public void getUserFromUsernameSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": \"1\",\r\n" +
                "  \"emailAddress\": \"bot@symphony.com\",\r\n" +
                "  \"firstName\": \"Bot\",\r\n" +
                "  \"lastName\": \"Smith\",\r\n" +
                "  \"displayName\": \"Bot Smith\",\r\n" +
                "  \"title\": \"Mr.\",\r\n" +
                "  \"company\": \"Symphony\",\r\n" +
                "  \"username\": \"botusername\",\r\n" +
                "  \"location\": \"California\",\r\n" +
                "  \"workPhoneNumber\": \"1312312\",\r\n" +
                "  \"mobilePhoneNumber\": \"12313123\",\r\n" +
                "  \"jobFunction\": \"CEO\",\r\n" +
                "  \"department\": \"Sales\",\r\n" +
                "  \"division\": \"US\",\r\n" +
                "  \"avatars\": [\r\n" +
                "    { \"size\": \"original\", \"url\": \"../avatars/static/150/default.png\" },\r\n" +
                "    { \"size\": \"small\", \"url\": \"../avatars/static/50/default.png\" }\r\n" +
                "  ]\r\n" +
                "}")));

    try {
      UserInfo userInfo = usersClient.getUserFromUsername("botusername");
      assertNotNull(userInfo);
      assertEquals(1L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserFromEmailSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "   \"users\": [\r\n" +
                "        {\r\n" +
                "            \"id\": 15942919536460,\r\n" +
                "            \"emailAddress\": \"test_1@symphony.com\",\r\n" +
                "            \"firstName\": \"test_1\",\r\n" +
                "            \"lastName\": \"test\",\r\n" +
                "            \"displayName\": \"test_1 test\",\r\n" +
                "            \"title\": \"Technical Writer\",\r\n" +
                "            \"company\": \"pod232\",\r\n" +
                "            \"username\": \"test_1\",\r\n" +
                "            \"location\": \"location\",\r\n" +
                "            \"avatars\": [\r\n" +
                "                {\r\n" +
                "                    \"size\": \"original\",\r\n" +
                "                    \"url\": \"../avatars/static/150/default.png\"\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"size\": \"small\",\r\n" +
                "                    \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        }" +
                "    ],\r\n" +
                "    \"errors\": [\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"email\": \"notavalidemail\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"id\": 654321\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}")));

    try {
      UserInfo userInfo = usersClient.getUserFromEmail("test_1@symphony.com", true);
      assertNotNull(userInfo);
      assertEquals(15942919536460L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserFromIdSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "   \"users\": [\r\n" +
                "        {\r\n" +
                "            \"id\": 15942919536460,\r\n" +
                "            \"emailAddress\": \"test_1@symphony.com\",\r\n" +
                "            \"firstName\": \"test_1\",\r\n" +
                "            \"lastName\": \"test\",\r\n" +
                "            \"displayName\": \"test_1 test\",\r\n" +
                "            \"title\": \"Technical Writer\",\r\n" +
                "            \"company\": \"pod232\",\r\n" +
                "            \"username\": \"test_1\",\r\n" +
                "            \"location\": \"location\",\r\n" +
                "            \"avatars\": [\r\n" +
                "                {\r\n" +
                "                    \"size\": \"original\",\r\n" +
                "                    \"url\": \"../avatars/static/150/default.png\"\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"size\": \"small\",\r\n" +
                "                    \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        }" +
                "    ],\r\n" +
                "    \"errors\": [\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"email\": \"notavalidemail\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"id\": 654321\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}")));

    try {
      UserInfo userInfo = usersClient.getUserFromId(15942919536460L, true);
      assertNotNull(userInfo);
      assertEquals(15942919536460L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUsersV3Success() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "   \"users\": [\r\n" +
                "        {\r\n" +
                "            \"id\": 15942919536460,\r\n" +
                "            \"emailAddress\": \"test_1@symphony.com\",\r\n" +
                "            \"firstName\": \"test_1\",\r\n" +
                "            \"lastName\": \"test\",\r\n" +
                "            \"displayName\": \"test_1 test\",\r\n" +
                "            \"title\": \"Technical Writer\",\r\n" +
                "            \"company\": \"pod232\",\r\n" +
                "            \"username\": \"test_1\",\r\n" +
                "            \"location\": \"location\",\r\n" +
                "            \"avatars\": [\r\n" +
                "                {\r\n" +
                "                    \"size\": \"original\",\r\n" +
                "                    \"url\": \"../avatars/static/150/default.png\"\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"size\": \"small\",\r\n" +
                "                    \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"id\": 15942919536461,\r\n" +
                "            \"emailAddress\": \"test_2@symphony.com\",\r\n" +
                "            \"firstName\": \"test_2\",\r\n" +
                "            \"lastName\": \"test\",\r\n" +
                "            \"displayName\": \"test_2 test\",\r\n" +
                "            \"title\": \"Technical Writer\",\r\n" +
                "            \"company\": \"pod232\",\r\n" +
                "            \"username\": \"test_2\",\r\n" +
                "            \"location\": \"location\",\r\n" +
                "            \"avatars\": [\r\n" +
                "                {\r\n" +
                "                    \"size\": \"original\",\r\n" +
                "                    \"url\": \"../avatars/static/150/default.png\"\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"size\": \"small\",\r\n" +
                "                    \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        }\r\n" +
                "    ],\r\n" +
                "    \"errors\": [\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"email\": \"notavalidemail\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"error\": \"invalid.format\",\r\n" +
                "            \"id\": 654321\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}")));

    try {
      List<Long> uids = Stream.of(15942919536460L,15942919536461L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
      assertNotNull(userInfoList);
      assertEquals(2, userInfoList.size());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void searchUsersSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SEARCHUSERS.concat("?local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"count\": 1,\r\n" +
                "  \"skip\": 0,\r\n" +
                "  \"limit\": 1,\r\n" +
                "  \"query\": \"john@symphony.com\",\r\n" +
                "  \"filters\": {\r\n" +
                "    \"title\": \"Portfolio Manager\",\r\n" +
                "    \"location\": \"New York\",\r\n" +
                "    \"company\": \"Gotham\"\r\n" +
                "  },\r\n" +
                "  \"users\": [\r\n" +
                "    {\r\n" +
                "      \"id\": 7078106124861,\r\n" +
                "      \"firstName\": \"John\",\r\n" +
                "      \"lastName\": \"Doe\",\r\n" +
                "      \"displayName\": \"John Doe\",\r\n" +
                "      \"company\": \"Gotham Associates\",\r\n" +
                "      \"location\": \"New York\",\r\n" +
                "      \"avatars\": [\r\n" +
                "        {\r\n" +
                "          \"size\": \"original\",\r\n" +
                "          \"url\": \"../avatars/static/150/default.png\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "          \"size\": \"small\",\r\n" +
                "          \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "        }\r\n" +
                "      ]\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}")));

    try {
      UserSearchResult result = usersClient.searchUsers(null, true, 0, 0, null);
      assertNotNull(result);
      assertEquals(1, result.getCount());
      assertEquals(7078106124861L, result.getUsers().get(0).getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getSessionUserSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": 7696581394433,\r\n" +
                "  \"emailAddress\": \"admin@symphony.com\",\r\n" +
                "  \"firstName\": \"Symphony\",\r\n" +
                "  \"lastName\": \"Admin\",\r\n" +
                "  \"displayName\": \"Symphony Admin\",\r\n" +
                "  \"title\": \"Administrator\",\r\n" +
                "  \"company\": \"Acme\",\r\n" +
                "  \"username\": \"admin@symphony.com\",\r\n" +
                "  \"location\": \"California\",\r\n" +
                "  \"avatars\": [\r\n" +
                "    { \"size\": \"original\", \"url\": \"../avatars/static/150/default.png\" },\r\n" +
                "    { \"size\": \"small\", \"url\": \"../avatars/static/50/default.png\" }\r\n" +
                "  ],\r\n" +
                "  \"roles\": [\r\n" +
                "     \"CONTENT_MANAGEMENT\",\r\n" +
                "     \"INDIVIDUAL\",\r\n" +
                "     \"USER_PROVISIONING\"\r\n" +
                "  ]\r\n" +
                "}")));

    UserInfo userInfo = usersClient.getSessionUser();
    assertNotNull(userInfo);
    assertEquals(7696581394433L, userInfo.getId().longValue());
  }
}
