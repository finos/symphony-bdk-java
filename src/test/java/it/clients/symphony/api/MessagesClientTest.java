package it.clients.symphony.api;

import clients.symphony.api.MessagesClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.DataLossPreventionException;
import it.commons.BotTest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import model.*;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class MessagesClientTest extends BotTest {
  private MessagesClient messagesClient;

  @Before
  public void initClient() {
    messagesClient = new MessagesClient(symBotClient);
  }

  @Test
  public void getMessageByIdSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.GETMESSAGEBYID.replace("{mid}", "mock-message_id")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\n" +
                "  \"messageId\": \"mockMessageId\",\n" +
                "  \"timestamp\": 1590130725008,\n" +
                "  \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\" class=\\\"wysiwyg\\\"><p>Hello</p></div>\",\n" +
                "  \"data\": \"{}\",\n" +
                "  \"user\": {\n" +
                "    \"userId\": 12345678901234,\n" +
                "    \"firstName\": \"Mock\",\n" +
                "    \"lastName\": \"User\",\n" +
                "    \"displayName\": \"Mock User\",\n" +
                "    \"email\": \"mock.user@symphony.com\"\n" +
                "  },\n" +
                "  \"stream\": {\n" +
                "    \"streamId\": \"mock_stream_id\",\n" +
                "    \"streamType\": \"IM\"\n" +
                "  },\n" +
                "  \"userAgent\": \"DESKTOP-40.0.0-11751-Windows-10-Chrome-81.0.4044.138\",\n" +
                "  \"originalFormat\": \"com.symphony.messageml.v2\",\n" +
                "  \"disclaimer\": \"This is mock disclaimer\",\n" +
                "  \"sid\": \"mock_sid\"\n" +
                "}")));

    InboundMessage message = messagesClient.getMessageById("mock+message/id");

    assertNotNull(message);
    assertEquals("mockMessageId", message.getMessageId());
    assertEquals("This is mock disclaimer", message.getDisclaimer());
    assertEquals("mock_sid", message.getSid());
  }

  @Test
  public void getMessagesFromStreamSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.GETMESSAGES.replace("{sid}", "1").concat("?since=1461808167175")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("since", equalTo("1461808167175"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "    {\r\n" +
                "        \"messageId\": \"ryoQ1\",\r\n" +
                "        \"timestamp\": 1534891105293,\r\n" +
                "        \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\">Hello World</div>\",\r\n" +
                "        \"data\": \"{\\\"entityIdentifier\\\":{\\\"type\\\":\\\"org.symphonyoss.fin.security\\\",\\\"version\\\":\\\"0.1\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.isin\\\",\\\"value\\\":\\\"US0378\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.cusip\\\",\\\"value\\\":\\\"037\\\"},{\\\"type\\\":\\\"org.symphonyoss.fin.security.id.openfigi\\\",\\\"value\\\":\\\"BBG000\\\"}]}}\",\r\n" +
                "        \"attachments\": [\r\n" +
                "            {\r\n" +
                "                \"id\": \"internal_14568529\",\r\n" +
                "                \"name\": \"Test2.bmp\",\r\n" +
                "                \"size\": 66,\r\n" +
                "                \"images\": []\r\n" +
                "            }\r\n" +
                "        ],\r\n" +
                "        \"user\": {\r\n" +
                "            \"userId\": 14568529068038,\r\n" +
                "            \"displayName\": \"Local Bot01\",\r\n" +
                "            \"email\": \"bot.user1@test3.symphony.com\",\r\n" +
                "            \"username\": \"bot.user1\"\r\n" +
                "        },\r\n" +
                "        \"stream\": {\r\n" +
                "            \"streamId\": \"-aSoi\",\r\n" +
                "            \"streamType\": \"POST\"\r\n" +
                "        },\r\n" +
                "        \"userAgent\": \"Agent-2.2.5-Linux-4.9.77-31.58.amzn1.x86_64\",\r\n" +
                "        \"originalFormat\": \"com.symphony.messageml.v2\"\r\n" +
                "    }\r\n" +
                "]")));

    List<InboundMessage> messages = messagesClient.getMessagesFromStream("1", 1461808167175L, 0, 0);

    assertNotNull(messages);
    assertEquals("ryoQ1", messages.get(0).getMessageId());
  }

  @Test
  public void getAttachmentSuccess() {
    stubFor(get(urlEqualTo(AgentConstants.GETATTACHMENT.replace("{sid}", "1").concat("?fileId=hUX1urTx5&messageId=urTxhUX17")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .withQueryParam("fileId", equalTo("hUX1urTx5"))
        .withQueryParam("messageId", equalTo("urTxhUX17"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("aGVsbG8gd29ybGQ=")));

    byte[] attachment = messagesClient.getAttachment("1", "hUX1urTx5", "urTxhUX17");

    assertNotNull(attachment);
  }

  @Test
  public void sendMessageSuccess() {
      String message = "hello";
      stubFor(
          post(urlEqualTo(AgentConstants.CREATEMESSAGE.replace("{sid}", "1")))
              .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                      .withBody("{\n" +
                          "    \"messageId\": \"1\",\n" +
                          "    \"timestamp\": 1579588917149,\n" +
                          "    \"message\": \"<div data-format=\\\"PresentationML\\\" data-version=\\\"2.0\\\">" + message + "</div>\"," +
                          "    \"user\": {\n" +
                          "        \"userId\": 1,\n" +
                          "        \"displayName\": \"Bot\",\n" +
                          "        \"email\": \"bot@symphony.com\",\n" +
                          "        \"username\": \"bot\"\n" +
                          "    },\n" +
                          "    \"stream\": {\n" +
                          "        \"streamId\": \"1\",\n" +
                          "        \"streamType\": \"IM\"\n" +
                          "    },\n" +
                          "    \"originalFormat\": \"com.symphony.messageml.v2\"\n" +
                          "}")
              )
      );

      OutboundMessage outboundMessage = new OutboundMessage(message);
      InboundMessage inboundMessage = messagesClient.sendMessage("1", outboundMessage);

      assertEquals(message, inboundMessage.getMessageText());
  }

    @Test(expected = DataLossPreventionException.class)
    public void sendMessageBlockedByDLP() {
        String message = "bake";
        stubFor(
            post(urlEqualTo(AgentConstants.CREATEMESSAGE.replace("{sid}", "1")))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(451)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody("{\n" +
                            "    \"code\": 451,\n" +
                            "    \"message\": \"Compliance issues found in message\",\n" +
                            "    \"details\": [\n" +
                            "        {\n" +
                            "            \"detectionIn\": \"TEXT\",\n" +
                            "            \"policyResults\": [\n" +
                            "                {\n" +
                            "                    \"name\": \"Cooking Policy\",\n" +
                            "                    \"status\": \"WARN\",\n" +
                            "                    \"scopes\": [\n" +
                            "                        \"INTERNAL\",\n" +
                            "                        \"EXTERNAL\"\n" +
                            "                    ],\n" +
                            "                    \"reasons\": [\n" +
                            "                        \"bake\"\n" +
                            "                    ]\n" +
                            "                }\n" +
                            "            ]\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}")
                )
        );

        messagesClient.sendMessage("1", new OutboundMessage(message));
    }

  @Test
  public void getMessageStatusSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETMESSAGESTATUS.replace("{mid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{    \r\n" +
                "  \"author\": {\r\n" +
                "      \"userId\": \"7078106103901\",\r\n" +
                "      \"firstName\": \"Gustav\",\r\n" +
                "      \"lastName\": \"Mahler\",\r\n" +
                "      \"displayName\": \"Gustav Mahler\",\r\n" +
                "      \"email\": \"gustav.mahler@music.org\",\r\n" +
                "      \"userName\": \"gmahler\",\r\n" +
                "      \"timestamp\": \"1531968487845\"\r\n" +
                "    },\r\n" +
                "  \"read\": [\r\n" +
                "    {\r\n" +
                "      \"userId\": \"7078106103901\", \r\n" +
                "      \"firsName\": \"Gustav\", \r\n" +
                "      \"lastName\": \"Mahler\", \r\n" +
                "      \"displayName\": \"Gustav Mahler\", \r\n" +
                "      \"email\": \"gustav.mahler@music.org\", \r\n" +
                "      \"userName\": \"gmahler\", \r\n" +
                "      \"timestamp\": \"1489769156271\"\r\n" +
                "    },\r\n" +
                "    {\r\n" +
                "      \"userId\": \"7078106103902\", \r\n" +
                "      \"firsName\": \"Hildegard\", \r\n" +
                "      \"lastName\": \"Bingen\", \r\n" +
                "      \"displayName\": \"Hildegard Bingen\", \r\n" +
                "      \"email\": \"hildegard.bingen@music.org\", \r\n" +
                "      \"userName\": \"hbingen\", \r\n" +
                "      \"timestamp\": \"1487352923000\"\r\n" +
                "    }\r\n" +
                "  ],\r\n" +
                "  \"delivered\": [\r\n" +
                "    {\r\n" +
                "      \"userId\": \"7078106103903\", \r\n" +
                "      \"firsName\": \"Franz\", \r\n" +
                "      \"lastName\": \"Liszt\", \r\n" +
                "      \"displayName\": \"Franz Liszt\", \r\n" +
                "      \"email\": \"franz.liszt@music.org\", \r\n" +
                "      \"userName\": \"fliszt\", \r\n" +
                "      \"timestamp\": \"1484674523000\"\r\n" +
                "    }\r\n" +
                "  ],\r\n" +
                "  \"sent\": [\r\n" +
                "    {\r\n" +
                "      \"userId\": \"7078106103904\", \r\n" +
                "      \"firsName\": \"Benjamin\", \r\n" +
                "      \"lastName\": \"Britten\", \r\n" +
                "      \"displayName\": \"Benjamin Britten\", \r\n" +
                "      \"email\": \"benjamin.britten@music.org\", \r\n" +
                "      \"userName\": \"gmahler\", \r\n" +
                "      \"timestamp\": \"1484156123000\"\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}")));

    MessageStatus status = messagesClient.getMessageStatus("1");

    assertNotNull(status);
    assertNotNull(status.getSent().get(0));
    assertEquals("7078106103904", status.getSent().get(0).getUserId());
  }

  @Test
  public void messageSearchSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.SEARCHMESSAGES))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"messageId\": \"-sfAvIPTTmyrpORkBuvL_3___qulZoKedA\",\r\n" +
                "    \"timestamp\": 1461808889185,\r\n" +
                "    \"version\" : \"2.0\",\r\n" +
                "    \"message\": \"<div class='presentationML-V2'>Just purchased the most recent recording of the <span class=\\\"hashTag\\\" data-entity-id=\\\"hash1\\\">#newWorld</span> Symphony</div>\",\r\n" +
                "    \"data\" : \"{\\\"hash1\\\":{\\\"type\\\":\\\"org.symphonyoss.taxonomy\\\",\\\"version\\\":\\\"1.0\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.taxonomy.hashtag\\\",\\\"value\\\":\\\"newWorld\\\"}]}}\",\r\n" +
                "    \"attachments\": [],\r\n" +
                "    \"user\": {\r\n" +
                "      \"userId\": 8933531975687,\r\n" +
                "      \"displayName\": \"Antonin Dvorak\",\r\n" +
                "      \"email\": \"antonin.dvorak@music.org\",\r\n" +
                "      \"username\": \"advorak\"\r\n" +
                "    },\r\n" +
                "    \"stream\": {\r\n" +
                "      \"streamId\": \"YQ_Q3ml8vMp98so2WRK_W3___qTUhq1_dA\",\r\n" +
                "      \"streamType\": \"ROOM\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"messageId\": \"-sfAvIPTTmyrpORkBuvL_3___qulZoKedB\",\r\n" +
                "    \"timestamp\": 1461808889195,\r\n" +
                "    \"version\" : \"2.0\",\r\n" +
                "    \"message\": \"<div class='presentationML-V2'>Listening to the <span class=\\\"hashTag\\\" data-entity-id=\\\"hash1\\\">#newWorld</span> Symphony</div>\",\r\n" +
                "    \"data\" : \"{\\\"hash1\\\":{\\\"type\\\":\\\"org.symphonyoss.taxonomy\\\",\\\"version\\\":\\\"1.0\\\",\\\"id\\\":[{\\\"type\\\":\\\"org.symphonyoss.taxonomy.hashtag\\\",\\\"value\\\":\\\"newWorld\\\"}]}}\",\r\n" +
                "    \"attachments\": [],\r\n" +
                "    \"user\": {\r\n" +
                "      \"userId\": 8933531975687,\r\n" +
                "      \"displayName\": \"Hildegard Bingen\",\r\n" +
                "      \"email\": \"hildegard.bingen@music.org\",\r\n" +
                "      \"username\": \"hbingen\"\r\n" +
                "    },\r\n" +
                "    \"stream\": {\r\n" +
                "      \"streamId\": \"YQ_Q3ml8vMp98so2WRK_W3___qTUhq1_dB\",\r\n" +
                "      \"streamType\": \"ROOM\"\r\n" +
                "    }\r\n" +
                "  }\r\n" +
                "]")));

    Map<String, String> query = Stream.of(new String[][] {
      { "hashtag", "newWorld" }
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    try {
      InboundMessageList messages = messagesClient.messageSearch(query, 0, 0, false);
      assertNotNull(messages);
      assertEquals(2, messages.size());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void shareContentSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.SHARE.replace("{sid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": \"HsaTBf7ClJRWvzNWaCp_4H___qlrh4WVdA\",\r\n" +
                "  \"timestamp\": \"1471369738860\",\r\n" +
                "  \"v2messageType\": \"V2Message\",\r\n" +
                "  \"streamId\": \"7w68A8sAG_qv1GwVc9ODzX___ql_RJ6zdA\",\r\n" +
                "  \"message\": \"{\\\"type\\\":\\\"com.symphony.sharing.article\\\",\\\"content\\\":{\\\"articleId\\\":\\\"tsla\\\",\\\"title\\\":\\\"The Secret's Out: Tesla Enters China and Is Winning\\\",\\\"subTitle\\\":null,\\\"message\\\":null,\\\"publisher\\\":\\\"Capital Market Laboratories\\\",\\\"thumbnailUrl\\\":\\\"http://www.cmlviz.com/cmld3b/images/tesla-supercharger-stop.jpg\\\",\\\"author\\\":\\\"OPHIRGOTTLIEB\\\",\\\"articleUrl\\\":\\\"http://ophirgottlieb.tumblr.com/post/146623530819/the-secrets-out-tesla-enters-china-and-is\\\",\\\"summary\\\":\\\"Tesla Motors Inc. (NASDAQ:TSLA) has a CEO more famous than the firm itself, perhaps. Elon Musk has made some bold predictions, first stating that the firm would grow sales from 50,000 units in 2015 to 500,000 by 2020 powered by the less expensive Model 3 and the massive manufacturing capability of the Gigafactory.\\\",\\\"appId\\\":\\\"ticker\\\",\\\"appName\\\":\\\"Market Data Demo\\\",\\\"appIconUrl\\\":\\\"https://apps-dev.symphony.com/ticker/assets/images/logo.png\\\"}}\",\r\n" +
                "  \"fromUserId\": 7696581430532\r\n" +
                "}")));

    OutboundShare outboundShare = new OutboundShare();
    outboundShare.setArticleId("tsla");
    outboundShare.setTitle("The Secret'\"'\"'s Out: Tesla Enters China and Is Winning");

    InboundShare inboundShare = messagesClient.shareContent("1", outboundShare);
    assertNotNull(inboundShare);
    assertEquals("HsaTBf7ClJRWvzNWaCp_4H___qlrh4WVdA", inboundShare.getId());
  }

}
