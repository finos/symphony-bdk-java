package com.symphony.bdk.core.service.presence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.presence.constant.PresenceStatus;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.PresenceApi;
import com.symphony.bdk.gen.api.model.V2Presence;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class PresenceServiceTest {

  private static final String V2_GET_PRESENCE = "/pod/v2/user/presence";
  private static final String V2_GET_ALL_PRESENCE = "/pod/v2/users/presence";
  private static final String V2_GET_USER_PRESENCE = "/pod/v3/user/{uid}/presence";
  private static final String V1_EXTERNAL_PRESENCE_INTEREST = "/pod/v1/user/presence/register";
  private static final String V2_SET_PRESENCE = "/pod/v2/user/presence";
  private static final String V1_CREATE_PRESENCE_FEED = "/pod/v1/presence/feed/create";
  private static final String V1_READ_PRESENCE_FEED = "/pod/v1/presence/feed/{feedId}/read";
  private static final String V1_DELETE_PRESENCE_FEED = "/pod/v1/presence/feed/{feedId}/delete";
  private static final String V3_SET_USER_PRESENCE = "/pod/v3/user/presence";

  private PresenceService service;
  private PresenceApi spiedPresenceApi;
  private MockApiClient mockApiClient;
  private BotAuthSession authSession;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(BotAuthSession.class);
    ApiClient podClient = mockApiClient.getApiClient("/pod");
    PresenceApi presenceApi = new PresenceApi(podClient);
    this.spiedPresenceApi = spy(presenceApi);
    this.service = new PresenceService(spiedPresenceApi, authSession, new RetryWithRecoveryBuilder<>());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void nonOboEndpointShouldThrowExceptionInOboMode() {
    this.service = new PresenceService(spiedPresenceApi, new RetryWithRecoveryBuilder<>());
    assertThrows(IllegalStateException.class, () -> this.service.getPresence());
  }

  @Test
  void getPresenceOboMode() {
    this.service = new PresenceService(spiedPresenceApi, new RetryWithRecoveryBuilder<>());

    this.mockApiClient.onGet(V2_GET_PRESENCE,
        "{\n"
            + "    \"category\": \"AVAILABLE\",\n"
            + "    \"userId\": 14568529068038,\n"
            + "    \"timestamp\": 1533928483800\n"
            + "}");

    final V2Presence presence = this.service.obo(this.authSession).getPresence();

    assertEquals(presence.getCategory(), "AVAILABLE");
    assertEquals(presence.getUserId(), 14568529068038L);
  }

  @Test
  void getPresenceTest() {
    this.mockApiClient.onGet(V2_GET_PRESENCE,
        "{\n"
            + "    \"category\": \"AVAILABLE\",\n"
            + "    \"userId\": 14568529068038,\n"
            + "    \"timestamp\": 1533928483800\n"
            + "}");

    V2Presence presence = this.service.getPresence();

    assertEquals(presence.getCategory(), "AVAILABLE");
    assertEquals(presence.getUserId(), 14568529068038L);
  }

  @Test
  void getPresenceFailed() {
    this.mockApiClient.onGet(400, V2_GET_PRESENCE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getPresence());
  }

  @Test
  void getAllUserPresenceTest() {
    this.mockApiClient.onGet(V2_GET_ALL_PRESENCE, "[\n"
        + "  {\n"
        + "    \"category\": \"AVAILABLE\",\n"
        + "    \"userId\": 14568529068038,\n"
        + "    \"timestamp\": 1533928483800\n"
        + "  },\n"
        + "  {\n"
        + "    \"category\": \"OFFLINE\",\n"
        + "    \"userId\": 974217539631,\n"
        + "    \"timestamp\": 1503286226030\n"
        + "  }  \n"
        + "]");

    List<V2Presence> presenceList = this.service.listPresences(1234L, 5000);

    assertEquals(presenceList.size(), 2);
    assertEquals(presenceList.get(0).getUserId(), 14568529068038L);
    assertEquals(presenceList.get(0).getCategory(), "AVAILABLE");
    assertEquals(presenceList.get(1).getUserId(), 974217539631L);
    assertEquals(presenceList.get(1).getCategory(), "OFFLINE");
  }

  @Test
  void getAllUserPresenceFailed() {
    this.mockApiClient.onGet(400, V2_GET_ALL_PRESENCE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listPresences(1234L, 5000));
  }

  @Test
  void getUserPresenceTest() {
    this.mockApiClient.onGet(V2_GET_USER_PRESENCE.replace("{uid}", "12345"),
        "{\n"
            + "  \"category\": \"AVAILABLE\",\n"
            + "  \"userId\": 349871117483,\n"
            + "  \"timestamp\": 1503285368906\n"
            + "}");

    V2Presence presence = this.service.getUserPresence(12345L, true);

    assertEquals(presence.getUserId(), 349871117483L);
    assertEquals(presence.getCategory(), "AVAILABLE");
  }

  @Test
  void  getUserPresenceFailed() {
    this.mockApiClient.onGet(400, V2_GET_USER_PRESENCE.replace("{uid}", "12345"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getUserPresence(12345L, true));
  }

  @Test
  void externalPresenceInterestTest() throws ApiException {
    this.mockApiClient.onPost(V1_EXTERNAL_PRESENCE_INTEREST, "{}");

    List<Long> userIds = Collections.singletonList(1234L);
    this.service.externalPresenceInterest(userIds);

    verify(this.spiedPresenceApi).v1UserPresenceRegisterPost("1234", userIds);
  }

  @Test
  void externalPresenceInterestFailed() {
    this.mockApiClient.onPost(400, V1_EXTERNAL_PRESENCE_INTEREST, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.externalPresenceInterest(Collections.singletonList(1234L)));
  }

  @Test
  void setPresenceTest() {
    this.mockApiClient.onPost(V2_SET_PRESENCE,
        "{\n"
            + "  \"category\": \"AWAY\",\n"
            + "  \"userId\": 349871117483,\n"
            + "  \"timestamp\": 1503286569882\n"
            + "}");

    V2Presence presence = this.service.setPresence(PresenceStatus.AWAY, true);

    assertEquals(presence.getCategory(), "AWAY");
    assertEquals(presence.getUserId(), 349871117483L);
  }

  @Test
  void setPresenceFailed() {
    this.mockApiClient.onPost(400 , V2_SET_PRESENCE, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.setPresence(PresenceStatus.AWAY, true));
  }

  @Test
  void createPresenceFeedTest() {
    this.mockApiClient.onPost(V1_CREATE_PRESENCE_FEED,
        "{\n"
            + "  \"id\": \"c4dca251-8639-48db-a9d4-f387089e17cf\"\n"
            + "}");

    String feedId = this.service.createPresenceFeed();

    assertEquals(feedId, "c4dca251-8639-48db-a9d4-f387089e17cf");
  }

  @Test
  void createPresenceFeedFailed() {
    this.mockApiClient.onPost(400, V1_CREATE_PRESENCE_FEED, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.createPresenceFeed());
  }

  @Test
  void readPresenceFeedTest() {
    this.mockApiClient.onGet(V1_READ_PRESENCE_FEED.replace("{feedId}", "1234"),
        "[\n"
            + "  { \n"
            + "    \"category\": \"AVAILABLE\", \n"
            + "    \"userId\": \"7078106103901\", \n"
            + "    \"timestamp\": \"1489769156271\"\n"
            + "  },\n"
            + "  { \n"
            + "    \"category\": \"ON_THE_PHONE\", \n"
            + "    \"userId\": \"7078106103902\", \n"
            + "    \"timestamp\": \"1489769156273\"\n"
            + "  }\n"
            + "]");
    
    List<V2Presence> presenceList = this.service.readPresenceFeed("1234");
    
    assertEquals(presenceList.size(), 2);
    assertEquals(presenceList.get(0).getUserId(), 7078106103901L);
    assertEquals(presenceList.get(0).getCategory(), "AVAILABLE");
    assertEquals(presenceList.get(1).getUserId(), 7078106103902L);
    assertEquals(presenceList.get(1).getCategory(), "ON_THE_PHONE");
  }
  
  @Test
  void readPresenceFeedFailed() {
    this.mockApiClient.onGet(400, V1_READ_PRESENCE_FEED.replace("{feedId}", "1234"), "{}");
    
    assertThrows(ApiRuntimeException.class, () -> this.service.readPresenceFeed("1234"));
  }
  
  @Test
  void deletePresenceFeedTest() {
    this.mockApiClient.onPost(V1_DELETE_PRESENCE_FEED.replace("{feedId}", "1234"),
        "{\n"
            + "  \"id\": \"1234\"\n"
            + "}");

    String feedId = this.service.deletePresenceFeed("1234");

    assertEquals(feedId, "1234");
  }

  @Test
  void deletePresenceFeedFailed() {
    this.mockApiClient.onPost(400, V1_DELETE_PRESENCE_FEED.replace("{feedId}", "1234"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.deletePresenceFeed("1234"));
  }

  @Test
  void setUserPresenceTest() {
    this.mockApiClient.onPost(V3_SET_USER_PRESENCE,
        "{\n"
            + "  \"category\": \"BUSY\",\n"
            + "  \"userId\": 349871117483,\n"
            + "  \"timestamp\": 1503286872978\n"
            + "}");

    V2Presence presence = this.service.setUserPresence(349871117483L, PresenceStatus.BUSY, true);

    assertEquals(presence.getUserId(), 349871117483L);
    assertEquals(presence.getCategory(), "BUSY");
  }
}
