package it.clients.symphony.api;

import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.PodConstants;
import clients.symphony.api.DatafeedClient;
import clients.symphony.api.constants.AgentConstants;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.SymClientException;
import it.commons.BotTest;
import model.DatafeedEvent;
import model.EventPayload;
import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.User;
import model.datafeed.DatafeedV2;
import model.datafeed.DatafeedV2EventList;
import model.events.MessageSent;
import org.eclipse.jetty.util.IO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class DatafeedClientV2Test extends BotTest {

    private  static  final Logger logger = LoggerFactory.getLogger(DatafeedClientV2Test.class);
    private DatafeedClient datafeedClient;

    @Before
    public void initClient() throws IOException {
        stubGet(PodConstants.GETSESSIONUSER,
                readResourceContent("/response_content/authenticate/get_session_user.json"));
        config.setDatafeedVersion("v2");
        SymBotRSAAuth auth = new SymBotRSAAuth(config);
        auth.setSessionToken("eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJ0ZXN0LWJvdCIsImlzcyI6InN5bXBob255Iiwic2Vzc2lvbklkIjoiZmRiOTAxMmQzOTgwMGE3NzNkMTJjYWFmZGY5MjU4ZjZjOWEyMTE2MmYyZDU1ODQ3M2Y5ZDU5MDUyNjA0Mjg1ZjU0MWM5Yzg0Mzc5YTE0MjZmODNiZmZkZTljYmQ5NjRjMDAwMDAxNmRmMjMyODIwNTAwMDEzZmYwMDAwMDAxZTgiLCJ1c2VySWQiOiIzNTE3NzUwMDE0MTIwNzIifQ.DlQ_-sAqZLlAcVTr7t_PaYt_Muq_P82yYrtbEEZWMpHMl-7qCciwfi3uXns7oRbc1uvOrhQd603VKQJzQxaZBZBVlUPS-2ysH0tBpCS57ocTS6ZwtQwPLCZYdT-EZ70EzQ95kG6P5TrLENH6UveohgeDdmyzSPOEiwyEUjjmzaXFE8Tu0R3xQDwl-BKbsyUAAgd1X7T0cUDC3WIDl9xaTvyxavep4ZJnZJl4qPc1Tan0yU7JrxtXeD8uwNYlKLudT3UVxduFPMQP_2jyj5Laa-YWGKvRtXkcy2d3hzf4ll1l1wVnyJc1e6hW2EnRlff_Nxge-QCJMcZ_ALrpOUtAyQ");
        symBotClient = SymBotClient.initBot(config, auth);
        datafeedClient = symBotClient.getDatafeedClient();
    }

    // createDatafeed
  @Test
  public void createDatafeedSuccess201() throws IOException {
    stubPost(AgentConstants.CREATEDATAFEEDV2.replace("{id}", "8e7c8672220723985e8c42485edft85eg9ef7"),
        readResourceContent("/response_content/datafeedv2/create_datafeedv2.json"), 201);

    try {

      assertNotNull(datafeedClient);

      final String datafeedId = datafeedClient.createDatafeed();
      assertNotNull(datafeedId);

      assertEquals("8e7c8672220723985e8c42485edft85eg9ef7", datafeedId);

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void createDatafeedFailure400() throws IOException {
    stubPost(AgentConstants.CREATEDATAFEEDV2.replace("{id}", "8e7c8672220723985e8c42485edft85eg9ef7"),
        readResourceContent("/response_content/datafeedv2/create_datafeedv2.json"), 400);

    assertNotNull(datafeedClient);

    final String datafeed = datafeedClient.createDatafeed();
  }

  @Test(expected = SymClientException.class)
  public void createDatafeedFailure401() throws IOException {
    stubPost(AgentConstants.CREATEDATAFEEDV2.replace("{id}", "8e7c8672220723985e8c42485edft85eg9ef7"),
        readResourceContent("/response_content/datafeedv2/create_datafeedv2.json"), 401);

    assertNotNull(datafeedClient);

    final String datafeed = datafeedClient.createDatafeed();
  }

  @Test(expected = ServerErrorException.class)
  public void createDatafeedFailure500() throws IOException {
    stubPost(AgentConstants.CREATEDATAFEEDV2.replace("{id}", "8e7c8672220723985e8c42485edft85eg9ef7"),
        readResourceContent("/response_content/datafeedv2/create_datafeedv2.json"), 500);

    assertNotNull(datafeedClient);

    final String datafeed = datafeedClient.createDatafeed();
  }
  // End createDatafeed

  // readDatafeed
  @Test
  public void readDatafeedV2_oldSuccess() throws IOException {
    stubPost(AgentConstants.READDATAFEEDV2.replace("{id}", "21449143d35a86461e254d28697214b4_f"),
        readResourceContent("/response_content/datafeedv2/read_datafeedv2_old.json"), 200);

    try {

      assertNotNull(datafeedClient);

      final List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed("21449143d35a86461e254d28697214b4_f");
      assertNotNull(datafeedEvents);

      assertEquals("ack_id_string", datafeedClient.getAckId());

      assertEquals(1, datafeedEvents.size());

      final DatafeedEvent event = datafeedEvents.get(0);
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

  @Ignore
  @Test
  public void readDatafeedV2Success(){
//    stubPost(AgentConstants.READDATAFEEDV2.replace("{id}", "21449143d35a86461e254d28697214b4_f"),
//        readResourceContent("/response_content/datafeedv2/read_datafeedv2.json"), 200);
    stubFor(post(urlEqualTo(AgentConstants.READDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "[\n"
                + "    {\n"
                + "        \"id\": \"ulPr8a:eFFDL7\",\n"
                + "        \"messageId\": \"CszQa6uPAA9V\",\n"
                + "        \"timestamp\": 1536346282592,\n"
                + "        \"type\": \"MESSAGESENT\",\n"
                + "        \"initiator\": {\n"
                + "            \"user\": {\n"
                + "                \"userId\": 1456852,\n"
                + "                \"displayName\": \"Local Bot01\",\n"
                + "                \"email\": \"bot.user1@test.com\",\n"
                + "                \"username\": \"bot.user1\"\n"
                + "            }\n"
                + "        },\n"
                + "        \"payload\": {\n"
                + "            \"messageSent\": {\n"
                + "                \"message\": {\n"
                + "                    \"messageId\": \"CszQa6uPAA9\",\n"
                + "                    \"timestamp\": 1536346282592,\n"
                + "                    \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\">Hello World</div>\",\n"
                + "                    \"data\": \"{\\\"entityIdentifier\\\":{\\\"type\\\":\\\"org.symphonyoss.fin.security\\\",\\\"version\\\":\\\"0.1\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.isin\\\",\\\"value\\\":\\\"US0378\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.cusip\\\",\\\"value\\\":\\\"037\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.openfigi\\\",\\\"value\\\":\\\"BBG000\\\"}]}}\",\n"
                + "                    \"user\": {\n"
                + "                        \"userId\": 14568529,\n"
                + "                        \"displayName\": \"Local Bot01\",\n"
                + "                        \"email\": \"bot.user1@ntest.com\",\n"
                + "                        \"username\": \"bot.user1\"\n"
                + "                    },\n"
                + "                    \"stream\": {\n"
                + "                        \"streamId\": \"wTmSDJSNPXgB\",\n"
                + "                        \"streamType\": \"ROOM\"\n"
                + "                    },\n"
                + "                    \"externalRecipients\": false,\n"
                + "                    \"userAgent\": \"Agent-2.2.8-Linux-4.9.77-31.58.amzn1.x86_64\",\n"
                + "                    \"originalFormat\": \"com.symphony.messageml.v2\",\n"
                + "                    \"sid\": \"a4d08d18-0729-4b54-9c4568da\"\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "]\n"
                + "}")));

    try {

      assertNotNull(datafeedClient);

      final List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed("1");
      assertNotNull(datafeedEvents);

      assertEquals(1, datafeedEvents.size());

      final DatafeedEvent event = datafeedEvents.get(0);
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
      final String expectedData = "{\"entityIdentifier\":{\"type\":\"org.symphonyoss.fin.security\",\"version\":\"0.1\",\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.isin\",\"value\":\"US0378\"},{\"type\":\"org.symphonyoss.fin.security.id.cusip\",\"value\":\"037\"},{\"type\":\"org.symphonyoss.fin.security.id.openfigi\",\"value\":\"BBG000\"}]}}";
      assertEquals(expectedMessage, message.getMessage());

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
      assertEquals("a4d08d18-0729-4b54-9c4568da", message.getSid());

    } catch (SymClientException e) {
      fail();
    }
  }

  @Ignore
  @Test
  public void readDatafeedV5Success() {
//    stubPost(AgentConstants.READDATAFEEDV2.replace("{id}", "21449143d35a86461e254d28697214b4_f"),
//        readResourceContent("/response_content/datafeedv2/read_datafeedv5.json"), 200);

    stubFor(post(urlEqualTo(AgentConstants.READDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "[\n"
                + "    {\n"
                + "        \"id\": \"eventId\",\n"
                + "        \"timestamp\": 1536346282592,\n"
                + "        \"type\": \"MESSAGESENT\",\n"
                + "        \"initiator\": {\n"
                + "            \"user\": {\n"
                + "                \"userId\": 1456852,\n"
                + "                \"displayName\": \"User 1\",\n"
                + "                \"email\": \"user1@test.com\",\n"
                + "                \"username\": \"user1\"\n"
                + "            }\n"
                + "        },\n"
                + "        \"payload\": {\n"
                + "            \"messageSent\": {\n"
                + "                \"message\": {\n"
                + "                    \"messageId\": \"CszQa6uPAA9\",\n"
                + "                    \"timestamp\": 1536346282592,\n"
                + "                    \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\">Hello World</div>\",\n"
                + "                    \"data\": \"{\\\"entityIdentifier\\\":{\\\"type\\\":\\\"org.symphonyoss.fin.security\\\",\\\"version\\\":\\\"0.1\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.isin\\\",\\\"value\\\":\\\"US0378\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.cusip\\\",\\\"value\\\":\\\"037\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.openfigi\\\",\\\"value\\\":\\\"BBG000\\\"}]}}\",\n"
                + "                    \"user\": {\n"
                + "                        \"userId\": 1456852,\n"
                + "                        \"displayName\": \"User 1\",\n"
                + "                        \"email\": \"user1@ntest.com\",\n"
                + "                        \"username\": \"user1\"\n"
                + "                    },\n"
                + "                    \"stream\": {\n"
                + "                        \"streamId\": \"wTmSDJSNPXgB\",\n"
                + "                        \"streamType\": \"ROOM\"\n"
                + "                    },\n"
                + "                    \"externalRecipients\": false,\n"
                + "                    \"userAgent\": \"Agent-2.2.8-Linux-4.9.77-31.58.amzn1.x86_64\",\n"
                + "                    \"originalFormat\": \"com.symphony.messageml.v2\"\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "]\n"
                + "}")));

    try {

      assertNotNull(datafeedClient);

      final List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed("1");
      assertNotNull(datafeedEvents);
      assertEquals(1, datafeedEvents.size());

      final DatafeedEvent event = datafeedEvents.get(0);
      assertNotNull(event);
      assertEquals("eventId", event.getId());
      assertEquals(1536346282592L, event.getTimestamp().longValue());
      assertEquals("MESSAGESENT", event.getType());

      final Initiator initiator = event.getInitiator();
      assertNotNull(initiator);
      final User user = initiator.getUser();
      assertNotNull(user);
      assertEquals(1456852L, user.getUserId().longValue());
      assertEquals("User 1", user.getDisplayName());
      assertEquals("user1@test.com", user.getEmail());
      assertEquals("user1", user.getUsername());

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
      assertEquals(1456852L, messageUser.getUserId().longValue());
      assertEquals("User 1", messageUser.getDisplayName());
      assertEquals("user1@ntest.com", messageUser.getEmail());
      assertEquals("user1", messageUser.getUsername());

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

  @Ignore
  @Test(expected = APIClientErrorException.class)
  public void readDatafeedFailure400() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedEvent> events = datafeedClient.readDatafeed("1");
  }

  @Ignore
  @Test(expected = SymClientException.class)
  public void readDatafeedFailure401() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedEvent> events = datafeedClient.readDatafeed("1");
  }

  @Ignore
  @Test(expected = ForbiddenException.class)
  public void readDatafeedFailure403() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{"
                + "\"code\": 403,"
                + "\"message\": \"The user lacks the required entitlement to perform this operation\"}")));

    assertNotNull(datafeedClient);

    final List<DatafeedEvent> events = datafeedClient.readDatafeed("1");
  }

  @Ignore
  @Test(expected = ServerErrorException.class)
  public void readDatafeedFailure500() {
    stubFor(get(urlEqualTo(AgentConstants.READDATAFEED.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedEvent> events = datafeedClient.readDatafeed("1");
  }

  @Ignore
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
  // End readDatafeed

  // listDatafeedId
    @Test
    public void listDatafeedIdsSuccess() throws IOException {
        stubGet(AgentConstants.LISTDATAFEEDV2,
                readResourceContent("/response_content/datafeedv2/list_datafeedv2.json"));

      try {

        assertNotNull(datafeedClient);

        final List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
        assertNotNull(datafeedIds);

        assertEquals(3, datafeedIds.size());

        final List<String> ids = new ArrayList(Arrays.asList(
                                      "2c2e8bb339c5da5711b55e32ba7c4687_f",
                                      "4dd10564ef289e053cc59b2092080c3b_f",
                                      "83b69942b56288a14d8625ca2c85f264_f"));

        final  List<Long> createdAts = new ArrayList(Arrays.asList(
                                            1536346282592L,
                                            1536346282592L,
                                            1536346282592L));

        for(int i = 0; i < 3; i++) {

          assertEquals(ids.get(i), datafeedIds.get(i).getId());
          assertEquals(createdAts.get(i).longValue(), datafeedIds.get(i).getCreatedAt().longValue());
        }
      } catch (SymClientException e) {
        fail();
      }
    }

  @Test(expected = APIClientErrorException.class)
  public void listDatafeedIdsFailure400() {
    stubFor(get(urlEqualTo(AgentConstants.LISTDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
  }

  @Test(expected = SymClientException.class)
  public void listDatafeedIdsFailure401() {
    stubFor(get(urlEqualTo(AgentConstants.LISTDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
  }

  @Test(expected = ServerErrorException.class)
  public void listDatafeedIdsFailure500() {
    stubFor(get(urlEqualTo(AgentConstants.LISTDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    final List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
  }
    // End listDatafeedId

  // deleteDatafeed
  @Test
    public void deleteDatafeedSuccess() {
      stubFor(delete(urlEqualTo(AgentConstants.DELETEDATAFEEDV2.replace("{id}", "1")))
          .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
          .willReturn(aResponse()
              .withStatus(204)
              .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
              .withBody("{"
                  + "\"code\": 204,"
                  + "\"message\": \"Datafeed deleted with success.\""
                  + "}")));

      assertNotNull(datafeedClient);

      datafeedClient.deleteDatafeed("1");

      assertTrue(true);
    }

  @Test(expected = APIClientErrorException.class)
  public void deleteDatafeedFailure400() {
    stubFor(delete(urlEqualTo(AgentConstants.DELETEDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    datafeedClient.deleteDatafeed("1");

    assertTrue(true);
  }

  @Test(expected = SymClientException.class)
  public void deleteDatafeedFailure401() {
    stubFor(delete(urlEqualTo(AgentConstants.DELETEDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    datafeedClient.deleteDatafeed("1");

    assertTrue(true);
  }

  @Test(expected = ServerErrorException.class)
  public void deleteDatafeedFailure500() {
    stubFor(delete(urlEqualTo(AgentConstants.DELETEDATAFEEDV2.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(datafeedClient);

    datafeedClient.deleteDatafeed("1");

    assertTrue(true);
  }
  // End deleteDatafeed

  // getAckId
    @Test
    public void getAckIdSuccess() throws IOException {
      stubPost(AgentConstants.READDATAFEEDV2.replace("{id}", "21449143d35a86461e254d28697214b4_f"),
          readResourceContent("/response_content/datafeedv2/read_datafeedv2_old.json"), 200);

      try {

        assertNotNull(datafeedClient);

        final List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed("21449143d35a86461e254d28697214b4_f");
        assertNotNull(datafeedEvents);

        final String ackId = datafeedClient.getAckId();
        assertEquals("ack_id_string", ackId);

      } catch (SymClientException e) {
        fail();
      }
    }
    // End getAckId
}
