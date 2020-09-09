package it.clients.symphony.api;

import clients.symphony.api.UsersClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.SymClientException;
import it.commons.BotTest;

import java.io.IOException;
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
  public void getUserFromUsernameSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERV2.concat("?username=botusername&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("username", equalTo("botusername"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_userV2.json"))));

    try {
      UserInfo userInfo = usersClient.getUserFromUsername("botusername");
      assertNotNull(userInfo);
      assertEquals(1L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserFromEmailSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?email=test_1%40symphony.com&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("email", equalTo("test_1@symphony.com"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_users.json"))));

    try {
      UserInfo userInfo = usersClient.getUserFromEmail("test_1@symphony.com", true);
      assertNotNull(userInfo);
      assertEquals(15942919536460L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void getUserFromIdSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=15942919536460&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("15942919536460"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/get_user.json"))));

    try {
      UserInfo userInfo = usersClient.getUserFromId(15942919536460L, true);
      assertNotNull(userInfo);
      assertEquals(15942919536460L, userInfo.getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

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

    try {
      List<Long> uids = Stream.of(15942919536460L, 15942919536461L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
      assertNotNull(userInfoList);
      assertEquals(2, userInfoList.size());
    } catch (NoContentException nce) {
      fail();
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
      List<Long> uids = Stream.of(7696581394433L, 7696581394434L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
      assertTrue(userInfoList.isEmpty());
    } catch (NoContentException e) {
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
      List<Long> uids = Stream.of(7696581394433L, 7696581394434L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
    } catch (NoContentException e) {
      fail();
    }
  }

  @Test(expected = SymClientException.class)
  public void getUsersV3Status401() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/error/unauthorized_error.json"))));

    try {
      List<Long> uids = Stream.of(7696581394433L, 7696581394434L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
    } catch (NoContentException e) {
      fail();
    }
  }

  @Test(expected = ForbiddenException.class)
  public void getUsersV3Status403() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSV3.concat("?uid=7696581394433%2C7696581394434&local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("uid", equalTo("7696581394433,7696581394434"))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/error/forbidden_error.json"))));

    try {
      List<Long> uids = Stream.of(7696581394433L, 7696581394434L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
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
      List<Long> uids = Stream.of(7696581394433L, 7696581394434L).collect(Collectors.toList());
      List<UserInfo> userInfoList = usersClient.getUsersV3(null, uids, true);
    } catch (NoContentException e) {
      fail();
    }
  }
  //endregion

  @Test
  public void searchUsersSuccess() throws IOException {
    stubFor(post(urlEqualTo(PodConstants.SEARCHUSERS.concat("?local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/user_client/search_user.json"))));

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
  public void getSessionUserSuccess() throws IOException {
    stubFor(get(urlEqualTo(PodConstants.GETSESSIONUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody(readResourceContent("/response_content/session/session_user.json"))));

    UserInfo userInfo = usersClient.getSessionUser();
    assertNotNull(userInfo);
    assertEquals(7696581394433L, userInfo.getId().longValue());
  }
}
