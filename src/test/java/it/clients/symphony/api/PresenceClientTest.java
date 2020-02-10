package it.clients.symphony.api;

import clients.symphony.api.PresenceClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;

import java.beans.Transient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import model.UserPresence;
import model.UserPresenceCategory;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PresenceClientTest extends BotTest {

  private PresenceClient presenceClient;

  @Before
  public void initClient() {
    this.presenceClient = new PresenceClient(this.symBotClient);
  }

  @Test
  public void getUserPresenceSuccess() {

    stubGet(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true"),
        "{ \"category\": \"AVAILABLE\", \"userId\": 1, \"timestamp\": 1533928483800 }"
    );

    UserPresence userPresence = presenceClient.getUserPresence(1L, true);

    assertNotNull(userPresence);
    assertEquals(1L, userPresence.getUserId().longValue());
    assertEquals(UserPresenceCategory.AVAILABLE, userPresence.getCategory());
  }

  @Test
  public void getCallingUserPresenceSuccess() {

    stubGet(PodConstants.GET_OR_SET_PRESENCE,
        "{ \"category\": \"AVAILABLE\", \"userId\": 1, \"timestamp\": 1533928483800 }"
    );

    UserPresence userPresence = presenceClient.getUserPresence();

    assertNotNull(userPresence);
    assertEquals(1L, userPresence.getUserId().longValue());
    assertEquals(UserPresenceCategory.AVAILABLE, userPresence.getCategory());
  }

  @Test
  public void getAllPresenceSuccess() {

    stubGet(PodConstants.GET_ALL_PRESENCE + "?lastUserId=1&limit=50",
        "[{ \"category\": \"AVAILABLE\", \"userId\": 1, \"timestamp\": 1533928483800 }]"
    );

    List<UserPresence> presenceList = presenceClient.getAllPresence(1L, 50);

    assertNotNull(presenceList);
    assertEquals(1, presenceList.size());
  }

  @Test
  public void setUserPresenceSuccess() {

    stubPost(PodConstants.GET_OR_SET_PRESENCE,
        "{ \"category\": \"AWAY\", \"userId\": 1, \"timestamp\": 1503286569882 }"
    );

    UserPresence userPresence = presenceClient.setPresence(UserPresenceCategory.AWAY);

    assertNotNull(userPresence);
    assertEquals(1L, userPresence.getUserId().longValue());
    assertEquals(UserPresenceCategory.AWAY, userPresence.getCategory());
  }

  @Test
  public void setOtherUserPresenceSuccess() {
    // TODO
  }

  @Test
  public void registerInterestExtUserSuccess() {

    stubPost(PodConstants.REGISTERPRESENCEINTEREST,
        "{ \"format\": \"TEXT\", \"message\": \"OK\" }"
    );

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    this.presenceClient.registerInterestExtUser(userIds);
  }

  @Test
  public void createPresenceFeedSuccess() {
    stubPost(PodConstants.PRESENCE_FEED_CREATE,
        "{ \"id\": \"c6ddc040-734c-40cb-9d33-20a5200486d8\" }"
    );

    final String feedId = this.presenceClient.createPresenceFeed();
    assertEquals("c6ddc040-734c-40cb-9d33-20a5200486d8", feedId);
  }

  @Test
  public void readPresenceFeedSuccess() {

    final String feedId = UUID.randomUUID().toString();

    stubPost(PodConstants.PRESENCE_FEED_READ.replace("{feedId}", feedId),
        "[{ \"category\": \"AVAILABLE\", \"userId\": 1, \"timestamp\": 1533928483800 }]"
    );

    final List<UserPresence> presenceList = this.presenceClient.readPresenceFeed(feedId);

    assertNotNull(presenceList);
    assertEquals(1, presenceList.size());
  }

  @Test
  public void deletePresenceFeedSuccess() {

    final String feedId = UUID.randomUUID().toString();

    stubDelete(PodConstants.PRESENCE_FEED_DELETE.replace("{feedId}", feedId),
        "{}"
    );

    this.presenceClient.deletePresenceFeed(feedId);
  }
}
