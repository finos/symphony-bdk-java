package it.clients.symphony.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import org.junit.Before;
import org.junit.Test;
import clients.symphony.api.AdminClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
import model.AdminNewUser;
import model.AdminStreamFilter;
import model.AdminUserAttributes;
import model.AdminUserInfo;
import model.ApplicationEntitlement;
import model.Avatar;
import model.FeatureEntitlement;
import model.InboundImportMessageList;
import model.OutboundImportMessage;
import model.OutboundImportMessageList;
import model.SuppressionResult;
import model.events.AdminStreamInfoList;

public class AdminClientTest extends BotTest {
  private AdminClient adminClient;

  @Before
  public void initClient() {
    adminClient = new AdminClient(symBotClient);
  }

  @Test
  public void importMessagesSuccess() {
    stubFor(post(urlEqualTo(AgentConstants.MESSAGEIMPORT))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"messageId\": \"FjSY1y3L\",  \r\n" +
                "    \"originatingSystemId\": \"AGENT_SDK\",\r\n" +
                "    \"originalMessageId\": \"M2\"\r\n" +
                "  }\r\n" +
                "]")));

    OutboundImportMessageList outMessageList = new OutboundImportMessageList();
    OutboundImportMessage message = new OutboundImportMessage();
    message.setMessage("<messageML>Imported message</messageML>");
    message.setIntendedMessageTimestamp(1433045622000L);
    message.setIntendedMessageFromUserId(7215545057281L);
    message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");
    outMessageList.add(message);

    InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
    assertNotNull(inMessageList);
    assertEquals(1,  inMessageList.size());
    assertEquals("FjSY1y3L", inMessageList.get(0).getMessageId());
  }

  @Test
  public void suppressMessageSuccess() {
    stubFor(post(urlEqualTo(PodConstants.MESSAGESUPPRESS.replace("{id}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"messageId\": \"1\",\r\n" +
                "  \"suppressed\": true,\r\n" +
                "  \"suppressionDate\": 1461565603191\r\n" +
                "}")));

    SuppressionResult result = adminClient.suppressMessage("1");
    assertNotNull(result);
    assertEquals("1",  result.getMessageId());
  }

  @Test
  public void listEnterpriseStreamsSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ENTERPRISESTREAMS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"count\": 4,\r\n" +
                "  \"skip\": 0,\r\n" +
                "  \"limit\": 50,\r\n" +
                "  \"filter\": {\r\n" +
                "    \"streamTypes\": [\"ROOM\"],\r\n" +
                "    \"scope\": \"EXTERNAL\",\r\n" +
                "    \"origin\": \"EXTERNAL\",\r\n" +
                "    \"status\": \"ACTIVE\",\r\n" +
                "    \"privacy\": \"PRIVATE\",\r\n" +
                "    \"startDate\": 1481575056047,\r\n" +
                "    \"endDate\": 1483038089833\r\n" +
                "  },\r\n" +
                "  \"streams\": [{\r\n" +
                "      \"id\": \"Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA\",\r\n" +
                "      \"isExternal\": false,\r\n" +
                "      \"isActive\": true,\r\n" +
                "      \"isPublic\": false,\r\n" +
                "      \"type\": \"ROOM\",\r\n" +
                "      \"attributes\": {\r\n" +
                "        \"roomName\": \"Active Internal Private Room\",\r\n" +
                "        \"roomDescription\": \"Active Internal Private Room\",\r\n" +
                "        \"createdByUserId\": 8933531975689,\r\n" +
                "        \"createdDate\": 1481575056047,\r\n" +
                "        \"lastModifiedDate\": 1481575056047,\r\n" +
                "        \"originCompany\": \"Symphony\",\r\n" +
                "        \"originCompanyId\": 130,\r\n" +
                "        \"membersCount\": 1,\r\n" +
                "        \"lastMessageDate\" :1516699467959\r\n" +
                "      }\r\n" +
                "    }, \r\n" +
                "    {\r\n" +
                "      \"id\": \"_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA\",\r\n" +
                "      \"isExternal\": true,\r\n" +
                "      \"isActive\": false,\r\n" +
                "      \"isPublic\": false,\r\n" +
                "      \"type\": \"ROOM\",\r\n" +
                "      \"attributes\": {\r\n" +
                "        \"roomName\": \"Inactive External Room\",\r\n" +
                "        \"roomDescription\": \"Inactive External Room\",\r\n" +
                "        \"createdByUserId\": 8933531975686,\r\n" +
                "        \"createdDate\": 1481876438194,\r\n" +
                "        \"lastModifiedDate\": 1481876438194,\r\n" +
                "        \"originCompany\": \"Symphony\",\r\n" +
                "        \"originCompanyId\": 130,\r\n" +
                "        \"membersCount\": 2,\r\n" +
                "        \"lastMessageDate\" :1516699467959\r\n" +
                "      }\r\n" +
                "    },\r\n" +
                "    {\r\n" +
                "      \"id\": \"fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA\",\r\n" +
                "      \"isExternal\": false,\r\n" +
                "      \"isActive\": true,\r\n" +
                "      \"type\": \"IM\",\r\n" +
                "      \"attributes\": {\r\n" +
                "        \"members\": [\r\n" +
                "          8933531975686,\r\n" +
                "          8933531975689\r\n" +
                "        ],\r\n" +
                "        \"createdByUserId\": 8933531975689,\r\n" +
                "        \"createdDate\": 1482302390238,\r\n" +
                "        \"lastModifiedDate\": 1482302390238,\r\n" +
                "        \"originCompany\": \"Symphony\",\r\n" +
                "        \"originCompanyId\": 130,\r\n" +
                "        \"membersCount\": 2,\r\n" +
                "        \"lastMessageDate\" :1516699467959\r\n" +
                "      }\r\n" +
                "    },\r\n" +
                "    {\r\n" +
                "      \"id\": \"k19u9c3GSE_iq0VHDKe1on___qa0Cp2WdA\",\r\n" +
                "      \"isExternal\": false,\r\n" +
                "      \"isActive\": true,\r\n" +
                "      \"type\": \"MIM\",\r\n" +
                "      \"attributes\": {\r\n" +
                "        \"members\": [\r\n" +
                "          8933531975688,\r\n" +
                "          8933531975689,\r\n" +
                "          8933531975717\r\n" +
                "        ],\r\n" +
                "        \"createdByUserId\": 8933531975688,\r\n" +
                "        \"createdDate\": 1483038089833,\r\n" +
                "        \"lastModifiedDate\": 1483038089833,\r\n" +
                "        \"originCompany\": \"Symphony\",\r\n" +
                "        \"originCompanyId\": 130,\r\n" +
                "        \"membersCount\": 3,\r\n" +
                "        \"lastMessageDate\" :1516699467959 \r\n" +
                "      }\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}")));

    AdminStreamFilter filter = new AdminStreamFilter();
    AdminStreamInfoList result = adminClient.listEnterpriseStreams(filter, 0, 0);

    assertNotNull(result);
    assertEquals(4,  result.getCount());
  }

  @Test
  public void createIMSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEIM))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"id\": \"xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA\"\r\n" +
                "}")));

    List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
    String streamId = adminClient.createIM(userIdList);

    assertNotNull(streamId);
    assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);
  }

  @Test
  public void getUserSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userAttributes\": {\r\n" +
                "    \"emailAddress\": \"bot.user@email.com\",\r\n" +
                "    \"userName\": \"bot.user\",\r\n" +
                "    \"displayName\": \"bot.user\",\r\n" +
                "    \"accountType\": \"SYSTEM\"\r\n" +
                "  },\r\n" +
                "  \"userSystemInfo\": {\r\n" +
                "    \"id\": 9826885173289,\r\n" +
                "    \"status\": \"ENABLED\",\r\n" +
                "    \"createdDate\": 1499373423000,\r\n" +
                "    \"createdBy\": \"9826885173255\",\r\n" +
                "    \"lastUpdatedDate\": 1499373423803\r\n" +
                "  },\r\n" +
                "  \"roles\": [\r\n" +
                "    \"USER_PROVISIONING\",\r\n" +
                "    \"CONTENT_MANAGEMENT\",\r\n" +
                "    \"INDIVIDUAL\"\r\n" +
                "  ],\r\n" +
                "  \"features\": [],\r\n" +
                "  \"apps\": [],\r\n" +
                "  \"groups\": [],\r\n" +
                "  \"disclaimers\": [],\r\n" +
                "  \"avatar\": {\r\n" +
                "    \"size\": \"small\",\r\n" +
                "    \"url\": \"../avatars/static/50/default.png\"\r\n" +
                "  }\r\n" +
                "}")));

    try {
      AdminUserInfo adminUser = adminClient.getUser(1L);

      assertNotNull(adminUser);
      assertEquals(9826885173289L, adminUser.getUserSystemInfo().getId().longValue());
    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test
  public void listUsersSuccess() {
    stubFor(get(urlEqualTo(PodConstants.LISTUSERSADMIN))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "    {\r\n" +
                "        \"userAttributes\": {\r\n" +
                "            \"emailAddress\": \"agentservice@acme.com\",\r\n" +
                "            \"firstName\": \"Agent Service\",\r\n" +
                "            \"userName\": \"agentservice\",\r\n" +
                "            \"displayName\": \"Agent Service\",\r\n" +
                "            \"accountType\": \"SYSTEM\"\r\n" +
                "        },\r\n" +
                "        \"userSystemInfo\": {\r\n" +
                "            \"id\": 9826885173252,\r\n" +
                "            \"status\": \"ENABLED\",\r\n" +
                "            \"createdDate\": 1498665229000,\r\n" +
                "            \"lastUpdatedDate\": 1498665229886,\r\n" +
                "            \"lastLoginDate\": 1504899004993\r\n" +
                "        },\r\n" +
                "        \"roles\": [\r\n" +
                "            \"USER_PROVISIONING\",\r\n" +
                "            \"CONTENT_MANAGEMENT\",\r\n" +
                "            \"L2_SUPPORT\",\r\n" +
                "            \"INDIVIDUAL\",\r\n" +
                "            \"AGENT\",\r\n" +
                "            \"SUPER_ADMINISTRATOR\",\r\n" +
                "            \"SUPER_COMPLIANCE_OFFICER\"\r\n" +
                "        ]\r\n" +
                "    },\r\n" +
                "    {\r\n" +
                "        \"userAttributes\": {\r\n" +
                "            \"emailAddress\": \"bot.user1@acme.yaml.com\",\r\n" +
                "            \"firstName\": \"Nexus1\",\r\n" +
                "            \"lastName\": \"Bot01\",\r\n" +
                "            \"userName\": \"bot.user1\",\r\n" +
                "            \"displayName\": \"ACME Bot01\",\r\n" +
                "            \"accountType\": \"NORMAL\",\r\n" +
                "            \"assetClasses\": [],\r\n" +
                "            \"industries\": []\r\n" +
                "        },\r\n" +
                "        \"userSystemInfo\": {\r\n" +
                "            \"id\": 9826885173258,\r\n" +
                "            \"status\": \"ENABLED\",\r\n" +
                "            \"createdDate\": 1499347606000,\r\n" +
                "            \"createdBy\": \"9826885173252\",\r\n" +
                "            \"lastUpdatedDate\": 1499348554853,\r\n" +
                "            \"lastLoginDate\": 1504839044527\r\n" +
                "        },\r\n" +
                "        \"roles\": [\r\n" +
                "            \"USER_PROVISIONING\",\r\n" +
                "            \"CONTENT_EXPORT_SERVICE\",\r\n" +
                "            \"CONTENT_MANAGEMENT\",\r\n" +
                "            \"L2_SUPPORT\",\r\n" +
                "            \"INDIVIDUAL\",\r\n" +
                "            \"SUPER_ADMINISTRATOR\",\r\n" +
                "            \"SUPER_COMPLIANCE_OFFICER\"\r\n" +
                "        ]\r\n" +
                "    }\r\n" +
                "]")));

    List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);

    assertNotNull(adminUsers);
    assertEquals(2, adminUsers.size());
    assertEquals(9826885173258L, adminUsers.get(1).getUserSystemInfo().getId().longValue());

  }

  @Test
  public void createUserSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEUSER))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userAttributes\": {\r\n" +
                "    \"emailAddress\": \"janedoe@symphony.com\",\r\n" +
                "    \"firstName\": \"Jane\",\r\n" +
                "    \"lastName\": \"Doe\",\r\n" +
                "    \"userName\": \"janedoe\",\r\n" +
                "    \"displayName\": \"Jane Doe\",\r\n" +
                "    \"companyName\": \"Company Name\",\r\n" +
                "    \"department\": \"\",\r\n" +
                "    \"division\": \"\",\r\n" +
                "    \"title\": \"Sales\",\r\n" +
                "    \"twoFactorAuthPhone\": \"\",\r\n" +
                "    \"workPhoneNumber\": \"\",\r\n" +
                "    \"mobilePhoneNumber\": \"\",     \r\n" +
                "    \"accountType\": \"NORMAL\",\r\n" +
                "    \"location\": \"San Francisco\",\r\n" +
                "    \"jobFunction\": \"Sales\",\r\n" +
                "    \"assetClasses\": [\r\n" +
                "      \"Commodities\"\r\n" +
                "    ],\r\n" +
                "    \"industries\": [\r\n" +
                "      \"Basic Materials\"\r\n" +
                "    ],\r\n" +
                "    \"currentKey\": {\r\n" +
                "        \"key\": \"-----BEGIN PUBLIC KEY-----\\nMIICI==\\n-----END PUBLIC KEY-----\"\r\n" +
                "}\r\n" +
                "  },\r\n" +
                "  \"userSystemInfo\": {\r\n" +
                "    \"id\": 7215545078541,\r\n" +
                "    \"status\": \"ENABLED\",\r\n" +
                "    \"createdDate\": 1461509290000,\r\n" +
                "    \"createdBy\": \"7215545078229\",\r\n" +
                "    \"lastUpdatedDate\": 1461509290000\r\n" +
                "  },\r\n" +
                "  \"roles\": [\r\n" +
                "    \"INDIVIDUAL\"\r\n" +
                "  ]\r\n" +
                "}")));


    AdminUserInfo user = adminClient.createUser(new AdminNewUser());

    assertNotNull(user);
    assertEquals(7215545078541L, user.getUserSystemInfo().getId().longValue());
  }

  @Test
  public void updateUserSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEUSER.replace("{uid}", "7215545078541")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userAttributes\": {\r\n" +
                "    \"emailAddress\": \"janedoe@symphony.com\",\r\n" +
                "    \"firstName\": \"Jane\",\r\n" +
                "    \"lastName\": \"Doe\",\r\n" +
                "    \"userName\": \"janedoe\",\r\n" +
                "    \"displayName\": \"Jane Doe\",\r\n" +
                "    \"companyName\": \"Company Name\",\r\n" +
                "    \"department\": \"\",\r\n" +
                "    \"division\": \"\",\r\n" +
                "    \"title\": \"Sales\",\r\n" +
                "    \"twoFactorAuthPhone\": \"\",\r\n" +
                "    \"workPhoneNumber\": \"\",\r\n" +
                "    \"mobilePhoneNumber\": \"\",     \r\n" +
                "    \"accountType\": \"NORMAL\",\r\n" +
                "    \"location\": \"San Francisco\",\r\n" +
                "    \"jobFunction\": \"Sales\",\r\n" +
                "    \"assetClasses\": [\r\n" +
                "      \"Commodities\"\r\n" +
                "    ],\r\n" +
                "    \"industries\": [\r\n" +
                "      \"Basic Materials\"\r\n" +
                "    ],\r\n" +
                "    \"currentKey\": {\r\n" +
                "        \"key\": \"-----BEGIN PUBLIC KEY-----\\nMIICI==\\n-----END PUBLIC KEY-----\"\r\n" +
                "}\r\n" +
                "  },\r\n" +
                "  \"userSystemInfo\": {\r\n" +
                "    \"id\": 7215545078541,\r\n" +
                "    \"status\": \"ENABLED\",\r\n" +
                "    \"createdDate\": 1461509290000,\r\n" +
                "    \"createdBy\": \"7215545078229\",\r\n" +
                "    \"lastUpdatedDate\": 1461509290000\r\n" +
                "  },\r\n" +
                "  \"roles\": [\r\n" +
                "    \"INDIVIDUAL\"\r\n" +
                "  ]\r\n" +
                "}")));


    AdminUserInfo user = adminClient.updateUser(7215545078541L, new AdminUserAttributes());

    assertNotNull(user);
    assertEquals(7215545078541L, user.getUserSystemInfo().getId().longValue());
  }

  @Test
  public void getAvatarSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETAVATARADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"size\": \"600\",\r\n" +
                "    \"url\": \"../avatars/acme/600/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"size\": \"150\",\r\n" +
                "    \"url\": \"../avatars/acme/150/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"size\": \"orig\",\r\n" +
                "    \"url\": \"../avatars/acme/102/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"size\": \"500\",\r\n" +
                "    \"url\": \"../avatars/acme/500/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"size\": \"50\",\r\n" +
                "    \"url\": \"../avatars/acme/50/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png\"\r\n" +
                "  }\r\n" +
                "]")));

    List<Avatar> avatarList = adminClient.getAvatar(1L);

    assertNotNull(avatarList);
    assertEquals(5, avatarList.size());
  }

  @Test
  public void updateAvatarSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"OK\"\r\n" +
                "}")));

    try {
      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
    } catch (IOException io) {
      fail();
    }
  }

  @Test
  public void getUserStatusSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"status\": \"ENABLED\"\r\n" +
                "}")));

    String status = adminClient.getUserStatus(1L);

    assertNotNull(status);
    assertEquals("ENABLED", status);
  }

  @Test
  public void updateUserStatusSuccess() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"message\": \"OK\"\r\n" +
                "}")));

    adminClient.updateUserStatus(1L, "DISABLED");
  }

  @Test
  public void listPodFeaturesSuccess() {
    stubFor(get(urlEqualTo(PodConstants.PODFEATURESADMIN))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "    \"postReadEnabled\",\r\n" +
                "    \"postWriteEnabled\",\r\n" +
                "    \"delegatesEnabled\",\r\n" +
                "    \"isExternalIMEnabled\",\r\n" +
                "    \"canShareFilesExternally\",\r\n" +
                "    \"canCreatePublicRoom\",\r\n" +
                "    \"canUpdateAvatar\",\r\n" +
                "    \"isExternalRoomEnabled\",\r\n" +
                "    \"canCreatePushedSignals\",\r\n" +
                "    \"canUseCompactMode\",\r\n" +
                "    \"mustBeRecorded\",\r\n" +
                "    \"sendFilesEnabled\",\r\n" +
                "    \"canUseInternalAudio\",\r\n" +
                "    \"canUseInternalVideo\",\r\n" +
                "    \"canProjectInternalScreenShare\",\r\n" +
                "    \"canViewInternalScreenShare\",\r\n" +
                "    \"canCreateMultiLateralRoom\",\r\n" +
                "    \"canJoinMultiLateralRoom\",\r\n" +
                "    \"canUseFirehose\",\r\n" +
                "    \"canUseInternalAudioMobile\",\r\n" +
                "    \"canUseInternalVideoMobile\",\r\n" +
                "    \"canProjectInternalScreenShareMobile\",\r\n" +
                "    \"canViewInternalScreenShareMobile\",\r\n" +
                "    \"canManageSignalSubscription\"\r\n" +
                "]")));

    List<String> features = adminClient.listPodFeatures();

    assertNotNull(features);
    assertEquals(24, features.size());
  }

  @Test
  public void getUserFeaturesSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"canCreatePublicRoom\",\r\n" +
                "    \"enabled\": true\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"isExternalRoomEnabled\",\r\n" +
                "    \"enabled\": false\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"delegatesEnabled\",\r\n" +
                "    \"enabled\": true\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"isExternalIMEnabled\",\r\n" +
                "    \"enabled\": true\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"sendFilesEnabled\",\r\n" +
                "    \"enabled\": true\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"entitlment\": \"canUpdateAvatar\",\r\n" +
                "    \"enabled\": true\r\n" +
                "  }\r\n" +
                "]")));

    List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);

    assertNotNull(entitlements);
    assertEquals(6, entitlements.size());
    assertEquals("canCreatePublicRoom", entitlements.get(0).getEntitlment());
  }

  @Test
  public void updateUserFeaturesSuccess() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"format\": \"TEXT\",\r\n" +
                "  \"message\": \"OK\"\r\n" +
                "}")));

    adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());
  }

  @Test
  public void getUserApplicationEntitlementsSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"appId\": \"djApp\",\r\n" +
                "    \"appName\": \"Dow Jones\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": false\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"appId\": \"spcapiq\",\r\n" +
                "    \"appName\": \"S&P Capital IQ Data\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": false\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"appId\": \"selerity\",\r\n" +
                "    \"appName\": \"Selerity Context\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": true,\r\n" +
                "    \"products\": [\r\n" +
                "      {\r\n" +
                "        \"appId\": \"selerity\",\r\n" +
                "        \"name\": \"Standard\",\r\n" +
                "        \"subscribed\": true,\r\n" +
                "        \"type\": \"default\"\r\n" +
                "      },\r\n" +
                "      {\r\n" +
                "        \"appId\": \"selerity\",\r\n" +
                "        \"name\": \"Premium\",\r\n" +
                "        \"sku\": \"AcDccU53SsY\",\r\n" +
                "        \"subscribed\": false,\r\n" +
                "        \"type\": \"premium\"\r\n" +
                "      }\r\n" +
                "    ]\r\n" +
                "  }\r\n" +
                "]")));

    List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);

    assertNotNull(entitlements);
    assertEquals(3, entitlements.size());
    assertEquals("djApp", entitlements.get(0).getAppId());
  }

  @Test
  public void updateUserApplicationEntitlementsSuccess() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"appId\": \"djApp\",\r\n" +
                "    \"appName\": \"Dow Jones\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": false\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"appId\": \"spcapiq\",\r\n" +
                "    \"appName\": \"S&P Capital IQ Data\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": false\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"appId\": \"selerity\",\r\n" +
                "    \"appName\": \"Selerity Context\",\r\n" +
                "    \"listed\": true,\r\n" +
                "    \"install\": true,\r\n" +
                "    \"products\": [\r\n" +
                "      {\r\n" +
                "        \"appId\": \"selerity\",\r\n" +
                "        \"name\": \"Standard\",\r\n" +
                "        \"subscribed\": true,\r\n" +
                "        \"type\": \"default\"\r\n" +
                "      },\r\n" +
                "      {\r\n" +
                "        \"appId\": \"selerity\",\r\n" +
                "        \"name\": \"Premium\",\r\n" +
                "        \"sku\": \"AcDccU53SsY\",\r\n" +
                "        \"subscribed\": false,\r\n" +
                "        \"type\": \"premium\"\r\n" +
                "      }\r\n" +
                "    ]\r\n" +
                "  }\r\n" +
                "]")));

    List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
        1L, new ArrayList<ApplicationEntitlement>());

    assertNotNull(entitlements);
    assertEquals(3, entitlements.size());
    assertEquals("djApp", entitlements.get(0).getAppId());
  }

}
