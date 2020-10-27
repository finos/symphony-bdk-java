package it.clients.symphony.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import clients.symphony.api.PresenceClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.SymClientException;
import it.commons.BotTest;
import model.UserPresence;
import model.UserPresenceCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PresenceClientTest extends BotTest {

  private PresenceClient presenceClient;

  @Before
  public void initClient() {
    presenceClient = new PresenceClient(symBotClient);
  }

  // getUserPresence with arguments
  @Test
  public void getUserPresenceSuccess() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        "{ "
            + "\"category\": \"AVAILABLE\", "
            + "\"userId\": 1, "
            + "\"timestamp\": 1533928483800"
            + "}"
    );

    final UserPresence userPresence = presenceClient.getUserPresence(1L, true);
    assertNotNull(userPresence);

    verifyUserPresence(userPresence, 1L, UserPresenceCategory.AVAILABLE, 1533928483800L);
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserPresenceFailure400() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        400, "{}"
    );

    presenceClient.getUserPresence(1L, true);
  }

  @Test(expected = SymClientException.class)
  public void getUserPresenceFailure401() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        401, "{}"
    );

    presenceClient.getUserPresence(1L, true);
  }


  @Test(expected = ForbiddenException.class)
  public void getUserPresenceFailure403() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        403, "{}"
    );

    presenceClient.getUserPresence(1L, true);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserPresenceFailure500() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        500, "{}"
    );

    presenceClient.getUserPresence(1L, true);
  }
  // End getUserPresence with arguments

  // getUserPresence without argument
  @Test
  public void getCallingUserPresenceSuccess() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE,
        "{"
            + "\"category\": \"AVAILABLE\","
            + "\"userId\": 1,"
            + "\"timestamp\": 1533928483800"
            + "}"
    );

    final UserPresence userPresence = presenceClient.getUserPresence();
    assertNotNull(userPresence);

    verifyUserPresence(userPresence, 1L, UserPresenceCategory.AVAILABLE, 1533928483800L);
  }

  @Test(expected = APIClientErrorException.class)
  public void getCallingUserPresenceFailure400() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE, 400, "{}");

    presenceClient.getUserPresence();
  }

  @Test(expected = SymClientException.class)
  public void getCallingUserPresenceFailure401() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE, 401, "{}");

    presenceClient.getUserPresence();
  }

  @Test(expected = ForbiddenException.class)
  public void getCallingUserPresenceFailure403() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE, 403, "{}");

    presenceClient.getUserPresence();
  }

  @Test(expected = ServerErrorException.class)
  public void getCallingUserPresenceFailure500() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE, 500, "{}");

    presenceClient.getUserPresence();
  }
  // End getUserPresence without argument

  // getAllPresence
  @Test
  public void getAllPresenceSuccess() {

    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=50",
        "["
            + "{ "
            + "\"category\": \"AVAILABLE\", "
            + "\"userId\": 1, "
            + "\"timestamp\": 1533928483800"
            + "},"
            + "{"
            + " \"category\": \"BUSY\","
            + " \"userId\": 974217539631,"
            + " \"timestamp\": 1503286226030"
            + "}"
            + "]"
    );

    final List<UserPresence> presenceList = presenceClient.getAllPresence(974217539631L, 50);
    assertNotNull(presenceList);

    assertEquals(2, presenceList.size());
    verifyUserPresence(presenceList.get(0), 1L, UserPresenceCategory.AVAILABLE, 1533928483800L);
    verifyUserPresence(presenceList.get(1), 974217539631L, UserPresenceCategory.BUSY, 1503286226030L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getAllPresenceFailureLimit() {
    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=5001",
        "["
            + "{ "
            + "\"category\": \"AVAILABLE\", "
            + "\"userId\": 1, "
            + "\"timestamp\": 1533928483800"
            + "},"
            + "{"
            + " \"category\": \"BUSY\","
            + " \"userId\": 974217539631,"
            + " \"timestamp\": 1503286226030"
            + "}"
            + "]"
    );

    presenceClient.getAllPresence(974217539631L, 5001);
  }

  @Test(expected = APIClientErrorException.class)
  public void getAllPresenceFailure400() {
    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=50", 400,
        "{}");

    presenceClient.getAllPresence(974217539631L, 50);
  }

  @Test(expected = SymClientException.class)
  public void getAllPresenceFailure401() {
    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=50", 401,
        "{}");

    presenceClient.getAllPresence(974217539631L, 50);
  }

  @Test(expected = ForbiddenException.class)
  public void getAllPresenceFailure403() {
    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=50", 403,
        "{"
            + "\"code\": 403,"
            + "\"message\": \"The user lacks the required entitlement to perform this operation\""
            + "}");

    presenceClient.getAllPresence(974217539631L, 50);
  }

  @Test(expected = ServerErrorException.class)
  public void getAllPresenceFailure500() {
    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=974217539631&limit=50", 500,
        "{}");

    presenceClient.getAllPresence(974217539631L, 50);
  }
  // End getAllPresence

  // setPresence by category
  @Test
  public void setUserPresenceByCategorySuccess() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE,
        "{"
            + "\"category\": \"AWAY\","
            + "\"userId\": 1,"
            + "\"timestamp\": 1503286569882"
            + "}"
    );

    final UserPresence userPresence = presenceClient.setPresence(UserPresenceCategory.AWAY);
    assertNotNull(userPresence);

    verifyUserPresence(userPresence, 1L, UserPresenceCategory.AWAY, 1503286569882L);
  }

  @Test(expected = APIClientErrorException.class)
  public void setUserPresenceByCategoryFailure400() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 400);

    presenceClient.setPresence(UserPresenceCategory.AWAY);
  }

  @Test(expected = SymClientException.class)
  public void setUserPresenceByCategoryFailure401() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 401);

    presenceClient.setPresence(UserPresenceCategory.AWAY);
  }

  @Test(expected = ServerErrorException.class)
  public void setUserPresenceByCategoryFailure500() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 500);

    presenceClient.setPresence(UserPresenceCategory.AWAY);
  }
  // End setPresence by category

  // setPresence by status
  @Test
  public void setUserPresenceByStatusStringSuccess() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE,
        "{"
            + "\"category\": \"AWAY\","
            + "\"userId\": 1,"
            + "\"timestamp\": 1503286569882"
            + "}"
    );

    final UserPresence userPresence = presenceClient.setPresence("AWAY");
    assertNotNull(userPresence);

    verifyUserPresence(userPresence, 1L, UserPresenceCategory.AWAY, 1503286569882L);
  }

  @Test(expected = APIClientErrorException.class)
  public void setUserPresenceByStatusStringFailure400() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 400);

    presenceClient.setPresence("AWAY");
  }

  @Test(expected = SymClientException.class)
  public void setUserPresenceByStatusStringFailure401() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 401);

    presenceClient.setPresence("AWAY");
  }

  @Test(expected = ServerErrorException.class)
  public void setUserPresenceByStatusStringFailure500() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE, "{}", 500);

    presenceClient.setPresence("AWAY");
  }
  // End setPresence by status

  // registerInterestExtUser
  @Test
  public void registerInterestExtUserSuccess() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,
        "{ "
            + "\"format\": \"TEXT\","
            + "\"message\": \"OK\" "
            + "}"
    );

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    assertNotNull(userIds);
    presenceClient.registerInterestExtUser(userIds);
    assertTrue(true);
  }

  @Test(expected = APIClientErrorException.class)
  public void registerInterestExtUserFailure400() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,"{}", 400);

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    assertNotNull(userIds);
    presenceClient.registerInterestExtUser(userIds);
    assertTrue(true);
  }

  @Test(expected = SymClientException.class)
  public void registerInterestExtUserFailure401() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,"{}", 401);

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    assertNotNull(userIds);
    presenceClient.registerInterestExtUser(userIds);
    assertTrue(true);
  }

  @Test(expected = ForbiddenException.class)
  public void registerInterestExtUserFailure403() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,"{}", 403);

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    assertNotNull(userIds);
    presenceClient.registerInterestExtUser(userIds);
    assertTrue(true);
  }

  @Test(expected = ServerErrorException.class)
  public void registerInterestExtUserFailure500() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,"{}", 500);

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    assertNotNull(userIds);
    presenceClient.registerInterestExtUser(userIds);
    assertTrue(true);
  }
  // End registerInterestExtUser

  // createPresenceFeed
  @Test
  public void createPresenceFeedSuccess() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }"
    );

    final String feedId = presenceClient.createPresenceFeed();
    assertEquals("c6ddc040-734c-40cb-9d33-20a5200486d8", feedId);
  }

  @Test(expected = APIClientErrorException.class)
  public void createPresenceFeedFailure400() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }", 400);

    presenceClient.createPresenceFeed();
  }

  @Test(expected = SymClientException.class)
  public void createPresenceFeedFailure401() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }", 401);

    presenceClient.createPresenceFeed();
  }

  @Test(expected = ForbiddenException.class)
  public void createPresenceFeedFailure403() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }", 403);

    presenceClient.createPresenceFeed();
  }

  @Test(expected = ServerErrorException.class)
  public void createPresenceFeedFailure500() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }", 500);

    presenceClient.createPresenceFeed();
  }
  // End createPresenceFeed

  // readPresenceFeed
  @Test
  public void readPresenceFeedSuccess() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "["
            + "{ "
            + "\"category\": \"AVAILABLE\","
            + "\"userId\": 1,"
            + "\"timestamp\": 1533928483800"
            + "},"
            + "{ "
            + "\"category\": \"ON_THE_PHONE\","
            + "\"userId\": 7078106103902,"
            + "\"timestamp\": 1489769156273"
            + "}"
            + "]"
    );

    final List<UserPresence> presenceList = presenceClient.readPresenceFeed(feedId);
    assertNotNull(presenceList);

    assertEquals(2, presenceList.size());
    verifyUserPresence(presenceList.get(0), 1L, UserPresenceCategory.AVAILABLE, 1533928483800L);
    verifyUserPresence(presenceList.get(1), 7078106103902L, UserPresenceCategory.ON_THE_PHONE, 1489769156273l);
  }

  @Test(expected = APIClientErrorException.class)
  public void readPresenceFeedSuccessFailure400() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "{}", 400);

    presenceClient.readPresenceFeed(feedId);
  }

  @Test(expected = SymClientException.class)
  public void readPresenceFeedSuccessFailure401() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "{}", 401);

    presenceClient.readPresenceFeed(feedId);
  }

  @Test(expected = ForbiddenException.class)
  public void readPresenceFeedSuccessFailure403() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "{}", 403);

    presenceClient.readPresenceFeed(feedId);
  }

  @Test(expected = ServerErrorException.class)
  public void readPresenceFeedSuccessFailure500() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "{}", 500);

    presenceClient.readPresenceFeed(feedId);
  }
  // End readPresenceFeed

  // deletePresenceFeed
  @Test
  public void deletePresenceFeedSuccess() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}"
    );

    presenceClient.deletePresenceFeed(feedId);

    assertTrue(true);
  }

  @Test(expected = APIClientErrorException.class)
  public void deletePresenceFeedFailure400() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}", 400);

    presenceClient.deletePresenceFeed(feedId);
  }

  @Test(expected = SymClientException.class)
  public void deletePresenceFeedFailure401() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}", 401);

    presenceClient.deletePresenceFeed(feedId);
  }

  @Test(expected = ForbiddenException.class)
  public void deletePresenceFeedFailure403() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}", 403);

    presenceClient.deletePresenceFeed(feedId);
  }

  @Test(expected = ServerErrorException.class)
  public void deletePresenceFeedFailure500() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}", 500);

    presenceClient.deletePresenceFeed(feedId);
  }
  // End deletePresenceFeed

  // setOtherUserPresence
  @Test
  public void setOtherUserPresenceSuccess() {

    stubPost(PodConstants.SET_OTHER_USER_PRESENCE,
        "{ "
            + "\"category\": \"BUSY\","
            + "\"userId\": 1,"
            + "\"timestamp\": 1503286569882"
            + "}"
    );

    final UserPresence userPresence = presenceClient.setOtherUserPresence(1L, UserPresenceCategory.BUSY);
    assertNotNull(userPresence);

    verifyUserPresence(userPresence, 1L, UserPresenceCategory.BUSY, 1503286569882L);
  }

  @Test(expected = APIClientErrorException.class)
  public void setOtherUserPresenceFailure400() {

    stubPost(PodConstants.SET_OTHER_USER_PRESENCE,"{}", 400);

    presenceClient.setOtherUserPresence(1L, UserPresenceCategory.BUSY);
  }

  @Test(expected = SymClientException.class)
  public void setOtherUserPresenceFailure401() {

    stubPost(PodConstants.SET_OTHER_USER_PRESENCE,"{}", 401);

    presenceClient.setOtherUserPresence(1L, UserPresenceCategory.BUSY);
  }

  @Test(expected = ForbiddenException.class)
  public void setOtherUserPresenceFailure403() {

    stubPost(PodConstants.SET_OTHER_USER_PRESENCE,"{}", 403);

    presenceClient.setOtherUserPresence(1L, UserPresenceCategory.BUSY);
  }

  @Test(expected = ServerErrorException.class)
  public void setOtherUserPresenceFailure500() {

    stubPost(PodConstants.SET_OTHER_USER_PRESENCE,"{}", 500);

    presenceClient.setOtherUserPresence(1L, UserPresenceCategory.BUSY);
  }
  // End setOtherUserPresence

  ////// Private method
  private void verifyUserPresence(final UserPresence userPresence, final Long userId, final UserPresenceCategory userPresenceCategory, final Long timestamp) {
    assertEquals(userPresenceCategory, userPresence.getCategory());
    assertEquals(userId.longValue(), userPresence.getUserId().longValue());
    assertEquals(timestamp.longValue(), userPresence.getTimestamp().longValue());
  }
}
