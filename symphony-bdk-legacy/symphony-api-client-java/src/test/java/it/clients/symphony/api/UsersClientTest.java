package it.clients.symphony.api;

import clients.symphony.api.UsersClient;
import clients.symphony.api.constants.PodConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.SymClientException;
import it.commons.BotTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import model.Avatar;
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

  // getUserFromUsername
  @Test
  public void getUserFromUsernameSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_userV2.json"))));

    final UserInfo userInfo = usersClient.getUserFromUsername("botusername");
    assertNotNull(userInfo);

    assertEquals(1L, userInfo.getId().longValue());
    assertEquals("bot@symphony.com", userInfo.getEmailAddress());
    assertEquals("Bot", userInfo.getFirstName());
    assertEquals("Smith", userInfo.getLastName());
    assertEquals("Bot Smith", userInfo.getDisplayName());
    assertEquals("Sales", userInfo.getTitle());
    assertEquals("Acme", userInfo.getCompany());
    assertEquals("botusername", userInfo.getUsername());
    assertEquals("San Francisco", userInfo.getLocation());

    final List<Avatar> avatars = userInfo.getAvatars();
    assertNotNull(avatars);
    assertEquals(2, avatars.size());

    int i=0;

    String expectedSize = null;
    String expectedUrl = null;

    for(Avatar avatar : avatars){
      i++;
      assertNotNull(avatar);
      if(i==1){
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if(i==2){
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }
  }

  @Test(expected = NoContentException.class)
  public void getUserFromUsernameFailure204() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromUsername("botusername");
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserFromUsernameFailure400() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromUsername("botusername");
  }

  @Test(expected = SymClientException.class)
  public void getUserFromUsernameFailure401() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    usersClient.getUserFromUsername("botusername");
  }

  @Test(expected = ForbiddenException.class)
  public void getUserFromUsernameFailure403() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\""
                + "}")));

    usersClient.getUserFromUsername("botusername");
  }

  @Test(expected = ServerErrorException.class)
  public void getUserFromUsernameFailure500() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromUsername("botusername");
  }
  // End getUserFromUsername

  // getUserFromEmail
  @Test
  public void getUserFromEmailSuccess1() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_user1.json"))));

    final UserInfo userInfo1 = usersClient.getUserFromEmail("test_1@symphony.com", true);
    assertNotNull(userInfo1);

    assertEquals(15942919536460L, userInfo1.getId().longValue());
    assertEquals("test_1@symphony.com", userInfo1.getEmailAddress());
    assertEquals("test_1", userInfo1.getFirstName());
    assertEquals("test", userInfo1.getLastName());
    assertEquals("test_1 test", userInfo1.getDisplayName());
    assertEquals("Technical Writer", userInfo1.getTitle());
    assertEquals("pod232", userInfo1.getCompany());
    assertEquals("test_1", userInfo1.getUsername());
    assertEquals("location", userInfo1.getLocation());

    final List<Avatar> avatars1 = userInfo1.getAvatars();
    assertNotNull(avatars1);
    assertEquals(2, avatars1.size());

    int i = 0;

    String expectedSize = null;
    String expectedUrl = null;

    for (Avatar avatar : avatars1) {
      i++;
      assertNotNull(avatar);
      if (i == 1) {
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if (i == 2) {
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }
  }

  @Test
  public void getUserFromEmailSuccess2() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_user2.json"))));

    final UserInfo userInfo = usersClient.getUserFromEmail("test_2@symphony.com", true);
    assertNotNull(userInfo);

    assertEquals(15942919536461L, userInfo.getId().longValue());
    assertEquals("test_2@symphony.com", userInfo.getEmailAddress());
    assertEquals("test_2", userInfo.getFirstName());
    assertEquals("test", userInfo.getLastName());
    assertEquals("test_2 test", userInfo.getDisplayName());
    assertEquals("Technical Writer", userInfo.getTitle());
    assertEquals("pod232", userInfo.getCompany());
    assertEquals("test_2", userInfo.getUsername());
    assertEquals("location", userInfo.getLocation());

    final List<Avatar> avatars = userInfo.getAvatars();
    assertNotNull(avatars);
    assertEquals(2, avatars.size());

    int i=0;

    String expectedSize = null;
    String expectedUrl = null;

    for(Avatar avatar : avatars){
      i++;
      assertNotNull(avatar);
      if(i==1){
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if(i==2){
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }
  }

  @Test
  public void getUserFromEmailError() throws IOException {
    final StubMapping stubMapping = stubGet(PodConstants.GETUSERSV3,
        readResourceContent("/response_content/user_client/get_user_error.json"));

    assertNotNull(stubMapping);

    final ResponseDefinition responseDefinition = stubMapping.getResponse();
    assertNotNull(responseDefinition);

    final String content = responseDefinition.getBody();
    assertNotNull(content);
    final JsonNode jsonNode = stringToJson(content);
    assertNotNull(jsonNode);

    final JsonNode jsonNodeErrors = jsonNode.get("errors");
    assertNotNull(jsonNodeErrors);

    assertEquals(2, jsonNodeErrors.size());

    final JsonNode jsonNodeError1 = jsonNodeErrors.get(0);
    assertNotNull(jsonNodeError1);
    final JsonNode errorField1 = jsonNodeError1.get("error");
    assertNotNull(errorField1);
    final JsonNode email = jsonNodeError1.get("email");
    assertEquals("invalid.format", errorField1.asText());
    assertEquals("test_2@.symphony.com", email.asText());

    final JsonNode jsonNodeError2 = jsonNodeErrors.get(1);
    assertNotNull(jsonNodeError2);
    final JsonNode errorField2 = jsonNodeError2.get("error");
    assertNotNull(errorField2);
    final JsonNode id = jsonNodeError2.get("id");
    assertEquals("invalid.format", errorField2.asText());
    assertEquals(654321, id.intValue());
  }

  private static JsonNode stringToJson(String content) throws IOException {
    return new JsonMapper().readTree(content);
  }

  @Test(expected = NoContentException.class)
  public void getUserFromEmailFailure204() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromEmail("test_1@symphony.com", true);
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserFromEmailFailure400() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromEmail("test_1@symphony.com", true);
  }

  @Test(expected = SymClientException.class)
  public void getUserFromEmailFailure401() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    usersClient.getUserFromEmail("test_1@symphony.com", true);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserFromEmailFailure403() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\""
                + "}")));

    usersClient.getUserFromEmail("test_1@symphony.com", true);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserFromEmailFailure500() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromEmail("test_1@symphony.com", true);
  }
  // End getUserFromEmail

  // getUserFromId
  @Test
  public void getUserFromIdSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_user1.json"))));

    final UserInfo userInfo1 = usersClient.getUserFromId(15942919536460L, true);
    assertNotNull(userInfo1);

    assertEquals(15942919536460L, userInfo1.getId().longValue());
    assertEquals("test_1@symphony.com", userInfo1.getEmailAddress());
    assertEquals("test_1", userInfo1.getFirstName());
    assertEquals("test", userInfo1.getLastName());
    assertEquals("test_1 test", userInfo1.getDisplayName());
    assertEquals("Technical Writer", userInfo1.getTitle());
    assertEquals("pod232", userInfo1.getCompany());
    assertEquals("test_1", userInfo1.getUsername());
    assertEquals("location", userInfo1.getLocation());

    final List<Avatar> avatars1 = userInfo1.getAvatars();
    assertNotNull(avatars1);
    assertEquals(2, avatars1.size());

    int i = 0;

    String expectedSize = null;
    String expectedUrl = null;

    for (Avatar avatar : avatars1) {
      i++;
      assertNotNull(avatar);
      if (i == 1) {
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if (i == 2) {
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }
  }

  @Test(expected = NoContentException.class)
  public void getUserFromIdFailure204() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromId(15942919536460L, true);
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserFromIdFailure400() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromId(15942919536460L, true);
  }

  @Test(expected = SymClientException.class)
  public void getUserFromIdFailure401() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    usersClient.getUserFromId(15942919536460L, true);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserFromIdFailure403() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\""
                + "}")));

    usersClient.getUserFromId(15942919536460L, true);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserFromIdFailure500() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getUserFromId(15942919536460L, true);
  }
  // End getUserFromId

  // getUserFromIdList
  @Test
  public void getUsersFromIdListSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_users.json"))));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    final List<UserInfo> userInfoList = usersClient.getUsersFromIdList( uids, true);
    assertNotNull(userInfoList);
    assertEquals(2, userInfoList.size());

    int n=0;
    for(final UserInfo userInfo : userInfoList){
      n++;
      if(n == 1) {
        assertEquals(15942919536460L, userInfo.getId().longValue());
        assertEquals("test_1@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_1", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_1 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_1", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars1 = userInfo.getAvatars();
        assertNotNull(avatars1);
        assertEquals(2, avatars1.size());

        int i = 0;

        String expectedSize = null;
        String expectedUrl = null;

        for (Avatar avatar : avatars1) {
          i++;
          assertNotNull(avatar);
          if (i == 1) {
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if (i == 2) {
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }
      } else if(n == 2){
        assertEquals(15942919536461L, userInfo.getId().longValue());
        assertEquals("test_2@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_2", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_2 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_2", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars = userInfo.getAvatars();
        assertNotNull(avatars);
        assertEquals(2, avatars.size());

        int i=0;

        String expectedSize = null;
        String expectedUrl = null;

        for(Avatar avatar : avatars){
          i++;
          assertNotNull(avatar);
          if(i==1){
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if(i==2){
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }

      }
    }
  }

  @Test
  public void getUsersFromIdListFailure204() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    final List<UserInfo> userInfoList = usersClient.getUsersFromIdList(uids, true);
    assertNotNull(userInfoList);
    assertTrue(userInfoList.isEmpty());
  }

  @Test(expected = APIClientErrorException.class)
  public void getUsersFromIdListFailure400() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    usersClient.getUsersFromIdList(uids, true);
  }

  @Test(expected = SymClientException.class)
  public void getUsersFromIdListFailure401() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\"}")));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    usersClient.getUsersFromIdList(uids, true);
  }

  @Test(expected = ForbiddenException.class)
  public void getUsersFromIdListFailure403() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\"}")));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    usersClient.getUsersFromIdList(uids, true);
  }

  @Test(expected = ServerErrorException.class)
  public void getUsersFromIdListFailure500() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    usersClient.getUsersFromIdList(uids, true);
  }
  // End getUserFromIdList

  // getUsersFromEmailList
  @Test
  public void getUsersFromEmailListSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_users.json"))));

    final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
    assertNotNull(emails);
    final List<UserInfo> userInfoList = usersClient.getUsersFromEmailList(emails, true);
    assertNotNull(userInfoList);
    assertEquals(2, userInfoList.size());

    int n=0;
    for(final UserInfo userInfo : userInfoList){
      n++;
      if(n == 1) {
        assertEquals(15942919536460L, userInfo.getId().longValue());
        assertEquals("test_1@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_1", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_1 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_1", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars1 = userInfo.getAvatars();
        assertNotNull(avatars1);
        assertEquals(2, avatars1.size());

        int i = 0;

        String expectedSize = null;
        String expectedUrl = null;

        for (Avatar avatar : avatars1) {
          i++;
          assertNotNull(avatar);
          if (i == 1) {
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if (i == 2) {
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }
      } else if(n == 2){
        assertEquals(15942919536461L, userInfo.getId().longValue());
        assertEquals("test_2@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_2", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_2 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_2", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars = userInfo.getAvatars();
        assertNotNull(avatars);
        assertEquals(2, avatars.size());

        int i=0;

        String expectedSize = null;
        String expectedUrl = null;

        for(Avatar avatar : avatars){
          i++;
          assertNotNull(avatar);
          if(i==1){
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if(i==2){
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }
      }
    }
  }

  @Test
  public void getUsersFromEmailListFailure204() {
    stubFor(get(urlEqualTo(
        PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
      assertNotNull(emails);
      final List<UserInfo> userInfoList = usersClient.getUsersFromEmailList(emails, true);
      assertNotNull(userInfoList);
      assertTrue(userInfoList.isEmpty());

    } catch (NoContentException nce) {
      fail();
    } catch (SymClientException sce){
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUsersFromEmailListFailure400()  {
    stubFor(get(urlEqualTo(
        PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
      assertNotNull(emails);
      usersClient.getUsersFromEmailList(emails, true);

    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test(expected = SymClientException.class)
  public void getUsersFromEmailListFailure401() {
    stubFor(get(urlEqualTo(
        PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    try {

      final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
      assertNotNull(emails);
      usersClient.getUsersFromEmailList(emails, true);

    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test(expected = ForbiddenException.class)
  public void getUsersFromEmailListFailure403() {
    stubFor(get(urlEqualTo(
        PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\""
                + "}")));

    try {

      final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
      assertNotNull(emails);
      usersClient.getUsersFromEmailList(emails, true);

    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test(expected = ServerErrorException.class)
  public void getUsersFromEmailListFailure500() {
    stubFor(get(urlEqualTo(
        PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com%2Ctest_2%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com,test_2@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
      assertNotNull(emails);
      usersClient.getUsersFromEmailList(emails, true);

    } catch (NoContentException nce) {
      fail();
    }
  }
  // End getUsersFromEmailList

  //region Test GetUsersV3
  @Test
  public void getUsersV3Success() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460%2C15942919536461&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460,15942919536461"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_users.json"))));

    final List<Long> uids = Arrays.asList(15942919536460L, 15942919536461L);
    assertNotNull(uids);
    final List<String> emails = Arrays.asList("test_1@symphony.com", "test_2@symphony.com");
    assertNotNull(emails);
    final List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
    assertNotNull(userInfoList);
    assertEquals(2, userInfoList.size());

    int n=0;
    for(final UserInfo userInfo : userInfoList){
      n++;
      if(n == 1) {
        assertEquals(15942919536460L, userInfo.getId().longValue());
        assertEquals("test_1@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_1", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_1 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_1", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars1 = userInfo.getAvatars();
        assertNotNull(avatars1);
        assertEquals(2, avatars1.size());

        int i = 0;

        String expectedSize = null;
        String expectedUrl = null;

        for (Avatar avatar : avatars1) {
          i++;
          assertNotNull(avatar);
          if (i == 1) {
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if (i == 2) {
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }
      } else if(n == 2){
        assertEquals(15942919536461L, userInfo.getId().longValue());
        assertEquals("test_2@symphony.com", userInfo.getEmailAddress());
        assertEquals("test_2", userInfo.getFirstName());
        assertEquals("test", userInfo.getLastName());
        assertEquals("test_2 test", userInfo.getDisplayName());
        assertEquals("Technical Writer", userInfo.getTitle());
        assertEquals("pod232", userInfo.getCompany());
        assertEquals("test_2", userInfo.getUsername());
        assertEquals("location", userInfo.getLocation());

        final List<Avatar> avatars = userInfo.getAvatars();
        assertNotNull(avatars);
        assertEquals(2, avatars.size());

        int i=0;

        String expectedSize = null;
        String expectedUrl = null;

        for(Avatar avatar : avatars){
          i++;
          assertNotNull(avatar);
          if(i==1){
            expectedSize = "original";
            expectedUrl = "../avatars/static/150/default.png";
          } else if(i==2){
            expectedSize = "small";
            expectedUrl = "../avatars/static/50/default.png";
          }
          assertEquals(expectedSize, avatar.getSize());
          assertEquals(expectedUrl, avatar.getUrl());
        }

      }
    }
  }

  @Test
  public void getUsersV3Status204() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(204)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<Long> uids = Arrays.asList(7696581394433L, 7696581394434L);
      final List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);

      assertTrue(userInfoList.isEmpty());

    } catch (NoContentException e) {
      fail();
    } catch (SymClientException sce){
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUsersV3Status400() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<Long> uids = Arrays.asList(7696581394433L, 7696581394434L);
      usersClient.getUsersV3(null, uids, true);

    } catch (NoContentException e) {
      fail();
    }
  }

  @Test(expected = SymClientException.class)
  public void getUsersV3Status401() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    try {

      final List<Long> uids = Arrays.asList(7696581394433L, 7696581394434L);
      usersClient.getUsersV3(null, uids, true);

    } catch (NoContentException e) {
      fail();
    }
  }

  @Test(expected = ForbiddenException.class)
  public void getUsersV3Status403() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\"}")));

    try {

      final List<Long> uids = Arrays.asList(7696581394433L, 7696581394434L);
      usersClient.getUsersV3(null, uids, true);

    } catch (NoContentException e) {
      fail();
    }
  }

  @Test(expected = ServerErrorException.class)
  public void getUsersV3Status500() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    try {

      final List<Long> uids = Arrays.asList(7696581394433L, 7696581394434L);
      usersClient.getUsersV3(null, uids, true);

    } catch (NoContentException e) {
      fail();
    }
  }
  //endregion

  //region Test searchUsers
  @Test
  public void searchUsersSuccess() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"),
        readResourceContent("/response_content/user_client/search_user.json"), 200);

    final UserSearchResult result = usersClient.searchUsers(null, true, 0, 0, null);
    assertNotNull(result);

    assertEquals(1, result.getCount());
    assertEquals(0, result.getSkip());
    assertEquals(1, result.getLimit());
    assertEquals("john@symphony.com", result.getQuery());

    final Map<String, String> filters = result.getFilters();
    assertNotNull(filters);
    assertEquals("Portfolio Manager", filters.get("title"));
    assertEquals("New York", filters.get("location"));
    assertEquals("Gotham", filters.get("company"));

    final List<UserInfo> users = result.getUsers();
    assertNotNull(users);
    assertEquals(1, users.size());

    final UserInfo user = users.get(0);
    assertNotNull(user);
    assertEquals(7078106124861L, user.getId().longValue());
    assertEquals("john@symphony.com", user.getEmailAddress());
    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());
    assertEquals("John Doe", user.getDisplayName());
    assertEquals("null", user.getTitle());
    assertEquals("Gotham Associates", user.getCompany());
    assertEquals("null", user.getUsername());
    assertEquals("New York", user.getLocation());
    assertEquals("NORMAL", user.getAccountType());

    final List<Avatar> avatars = user.getAvatars();
    assertNotNull(avatars);
    int n = 0;
    String expectedSize = null;
    String expectedUrl = null;
    for(final Avatar avatar : avatars){
      n++;
      if(n == 1){
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if(n == 2){
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }
  }

  @Test(expected = NoContentException.class)
  public void searchUsersStatus204() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"), null, 204);

    usersClient.searchUsers(null, true, 0, 0, null);
  }

  @Test(expected = APIClientErrorException.class)
  public void searchUsersStatus400() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"), null, 400);

    usersClient.searchUsers(null, true, 0, 0, null);
  }

  @Test(expected = SymClientException.class)
  public void searchUsersStatus401() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"), null, 401);

    usersClient.searchUsers(null, true, 0, 0, null);
  }

  @Test(expected = ForbiddenException.class)
  public void searchUsersStatus403() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"), null, 403);

    usersClient.searchUsers(null, true, 0, 0, null);
  }

  @Test(expected = ServerErrorException.class)
  public void searchUsersStatus500() throws IOException {
    stubPost(PodConstants.SEARCHUSERS.concat("?local=true"), null, 500);

    usersClient.searchUsers(null, true, 0, 0, null);
  }
  //endregion

  @Test
  public void getSessionUserSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/session/session_user.json"))));

    final UserInfo userInfo = usersClient.getSessionUser();
    assertNotNull(userInfo);
    assertEquals(7696581394433L, userInfo.getId().longValue());
    assertEquals("admin@symphony.com", userInfo.getEmailAddress());
    assertEquals("Symphony", userInfo.getFirstName());
    assertEquals("Admin", userInfo.getLastName());
    assertEquals("Symphony Admin", userInfo.getDisplayName());
    assertEquals("Administrator", userInfo.getTitle());
    assertEquals("Acme", userInfo.getCompany());
    assertEquals("admin@symphony.com", userInfo.getUsername());
    assertEquals("California", userInfo.getLocation());

    final List<Avatar> avatars = userInfo.getAvatars();
    assertNotNull(avatars);
    int n = 0;
    String expectedSize = null;
    String expectedUrl = null;
    for(final Avatar avatar : avatars){
      n++;
      if(n == 1){
        expectedSize = "original";
        expectedUrl = "../avatars/static/150/default.png";
      } else if(n == 2){
        expectedSize = "small";
        expectedUrl = "../avatars/static/50/default.png";
      }
      assertEquals(expectedSize, avatar.getSize());
      assertEquals(expectedUrl, avatar.getUrl());
    }

    // Roles
    final StubMapping stubMapping = stubGet(PodConstants.GETUSERSV3,
        readResourceContent("/response_content/session/session_user.json"));

    assertNotNull(stubMapping);

    final ResponseDefinition responseDefinition = stubMapping.getResponse();
    assertNotNull(responseDefinition);

    final String content = responseDefinition.getBody();
    assertNotNull(content);
    final JsonNode jsonNode = stringToJson(content);
    assertNotNull(jsonNode);

    final JsonNode jsonNodeRoles = jsonNode.get("roles");
    assertNotNull(jsonNodeRoles);

    assertEquals(3, jsonNodeRoles.size());

    final String role1 = jsonNodeRoles.get(0).asText();
    assertEquals("CONTENT_MANAGEMENT", role1);

    final String role2 = jsonNodeRoles.get(1).asText();
    assertEquals("INDIVIDUAL", role2);

    final String role3 = jsonNodeRoles.get(2).asText();
    assertEquals("USER_PROVISIONING", role3);
  }

  @Test(expected = APIClientErrorException.class)
  public void getSessionUserFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getSessionUser();
  }

  @Test(expected = SymClientException.class)
  public void getSessionUserFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 401,"
                + "\"message\": \"Invalid session\""
                + "}")));

    usersClient.getSessionUser();
  }

  @Test(expected = ForbiddenException.class)
  public void getSessionUserFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\""
                + "}")));

    usersClient.getSessionUser();
  }

  @Test(expected = ServerErrorException.class)
  public void getSessionUserFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    usersClient.getSessionUser();
  }
}
