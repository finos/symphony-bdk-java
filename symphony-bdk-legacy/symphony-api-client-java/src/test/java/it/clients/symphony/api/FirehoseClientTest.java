package it.clients.symphony.api;

import clients.symphony.api.FirehoseClient;
import clients.symphony.api.constants.AgentConstants;
import exceptions.SymClientException;
import it.commons.BotTest;
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
import org.junit.Ignore;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class FirehoseClientTest extends BotTest {
  private FirehoseClient firehoseClient;

  @Before
  public void initClient() {
    firehoseClient = new FirehoseClient(symBotClient);
  }

  @Test
  public void createFirehoseSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.CREATEFIREHOSE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"id\": \"8e7c8672-220\"\r\n" +
                "}")));

    try {

      assertNotNull(firehoseClient);

      final String firehoseId = firehoseClient.createFirehose();
      assertNotNull(firehoseId);
      assertEquals("8e7c8672-220", firehoseId);

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test
  public void readFirehoseSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.READFIREHOSE.replace("{id}", "1")))
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

      assertNotNull(firehoseClient);

      final List<DatafeedEvent> events = firehoseClient.readFirehose("1");

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

      final User messageUser = message.getUser();
      assertNotNull(messageUser);
      assertEquals(14568529L, messageUser.getUserId().longValue());
      assertEquals("Local Bot01", messageUser.getDisplayName());
      assertEquals("bot.user1@test.com", messageUser.getEmail());
      assertEquals("bot.user1", messageUser.getUsername());

      final Stream stream = message.getStream();
      assertNotNull(stream);
      assertEquals("wTmSDJSNPXgB", stream.getStreamId());
      assertEquals("ROOM", stream.getStreamType());

      assertFalse(message.getExternalRecipients());
      assertEquals("Agent-2.2.8-Linux-4.9.77-31.58.amzn1.x86_64", message.getUserAgent());
      assertEquals("com.symphony.messageml.v2", message.getOriginalFormat());


    } catch (SymClientException e) {
      fail();
    }
  }

}
