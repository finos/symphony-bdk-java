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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;
import clients.symphony.api.SignalsClient;
import clients.symphony.api.constants.AgentConstants;
import it.commons.BotTest;
import model.Signal;
import model.SignalSubscriberList;
import model.SignalSubscriptionResult;

public class SignalsClientTest extends BotTest {
  private SignalsClient signalsClient;

  @Before
  public void initClient() {
    signalsClient = new SignalsClient(symBotClient);
  }

  @Test
  public void listSignalsSuccessNoContent() {
    stubFor(get(urlEqualTo(AgentConstants.LISTSIGNALS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(204)));

    List<Signal> signals = signalsClient.listSignals(0, 0);

    assertNotNull(signals);
    assertEquals(0, signals.size());
  }

  @Test
  public void listSignalsSuccessWithContent() {
    stubFor(get(urlEqualTo(AgentConstants.LISTSIGNALS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"name\": \"Mention and keyword\",\r\n" +
                "    \"query\": \"HASHTAG:Hello OR POSTEDBY:10854618893681\",\r\n" +
                "    \"visibleOnProfile\": false,\r\n" +
                "    \"companyWide\": false,\r\n" +
                "    \"id\": \"5a0068344b570777718322a3\",\r\n" +
                "    \"timestamp\": 1509976116525\r\n" +
                "  }\r\n" +
                "]")));

    List<Signal> signals = signalsClient.listSignals(0, 0);

    assertNotNull(signals);
    assertEquals(1, signals.size());
    assertEquals("Mention and keyword", signals.get(0).getName());
  }

  @Test
  public void getSignalSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.GETSIGNAL.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"name\": \"my signal\",\r\n" +
                "    \"query\": \"HASHTAG:hashtag AND CASHTAG:cash\",\r\n" +
                "    \"visibleOnProfile\": true,\r\n" +
                "    \"companyWide\": false,\r\n" +
                "    \"id\": \"5a8daa0bb9d82100011d5095\",\r\n" +
                "    \"timestamp\": 1519233547982\r\n" +
                "}")));

    Signal signal = signalsClient.getSignal("1");

    assertNotNull(signal);
    assertEquals("my signal", signal.getName());
  }

  @Test
  public void createSignalSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.CREATESIGNAL))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"name\": \"hash and cash\",\r\n" +
                "    \"query\": \"HASHTAG:hash AND CASHTAG:cash\",\r\n" +
                "    \"visibleOnProfile\": true,\r\n" +
                "    \"companyWide\": false,\r\n" +
                "    \"id\": \"5a8da7edb9d82100011d508f\",\r\n" +
                "    \"timestamp\": 1519233005107\r\n" +
                "}")));

    Signal signal = new Signal();
    signal.setName("hash and cash");
    signal.setQuery("HASHTAG:hash AND CASHTAG:cash");
    signal.setVisibleOnProfile(true);
    signal.setCompanyWide(false);

    signal = signalsClient.createSignal(signal);

    assertNotNull(signal);
    assertEquals("5a8da7edb9d82100011d508f", signal.getId());
    assertEquals("hash and cash", signal.getName());
  }

  @Test
  public void updateSignalSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.UPDATESIGNAL.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"name\": \"hash only\",\r\n" +
                "    \"query\": \"HASHTAG:hash\",\r\n" +
                "    \"visibleOnProfile\": true,\r\n" +
                "    \"companyWide\": false,\r\n" +
                "    \"id\": \"1\",\r\n" +
                "    \"timestamp\": 1519233005107\r\n" +
                "}")));

    Signal signal = new Signal();
    signal.setId("1");
    signal.setName("hash only");
    signal.setQuery("HASHTAG:hash");
    signal.setVisibleOnProfile(true);
    signal.setCompanyWide(false);

    signal = signalsClient.updateSignal(signal);

    assertNotNull(signal);
    assertEquals("1", signal.getId());
    assertEquals("hash only", signal.getName());
  }

  @Test
  public void deleteSignalSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.DELETESIGNAL.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"format\": \"TEXT\",\r\n" +
                "    \"message\": \"Signal 1 deleted\"\r\n" +
                "}")));

    signalsClient.deleteSignal("1");
  }

  @Test
  public void subscribeSignalSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.SUBSCRIBESIGNAL.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"requestedSubscription\": 1,\r\n" +
                "    \"successfulSubscription\": 1,\r\n" +
                "    \"failedSubscription\": 0,\r\n" +
                "    \"subscriptionErrors\": []\r\n" +
                "}")));

    SignalSubscriptionResult result = signalsClient.subscribeSignal("1", true, null, false);

    assertNotNull(result);
    assertEquals(1, result.getSuccessfulSubscription());
    assertEquals(0, result.getFailedSubscription());
  }

  @Test
  public void unsubscribeSignalSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.UNSUBSCRIBESIGNAL.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"requestedSubscription\": 1,\r\n" +
                "    \"successfulSubscription\": 1,\r\n" +
                "    \"failedSubscription\": 0,\r\n" +
                "    \"subscriptionErrors\": []\r\n" +
                "}")));

    SignalSubscriptionResult result = signalsClient.unsubscribeSignal("1", true, null);

    assertNotNull(result);
    assertEquals(1, result.getSuccessfulSubscription());
    assertEquals(0, result.getFailedSubscription());
  }

  @Test
  public void getSignalSubscribersSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.GETSUBSCRIBERS.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"offset\": 0,\r\n" +
                "    \"hasMore\": true,\r\n" +
                "    \"total\": 3,\r\n" +
                "    \"data\": [\r\n" +
                "        {\r\n" +
                "            \"pushed\": false,\r\n" +
                "            \"owner\": true,\r\n" +
                "            \"subscriberName\": \"John Doe 01\",\r\n" +
                "            \"userId\": 68719476742,\r\n" +
                "            \"timestamp\": 1519231972000\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"pushed\": false,\r\n" +
                "            \"owner\": false,\r\n" +
                "            \"subscriberName\": \"John Doe 02\",\r\n" +
                "            \"userId\": 68719476742,\r\n" +
                "            \"timestamp\": 1519296588000\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"pushed\": false,\r\n" +
                "            \"owner\": false,\r\n" +
                "            \"subscriberName\": \"John Doe 03\",\r\n" +
                "            \"userId\": 68719476744,\r\n" +
                "            \"timestamp\": 1519296589000\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}")));

    SignalSubscriberList subList = signalsClient.getSignalSubscribers("1", 0, 0);

    assertNotNull(subList);
    assertEquals(3, subList.getTotal());
  }
}
