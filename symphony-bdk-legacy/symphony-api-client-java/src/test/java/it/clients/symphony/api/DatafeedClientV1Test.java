package it.clients.symphony.api;

import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.PodConstants;
import clients.symphony.api.DatafeedClient;
import clients.symphony.api.constants.AgentConstants;
import exceptions.SymClientException;
import it.commons.BotTest;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import model.DatafeedEvent;
import model.EventPayload;
import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.User;
import model.events.MessageSent;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class DatafeedClientV1Test extends BotTest {
  private DatafeedClient datafeedClient;

  @Before
  public void initClient() throws IOException {
    stubGet(PodConstants.GETSESSIONUSER,
            readResourceContent("/response_content/authenticate/get_session_user.json"));
    SymBotRSAAuth auth = new SymBotRSAAuth(config);
    auth.setSessionToken("eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJ0ZXN0LWJvdCIsImlzcyI6InN5bXBob255Iiwic2Vzc2lvbklkIjoiZmRiOTAxMmQzOTgwMGE3NzNkMTJjYWFmZGY5MjU4ZjZjOWEyMTE2MmYyZDU1ODQ3M2Y5ZDU5MDUyNjA0Mjg1ZjU0MWM5Yzg0Mzc5YTE0MjZmODNiZmZkZTljYmQ5NjRjMDAwMDAxNmRmMjMyODIwNTAwMDEzZmYwMDAwMDAxZTgiLCJ1c2VySWQiOiIzNTE3NzUwMDE0MTIwNzIifQ.DlQ_-sAqZLlAcVTr7t_PaYt_Muq_P82yYrtbEEZWMpHMl-7qCciwfi3uXns7oRbc1uvOrhQd603VKQJzQxaZBZBVlUPS-2ysH0tBpCS57ocTS6ZwtQwPLCZYdT-EZ70EzQ95kG6P5TrLENH6UveohgeDdmyzSPOEiwyEUjjmzaXFE8Tu0R3xQDwl-BKbsyUAAgd1X7T0cUDC3WIDl9xaTvyxavep4ZJnZJl4qPc1Tan0yU7JrxtXeD8uwNYlKLudT3UVxduFPMQP_2jyj5Laa-YWGKvRtXkcy2d3hzf4ll1l1wVnyJc1e6hW2EnRlff_Nxge-QCJMcZ_ALrpOUtAyQ");
    symBotClient = SymBotClient.initBot(config, auth);
    datafeedClient = symBotClient.getDatafeedClient();
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

    try {

      assertNotNull(datafeedClient);

      final String datafeedId = datafeedClient.createDatafeed();
      assertNotNull(datafeedId);
      assertEquals("8e7c8672-220", datafeedId);

    } catch (SymClientException e) {
      fail();
    }
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

    try {

      assertNotNull(datafeedClient);

      final List<DatafeedEvent> events = datafeedClient.readDatafeed("1");
      assertNotNull(events);
      assertEquals(1, events.size());

      final DatafeedEvent event = events.get(0);
      assertNotNull(event);
      assertEquals("ulPr8a:eFFDL7", event.getId());
      assertEquals("CszQa6uPAA9V", event.getMessageId());
      assertEquals(1536346282592L, event.getTimestamp().longValue());
      assertEquals("MESSAGESENT", event.getType());

      final Initiator initiator = event.getInitiator();
      assertNotNull(initiator);
      final User user = initiator.getUser();
      assertNotNull(user);
      assertEquals(1456852L, user.getUserId().longValue());
      assertEquals("Local Bot01", user.getDisplayName());
      assertEquals("bot.user1@test.com", user.getEmail());
      assertEquals("bot.user1", user.getUsername());

      final EventPayload payload = event.getPayload();
      assertNotNull(payload);
      final MessageSent messageSent = payload.getMessageSent();
      assertNotNull(messageSent);
      final InboundMessage message = messageSent.getMessage();
      assertNotNull(message);
      assertEquals("CszQa6uPAA9", message.getMessageId());
      assertEquals(1536346282592L, message.getTimestamp().longValue());
      final String expectedMessage = "<div data-format=\"PresentationML\" data-version=\"2.0\">Hello World</div>";
      assertEquals(expectedMessage, message.getMessage());
      final String expectedData = "{\"entityIdentifier\":{\"type\":\"org.symphonyoss.fin.security\",\"version\":\"0.1\",\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.isin\",\"value\":\"US0378\"},{\"type\":\"org.symphonyoss.fin.security.id.cusip\",\"value\":\"037\"},{\"type\":\"org.symphonyoss.fin.security.id.openfigi\",\"value\":\"BBG000\"}]}}";
      assertEquals(expectedData, message.getData());

      final User messageUser = message.getUser();
      assertNotNull(messageUser);
      assertEquals(14568529L, messageUser.getUserId().longValue());
      assertEquals("Local Bot01", messageUser.getDisplayName());
      assertEquals("bot.user1@ntest.com", messageUser.getEmail());
      assertEquals("bot.user1", messageUser.getUsername());

      final Stream stream = message.getStream();
      assertNotNull(stream);
      assertEquals("wTmSDJSNPXgB", stream.getStreamId());
      assertEquals("ROOM", stream.getStreamType());

      assertFalse(message.getExternalRecipients());
      assertEquals("Agent-2.2.8-Linux-4.9.77-31.58.amzn1.x86_64", message.getUserAgent());
      assertEquals("com.symphony.messageml.v2", message.getOriginalFormat());

    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = javax.ws.rs.ProcessingException.class)
  public void readDatafeedConnectionTimeout() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse().withStatus(200).withFixedDelay(10000)));

    try {

      datafeedClient.readDatafeed("1");

    } catch (SymClientException e) {
      fail();
    }
  }

}
