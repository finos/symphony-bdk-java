package it.clients.symphony.api;

import clients.symphony.api.DatafeedClient;
import clients.symphony.api.constants.AgentConstants;
import it.commons.BotTest;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import model.DatafeedEvent;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatafeedClientTest extends BotTest {
  private DatafeedClient datafeedClient;

  @Before
  public void initClient() {
    datafeedClient = new DatafeedClient(symBotClient);
  }

  @Test
  public void createDatafeedSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.CREATEDATAFEED))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"id\": \"8e7c8672-220\"\r\n" +
                "}")));

    String datafeedId = datafeedClient.createDatafeed();

    assertNotNull(datafeedId);
    assertEquals("8e7c8672-220", datafeedId);
  }

  @Test
  public void readDatafeedSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "    {\r\n" +
                "        \"id\": \"ulPr8a:eFFDL7\",\r\n" +
                "        \"messageId\": \"CszQa6uPAA9V\",\r\n" +
                "        \"timestamp\": 1536346282592,\r\n" +
                "        \"type\": \"MESSAGESENT\",\r\n" +
                "        \"initiator\": {\r\n" +
                "            \"user\": {\r\n" +
                "                \"userId\": 1456852,\r\n" +
                "                \"displayName\": \"Local Bot01\",\r\n" +
                "                \"email\": \"bot.user1@test.com\",\r\n" +
                "                \"username\": \"bot.user1\"\r\n" +
                "            }\r\n" +
                "        },\r\n" +
                "        \"payload\": {\r\n" +
                "            \"messageSent\": {\r\n" +
                "                \"message\": {\r\n" +
                "                    \"messageId\": \"CszQa6uPAA9\",\r\n" +
                "                    \"timestamp\": 1536346282592,\r\n" +
                "                    \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\">Hello World</div>\",\r\n" +
                "                    \"data\": \"{\\\"entityIdentifier\\\":{\\\"type\\\":\\\"org.symphonyoss.fin.security\\\",\\\"version\\\":\\\"0.1\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.isin\\\",\\\"value\\\":\\\"US0378\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.cusip\\\",\\\"value\\\":\\\"037\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.openfigi\\\",\\\"value\\\":\\\"BBG000\\\"}]}}\",\r\n" +
                "                    \"user\": {\r\n" +
                "                        \"userId\": 14568529,\r\n" +
                "                        \"displayName\": \"Local Bot01\",\r\n" +
                "                        \"email\": \"bot.user1@ntest.com\",\r\n" +
                "                        \"username\": \"bot.user1\"\r\n" +
                "                    },\r\n" +
                "                    \"stream\": {\r\n" +
                "                        \"streamId\": \"wTmSDJSNPXgB\",\r\n" +
                "                        \"streamType\": \"ROOM\"\r\n" +
                "                    },\r\n" +
                "                    \"externalRecipients\": false,\r\n" +
                "                    \"userAgent\": \"Agent-2.2.8-Linux-4.9.77-31.58.amzn1.x86_64\",\r\n" +
                "                    \"originalFormat\": \"com.symphony.messageml.v2\"\r\n" +
                "                }\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "]")));

    List<DatafeedEvent> events = datafeedClient.readDatafeed("1");

    assertNotNull(events);
    assertEquals(1, events.size());
    assertEquals("CszQa6uPAA9V", events.get(0).getMessageId());
  }

  @Test(expected = javax.ws.rs.ProcessingException.class)
  public void readDatafeedConnectionTimeout() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse().withStatus(200).withFixedDelay(10000)));

    datafeedClient.readDatafeed("1");
  }

}
