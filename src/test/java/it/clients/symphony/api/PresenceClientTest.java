package it.clients.symphony.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;
import clients.symphony.api.PresenceClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
import model.UserPresence;

public class PresenceClientTest extends BotTest {
  private PresenceClient presenceClient;

  @Before
  public void initClient() {
    presenceClient = new PresenceClient(symBotClient);
  }

  @Test
  public void getUserPresenceSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERPRESENCE.replace("{uid}", Long.toString(1L)).concat("?local=true")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("local", equalTo("true"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"category\": \"AVAILABLE\", \"userId\": 1, \"timestamp\": 1533928483800 }")));

    UserPresence userPresence = presenceClient.getUserPresence(1L, true);

    assertNotNull(userPresence);
    assertEquals(1L, userPresence.getUserId().longValue());
    assertEquals("AVAILABLE", userPresence.getCategory());
  }

  @Test
  public void setUserPresenceSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SETPRESENCE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"category\": \"AWAY\", \"userId\": 1, \"timestamp\": 1503286569882 }")));

    UserPresence userPresence = presenceClient.setPresence("AWAY");

    assertNotNull(userPresence);
    assertEquals(1L, userPresence.getUserId().longValue());
    assertEquals("AWAY", userPresence.getCategory());
  }

  @Test
  public void registerInterestExtUserSuccess() {
    stubFor(post(urlEqualTo(PodConstants.REGISTERPRESENCEINTEREST))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{ \"format\": \"TEXT\", \"message\": \"OK\" }")));

    List<Long> userIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
    presenceClient.registerInterestExtUser(userIds);
  }

}
