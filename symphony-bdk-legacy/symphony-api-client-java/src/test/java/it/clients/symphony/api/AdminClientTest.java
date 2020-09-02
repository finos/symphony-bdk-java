package it.clients.symphony.api;

import clients.symphony.api.APIClient;
import clients.symphony.api.AdminClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.*;
import it.commons.BotTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import model.*;
import model.events.AdminStreamInfoList;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static net.bytebuddy.matcher.ElementMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class AdminClientTest extends BotTest {

  private  static  final Logger logger = LoggerFactory.getLogger(AdminClient.class);

  private AdminClient adminClient;

  @Before
  public void initClient() {
    adminClient = new AdminClient(symBotClient);
  }

  /////////// importMessages
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

    try {
      assertNotNull(adminClient);
      final OutboundImportMessageList outMessageList = new OutboundImportMessageList();
      final OutboundImportMessage message = new OutboundImportMessage();
      message.setMessage("<messageML>Imported message</messageML>");
      message.setData("Data message");
      message.setIntendedMessageTimestamp(1433045622000L);
      message.setIntendedMessageFromUserId(7215545057281L);
      message.setOriginatingSystemId("OSI");
      message.setOriginalMessageId("OMI");
      message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");

      assertNotNull(message);
      outMessageList.add(message);
      assertEquals(1, outMessageList.size());

      final InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
      assertNotNull(inMessageList);
      assertEquals(1,  inMessageList.size());

      assertEquals("FjSY1y3L", inMessageList.get(0).getMessageId());
      assertEquals("AGENT_SDK", inMessageList.get(0).getOriginatingSystemId());
      assertEquals("M2", inMessageList.get(0).getOriginalMessageId());

      assertEquals("<messageML>Imported message</messageML>", outMessageList.get(0).getMessage());
      assertEquals("Data message", outMessageList.get(0).getData());
      assertEquals(1433045622000L, outMessageList.get(0).getIntendedMessageTimestamp());
      assertEquals(7215545057281L, outMessageList.get(0).getIntendedMessageFromUserId());
      assertEquals("OSI", outMessageList.get(0).getOriginatingSystemId());
      assertEquals("OMI", outMessageList.get(0).getOriginalMessageId());
      assertEquals("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA", outMessageList.get(0).getStreamId());

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void importMessagesFailure400() {
    stubFor(post(urlEqualTo(AgentConstants.MESSAGEIMPORT))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

      assertNotNull(adminClient);

      final OutboundImportMessageList outMessageList = new OutboundImportMessageList();
      final OutboundImportMessage message = new OutboundImportMessage();
      message.setMessage("<messageML>Imported message</messageML>");
      message.setData("Data message");
      message.setIntendedMessageTimestamp(1433045622000L);
      message.setIntendedMessageFromUserId(7215545057281L);
      message.setOriginatingSystemId("OSI");
      message.setOriginalMessageId("OMI");
      message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");

      assertNotNull(message);
      outMessageList.add(message);
      assertEquals(1, outMessageList.size());

      final InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
  }

  @Test(expected = SymClientException.class)
  public void importMessagesFailure401() {
    stubFor(post(urlEqualTo(AgentConstants.MESSAGEIMPORT))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{\r\n" +
                            "\"code\": 401,\r\n" +
                            "\"message\": \"Invalid session\"\r\n" +
                            "}")));

    assertNotNull(adminClient);

    final OutboundImportMessageList outMessageList = new OutboundImportMessageList();
    final OutboundImportMessage message = new OutboundImportMessage();
    message.setMessage("<messageML>Imported message</messageML>");
    message.setData("Data message");
    message.setIntendedMessageTimestamp(1433045622000L);
    message.setIntendedMessageFromUserId(7215545057281L);
    message.setOriginatingSystemId("OSI");
    message.setOriginalMessageId("OMI");
    message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");

    assertNotNull(message);
    outMessageList.add(message);
    assertEquals(1, outMessageList.size());

    final InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
  }

  @Test(expected = ForbiddenException.class)
  public void importMessagesFailure403() {
    stubFor(post(urlEqualTo(AgentConstants.MESSAGEIMPORT))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{\r\n" +
                            "\"code\": 403,\r\n" +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"\r\n" +
                            "}")));

    assertNotNull(adminClient);

    final OutboundImportMessageList outMessageList = new OutboundImportMessageList();
    final OutboundImportMessage message = new OutboundImportMessage();
    message.setMessage("<messageML>Imported message</messageML>");
    message.setData("Data message");
    message.setIntendedMessageTimestamp(1433045622000L);
    message.setIntendedMessageFromUserId(7215545057281L);
    message.setOriginatingSystemId("OSI");
    message.setOriginalMessageId("OMI");
    message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");

    assertNotNull(message);
    outMessageList.add(message);
    assertEquals(1, outMessageList.size());

    final InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
  }

  @Test(expected = ServerErrorException.class)
  public void importMessagesFailure500() {
    stubFor(post(urlEqualTo(AgentConstants.MESSAGEIMPORT))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final OutboundImportMessageList outMessageList = new OutboundImportMessageList();
    final OutboundImportMessage message = new OutboundImportMessage();
    message.setMessage("<messageML>Imported message</messageML>");
    message.setData("Data message");
    message.setIntendedMessageTimestamp(1433045622000L);
    message.setIntendedMessageFromUserId(7215545057281L);
    message.setOriginatingSystemId("OSI");
    message.setOriginalMessageId("OMI");
    message.setStreamId("Z3oQRAZGTCNl5KjiUH2G1n___qr9lLT8dA");

    assertNotNull(message);
    outMessageList.add(message);
    assertEquals(1, outMessageList.size());

    final InboundImportMessageList inMessageList = adminClient.importMessages(outMessageList);
  }
  /////////// End importMessages

  ////////// suppressMessage
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

    try {
      assertNotNull(adminClient);
      final SuppressionResult result = adminClient.suppressMessage("1");
      assertNotNull(result);
      assertEquals("1",  result.getMessageId());
      assertEquals(true, result.isSuppressed());
      assertEquals(1461565603191L, result.getSuppressionDate());

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void suppressMessageFailure400() {
    stubFor(post(urlEqualTo(PodConstants.MESSAGESUPPRESS.replace("{id}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final SuppressionResult result = adminClient.suppressMessage("1");
  }

  @Test(expected = SymClientException.class)
  public void suppressMessageFailure401() {
    stubFor(post(urlEqualTo(PodConstants.MESSAGESUPPRESS.replace("{id}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\" : 401," +
                            "\"message\" : \"invalid session\"}")));

    assertNotNull(adminClient);
    final SuppressionResult result = adminClient.suppressMessage("1");
  }

  @Test(expected = ForbiddenException.class)
  public void suppressMessageFailure403() {
    stubFor(post(urlEqualTo(PodConstants.MESSAGESUPPRESS.replace("{id}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\" : 403," +
                            "\"message\" : \"The user lacks the required entitlement to perform this operation\"}")));

    assertNotNull(adminClient);
    final SuppressionResult result = adminClient.suppressMessage("1");
  }

  @Test(expected = ServerErrorException.class)
  public void suppressMessageFailure500() {
    stubFor(post(urlEqualTo(PodConstants.MESSAGESUPPRESS.replace("{id}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final SuppressionResult result = adminClient.suppressMessage("1");
  }
  ////////// End suppressMessage

  ////////// listEnterpriseStreams (no failure)
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
                "      \"external\": false,\r\n" +
                "      \"active\": true,\r\n" +
                "      \"public\": false,\r\n" +
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
                "      \"external\": true,\r\n" +
                "      \"active\": false,\r\n" +
                "      \"public\": false,\r\n" +
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
                "      \"external\": false,\r\n" +
                "      \"active\": true,\r\n" +
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
                "      \"external\": false,\r\n" +
                "      \"active\": true,\r\n" +
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

    try {
      assertNotNull(adminClient);
      final AdminStreamFilter filter = new AdminStreamFilter();
      final AdminStreamInfoList result = adminClient.listEnterpriseStreams(filter, 0, 0);
      assertNotNull(result);
      assertEquals(4,  result.getCount());
      assertEquals(0, result.getSkip());
      assertEquals(50, result.getLimit());

      final AdminStreamFilter filter1 = result.getFilter();
      assertNotNull(filter1);
      verifyFilter(filter1);

      final List<AdminStreamInfo> adminStreamInfoList = result.getStreams();
      assertNotNull(adminStreamInfoList);
      int num = 0;
      AdminStreamAttributes adminStreamAttributes = null;

      String id;
      boolean isExternal;
      boolean isActive;
      boolean isPublic;
      String type;

      String roomName;
      String roomDescription;
      long createdByUserId;
      long createdDate;
      long lastModifiedDate;
      String originCompany;
      Integer originCompanyId;
      Integer membersCounts;
      long lastMessageDate;

      final List<Long> listMembers = new ArrayList<Long>();

      for (final AdminStreamInfo adminStreamInfo : adminStreamInfoList){
        num++;
        assertNotNull(adminStreamInfo);
        adminStreamAttributes = adminStreamInfo.getAttributes();
        assertNotNull(adminStreamAttributes);
        listMembers.clear();
        if(num == 1){
          id  = "Q2KYGm7JkljrgymMajYTJ3___qcLPr1UdA";
          isExternal = false;
          isActive = true;
          isPublic = false;
          type = "ROOM";
          verifyStream(adminStreamInfo, id, isExternal, isActive, isPublic, type);

          roomName = "Active Internal Private Room";
          roomDescription = "Active Internal Private Room";
          createdByUserId = 8933531975689L;
          createdDate = 1481575056047L;
          lastModifiedDate = 1481575056047L;
          originCompany = "Symphony";
          originCompanyId = Integer.valueOf(130);
          membersCounts = Integer.valueOf(1);
          lastMessageDate = 1516699467959L;
          verifyAttributes(adminStreamAttributes, roomName, roomDescription, createdByUserId,  createdDate, lastModifiedDate, originCompany, originCompanyId, membersCounts, lastMessageDate);
        }
        else if(num == 2){
          id  = "_KnoYrMkhEn3H2_8vE0kl3___qb5SANQdA";
          isExternal = true;
          isActive = false;
          isPublic = false;
          type = "ROOM";
          verifyStream(adminStreamInfo, id, isExternal, isActive, isPublic, type);

          roomName = "Inactive External Room";
          roomDescription = "Inactive External Room";
          createdByUserId = 8933531975686L;
          createdDate = 1481876438194L;
          lastModifiedDate = 1481876438194L;
          originCompany = "Symphony";
          originCompanyId = Integer.valueOf(130);
          membersCounts = Integer.valueOf(2);
          lastMessageDate = 1516699467959L;
          verifyAttributes(adminStreamAttributes, roomName, roomDescription, createdByUserId,  createdDate, lastModifiedDate, originCompany, originCompanyId, membersCounts, lastMessageDate);
        }
        else if(num == 3){
          id  = "fBoaBSRUyb5Rq3YgeSqZvX___qbf5IAhdA";
          isExternal = false;
          isActive = true;
          type = "IM";
          verifyStream(adminStreamInfo, id, isExternal, isActive, type);

          listMembers.add(8933531975686L);
          listMembers.add(8933531975689L);

          createdByUserId = 8933531975689L;
          createdDate = 1482302390238L;
          lastModifiedDate = 1482302390238L;
          originCompany = "Symphony";
          originCompanyId = Integer.valueOf(130);
          membersCounts = Integer.valueOf(2);
          lastMessageDate = 1516699467959L;
          verifyAttributes(adminStreamAttributes, createdByUserId, createdDate, lastModifiedDate, originCompany, originCompanyId, membersCounts, lastMessageDate, listMembers);
        }
        else if(num == 4){
          id  = "k19u9c3GSE_iq0VHDKe1on___qa0Cp2WdA";
          isExternal = false;
          isActive = true;
          type = "MIM";
          verifyStream(adminStreamInfo, id, isExternal, isActive, type);

          listMembers.add(8933531975688L);
          listMembers.add(8933531975689L);
          listMembers.add(8933531975717L);

          createdByUserId = 8933531975688L;
          createdDate = 1483038089833L;
          lastModifiedDate = 1483038089833L;
          originCompany = "Symphony";
          originCompanyId = Integer.valueOf(130);
          membersCounts = Integer.valueOf(3);
          lastMessageDate = 1516699467959L;
          verifyAttributes(adminStreamAttributes, createdByUserId, createdDate, lastModifiedDate, originCompany, originCompanyId, membersCounts, lastMessageDate, listMembers);
        }
      }
    } catch (SymClientException e) {
      fail();
    }
  }
  ////////// End listEnterpriseStreams

  private void verifyAttributes(final AdminStreamAttributes adminStreamAttributes, final String roomName, final String roomDescription, final long createdByUserId, final long createdDate, final long lastModifiedDate, final String originCompany, final Integer originCompanyId, final Integer membersCount, final long lastMessageDate) {
    assertEquals(roomName, adminStreamAttributes.getRoomName());
    assertEquals(roomDescription, adminStreamAttributes.getRoomDescription());
    assertEquals(createdByUserId, adminStreamAttributes.getCreatedByUserId());
    assertEquals(createdDate, adminStreamAttributes.getCreatedDate());
    assertEquals(lastModifiedDate, adminStreamAttributes.getLastModifiedDate());
    assertEquals(originCompany, adminStreamAttributes.getOriginCompany());
    assertEquals(originCompanyId, adminStreamAttributes.getOriginCompanyId());
    assertEquals(membersCount, adminStreamAttributes.getMembersCount());
    assertEquals(lastMessageDate, adminStreamAttributes.getLastMessageDate());
  }

  private void verifyAttributes(final AdminStreamAttributes adminStreamAttributes, final long createdByUserId, final long createdDate, final long lastModifiedDate, final String originCompany, final Integer originCompanyId, final Integer membersCount, final long lastMessageDate, final List<Long> listMembers) {
    assertEquals(createdByUserId, adminStreamAttributes.getCreatedByUserId());
    assertEquals(createdDate, adminStreamAttributes.getCreatedDate());
    assertEquals(lastModifiedDate, adminStreamAttributes.getLastModifiedDate());
    assertEquals(originCompany, adminStreamAttributes.getOriginCompany());
    assertEquals(originCompanyId, adminStreamAttributes.getOriginCompanyId());
    assertEquals(membersCount, adminStreamAttributes.getMembersCount());
    assertEquals(lastMessageDate, adminStreamAttributes.getLastMessageDate());
    assertEquals(listMembers.size(), adminStreamAttributes.getMembers().size());
    final int membersSizeGot = adminStreamAttributes.getMembers().size();
    final int membersSizeRead = listMembers.size();
    assertEquals(membersSizeRead, membersSizeGot);
    for(int i = 0; i < membersSizeGot; i++){
      assertEquals(listMembers.get(i), adminStreamAttributes.getMembers().get(i));
    }
  }

  private void verifyStream(final AdminStreamInfo adminStreamInfo, final String id, final boolean isExternal, final boolean isActive, final boolean isPublic, final String type) {
    assertEquals(id, adminStreamInfo.getId());
    assertEquals(isExternal, adminStreamInfo.isExternal());
    assertEquals(isActive, adminStreamInfo.isActive());
    assertEquals(isPublic, adminStreamInfo.isPublic());
    assertEquals(type, adminStreamInfo.getType());
  }

  private void verifyStream(final AdminStreamInfo adminStreamInfo, final String id, final boolean isExternal, final boolean isActive, final String type) {
    assertEquals(id, adminStreamInfo.getId());
    assertEquals(isExternal, adminStreamInfo.isExternal());
    assertEquals(isActive, adminStreamInfo.isActive());
    assertEquals(type, adminStreamInfo.getType());
  }

  private void verifyFilter(final AdminStreamFilter filter1) {
    assertEquals(1, filter1.getStreamTypes().size());
    assertEquals("ROOM", filter1.getStreamTypes().get(0));
    assertEquals("EXTERNAL", filter1.getScope());
    assertEquals("EXTERNAL", filter1.getOrigin());
    assertEquals("ACTIVE", filter1.getStatus());
    assertEquals("PRIVATE", filter1.getPrivacy());
    assertEquals(Long.valueOf(1481575056047L), filter1.getStartDate());
    assertEquals(Long.valueOf(1483038089833L), filter1.getEndDate());
  }

  ////////// createIM
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

    try {
      assertNotNull(adminClient);

      final List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
      assertNotNull(userIdList);

      final String streamId = adminClient.createIM(userIdList);
      assertNotNull(streamId);
      assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void createIMFailure400() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEIM))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
    assertNotNull(userIdList);

    final String streamId = adminClient.createIM(userIdList);
    assertNotNull(streamId);
    assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);
  }

  @Test(expected = SymClientException.class)
  public void createIMFailure401() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEIM))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
    assertNotNull(userIdList);

    final String streamId = adminClient.createIM(userIdList);
    assertNotNull(streamId);
    assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);
  }

  @Test(expected = ForbiddenException.class)
  public void createIMFailure403() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEIM))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
    assertNotNull(userIdList);

    final String streamId = adminClient.createIM(userIdList);
    assertNotNull(streamId);
    assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);
  }

  @Test(expected = ServerErrorException.class)
  public void createIMFailure500() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEIM))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<Long> userIdList = Stream.of(1L, 2L).collect(Collectors.toList());
    assertNotNull(userIdList);

    final String streamId = adminClient.createIM(userIdList);
    assertNotNull(streamId);
    assertEquals("xhGxbTcvTDK6EIMMrwdOrX___quztr2HdA", streamId);
  }
  ////////// End createIM

  ////////// getUser
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
      assertNotNull(adminClient);
      final AdminUserInfo adminUser = adminClient.getUser(1L);
      assertNotNull(adminUser);

      final AdminUserAttributes adminUserAttributes = adminUser.getUserAttributes();
      assertNotNull(adminUserAttributes);
      assertEquals("bot.user@email.com", adminUserAttributes.getEmailAddress());
      assertEquals("bot.user", adminUserAttributes.getUserName());
      assertEquals("bot.user", adminUserAttributes.getDisplayName());
      assertEquals("SYSTEM", adminUserAttributes.getAccountType());

      final AdminUserSystemInfo userSystemInfo = adminUser.getUserSystemInfo();
      assertNotNull(userSystemInfo);
      assertEquals(9826885173289L, userSystemInfo.getId().longValue());
      assertEquals("ENABLED", userSystemInfo.getStatus());
      assertEquals(1499373423000L, userSystemInfo.getCreatedDate().longValue());
      assertEquals("9826885173255", userSystemInfo.getCreatedBy());
      assertEquals(1499373423803L, userSystemInfo.getLastUpdatedDate().longValue());

      final List<String> userRoles = adminUser.getRoles();
      assertNotNull(userRoles);
      assertEquals(3, userRoles.size());
      for (int i = 0; i < 3; i++){
        if(i == 0)
          assertEquals("USER_PROVISIONING", userRoles.get(i));
        else if(i == 1)
          assertEquals("CONTENT_MANAGEMENT", userRoles.get(i));
        else if(i == 2)
          assertEquals("INDIVIDUAL", userRoles.get(i));
      }

      assertTrue(adminUser.getFeatures().isEmpty());
      assertTrue(adminUser.getApps().isEmpty());
      assertTrue(adminUser.getGroups().isEmpty());
      assertTrue(adminUser.getDisclaimers().isEmpty());

      final Avatar avatar = adminUser.getAvatar();
      assertNotNull(avatar);
      assertEquals("small", avatar.getSize());
      assertEquals("../avatars/static/50/default.png", avatar.getUrl());

    } catch (NoContentException nce) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    AdminUserInfo adminUser = null;
    try {
      adminUser = adminClient.getUser(1L);
    } catch (NoContentException e) {
      e.printStackTrace();
    }
    assertNotNull(adminUser);
  }

  @Test(expected = SymClientException.class)
  public void getUserFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);
    AdminUserInfo adminUser = null;
    try {
      adminUser = adminClient.getUser(1L);
    } catch (NoContentException e) {
      e.printStackTrace();
    }
    assertNotNull(adminUser);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);
    AdminUserInfo adminUser = null;
    try {
      adminUser = adminClient.getUser(1L);
    } catch (NoContentException e) {
      e.printStackTrace();
    }
    assertNotNull(adminUser);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    AdminUserInfo adminUser = null;
    try {
      adminUser = adminClient.getUser(1L);
    } catch (NoContentException e) {
      e.printStackTrace();
    }
    assertNotNull(adminUser);
  }
  ////////// End getUser

  ////////// listUsers
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

    try {
      assertNotNull(adminClient);

      final List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);
      assertNotNull(adminUsers);
      assertEquals(2, adminUsers.size());

      int num=0;
      AdminUserAttributes adminUserAttributes;
      AdminUserSystemInfo adminUserSystemInfo;
      List<String> roles;

      String expectedEmailAddress = null;
      String expectedFirstName = null;
      String expectedLastName = null;
      String expectedUserName = null;
      String expectedDisplayName = null;
      String expectedAccountType = null;
      List<String> expectedRoles;
      List<String> assetClasses;
      List<String> industries;

      long expectedId;
      String expectedStatus;
      long expectedCreatedDate;
      long expectedLastUpdatedDate;
      long expectedLastLoginDate;
      String expectedCratedBy;
      for(final AdminUserInfo adminUserInfo : adminUsers){
        num++;
        adminUserAttributes = adminUserInfo.getUserAttributes();
        adminUserSystemInfo = adminUserInfo.getUserSystemInfo();
        roles = adminUserInfo.getRoles();
        assertNotNull(adminUserAttributes);
        assertNotNull(adminUserSystemInfo);
        assertNotNull(roles);

        if(num == 1){
          expectedEmailAddress = "agentservice@acme.com";
          expectedFirstName = "Agent Service";
          expectedUserName = "agentservice";
          expectedDisplayName = "Agent Service";
          expectedAccountType = "SYSTEM";

          expectedId = 9826885173252L;
          expectedStatus = "ENABLED";
          expectedCreatedDate = 1498665229000L;
          expectedLastUpdatedDate = 1498665229886L;
          expectedLastLoginDate = 1504899004993L;
          expectedRoles = new ArrayList(Arrays.asList("USER_PROVISIONING","CONTENT_MANAGEMENT","L2_SUPPORT","INDIVIDUAL","AGENT","SUPER_ADMINISTRATOR","SUPER_COMPLIANCE_OFFICER"));

          verifyUserAttributes(adminUserAttributes, expectedEmailAddress, expectedFirstName, expectedUserName, expectedDisplayName, expectedAccountType);
          verifyUserSystemInfo(adminUserInfo, adminUserSystemInfo, expectedId, expectedStatus, expectedCreatedDate, expectedLastUpdatedDate, expectedLastLoginDate, expectedRoles);
        }
        else if(num == 2){
          expectedEmailAddress = "bot.user1@acme.yaml.com";
          expectedFirstName = "Nexus1";
          expectedLastName = "Bot01";
          expectedUserName = "bot.user1";
          expectedDisplayName = "ACME Bot01";
          expectedAccountType = "NORMAL";
          assetClasses = new ArrayList<String>();
          industries = new ArrayList<String>();

          expectedId = 9826885173258L;
          expectedStatus = "ENABLED";
          expectedCreatedDate = 1499347606000L;
          expectedCratedBy = "9826885173252";
          expectedLastUpdatedDate = 1499348554853L;
          expectedLastLoginDate = 1504839044527L;
          expectedRoles = new ArrayList(Arrays.asList("USER_PROVISIONING","CONTENT_EXPORT_SERVICE","CONTENT_MANAGEMENT","L2_SUPPORT","INDIVIDUAL","SUPER_ADMINISTRATOR","SUPER_COMPLIANCE_OFFICER"));

          verifyUserAttributes(adminUserAttributes, expectedEmailAddress, expectedFirstName, expectedLastName, expectedUserName, expectedDisplayName, expectedAccountType, assetClasses, industries);
          verifyUserSystemInfo(adminUserInfo, adminUserSystemInfo, expectedId, expectedStatus, expectedCreatedDate, expectedCratedBy, expectedLastUpdatedDate, expectedLastLoginDate, expectedRoles);
        }
      }
    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void listUsersFailure400() {
    stubFor(get(urlEqualTo(PodConstants.LISTUSERSADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);
  }

  @Test(expected = SymClientException.class)
  public void listUsersFailure401() {
    stubFor(get(urlEqualTo(PodConstants.LISTUSERSADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);
    final List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);
  }

  @Test(expected = ForbiddenException.class)
  public void listUsersFailure403() {
    stubFor(get(urlEqualTo(PodConstants.LISTUSERSADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);
    final List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);
  }

  @Test(expected = ServerErrorException.class)
  public void listUsersFailure500() {
    stubFor(get(urlEqualTo(PodConstants.LISTUSERSADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final List<AdminUserInfo> adminUsers = adminClient.listUsers(0, 0);
  }
  ////////// End listUsers

  private void verifyUserSystemInfo(final AdminUserInfo adminUserInfo, final AdminUserSystemInfo adminUserSystemInfo, final long expectedId, final String expectedStatus, final long expectedCreatedDate, final long expectedLastUpdatedDate, final long expectedLastLoginDate, final List<String> expectedRoles) {
    assertEquals(expectedId, adminUserSystemInfo.getId().longValue());
    assertEquals(expectedStatus, adminUserSystemInfo.getStatus());
    assertEquals(expectedCreatedDate, adminUserSystemInfo.getCreatedDate().longValue());
    assertEquals(expectedLastUpdatedDate, adminUserSystemInfo.getLastUpdatedDate().longValue());
    assertEquals(expectedLastLoginDate, adminUserSystemInfo.getLastLoginDate().longValue());
    assertEquals(expectedRoles, adminUserInfo.getRoles());
  }

  private void verifyUserSystemInfo(final AdminUserInfo adminUserInfo, final AdminUserSystemInfo adminUserSystemInfo, final long expectedId, final String expectedStatus, final long expectedCreatedDate, final String expectedCratedBy, final long expectedLastUpdatedDate, final long expectedLastLoginDate, final List<String> expectedRoles) {
    assertEquals(expectedId, adminUserSystemInfo.getId().longValue());
    assertEquals(expectedStatus, adminUserSystemInfo.getStatus());
    assertEquals(expectedCreatedDate, adminUserSystemInfo.getCreatedDate().longValue());
    assertEquals(expectedCratedBy, adminUserSystemInfo.getCreatedBy());
    assertEquals(expectedLastUpdatedDate, adminUserSystemInfo.getLastUpdatedDate().longValue());
    assertEquals(expectedLastLoginDate, adminUserSystemInfo.getLastLoginDate().longValue());
    assertEquals(expectedRoles, adminUserInfo.getRoles());
  }

  private void verifyUserAttributes(final AdminUserAttributes adminUserAttributes, final String expectedEmailAddress, final String expectedFirstName, final String expectedUserName, final String expectedDisplayName, final String expectedAccountType) {
    assertEquals(expectedEmailAddress, adminUserAttributes.getEmailAddress());
    assertEquals(expectedFirstName, adminUserAttributes.getFirstName());
    assertEquals(expectedUserName, adminUserAttributes.getUserName());
    assertEquals(expectedDisplayName, adminUserAttributes.getDisplayName());
    assertEquals(expectedAccountType, adminUserAttributes.getAccountType());
  }

  private void verifyUserAttributes(final AdminUserAttributes adminUserAttributes, final String expectedEmailAddress, final String expectedFirstName, final String expectedLastName, final String expectedUserName, final String expectedDisplayName, final String expectedAccountType, final List<String> expectedAssetClasses, final List<String> expectedIndustries) {
    assertEquals(expectedEmailAddress, adminUserAttributes.getEmailAddress());
    assertEquals(expectedFirstName, adminUserAttributes.getFirstName());
    assertEquals(expectedLastName, adminUserAttributes.getLastName());
    assertEquals(expectedUserName, adminUserAttributes.getUserName());
    assertEquals(expectedDisplayName, adminUserAttributes.getDisplayName());
    assertEquals(expectedAccountType, adminUserAttributes.getAccountType());
    assertEquals(expectedAssetClasses, adminUserAttributes.getAssetClasses());
    assertEquals(expectedIndustries, adminUserAttributes.getIndustries());
  }

  ////////// createUser
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


    try {
      assertNotNull(adminClient);

      final AdminUserInfo adminUserInfo = adminClient.createUser(new AdminNewUser());
      assertNotNull(adminUserInfo);

      final AdminUserAttributes adminUserAttributes = adminUserInfo.getUserAttributes();
      assertNotNull(adminUserAttributes);
      assertEquals("janedoe@symphony.com", adminUserAttributes.getEmailAddress());
      assertEquals("Jane", adminUserAttributes.getFirstName());
      assertEquals("Doe", adminUserAttributes.getLastName());
      assertEquals("janedoe", adminUserAttributes.getUserName());
      assertEquals("Jane Doe", adminUserAttributes.getDisplayName());
      assertEquals("Company Name", adminUserAttributes.getCompanyName());
      assertEquals("", adminUserAttributes.getDepartment());
      assertEquals("", adminUserAttributes.getDivision());
      assertEquals("Sales", adminUserAttributes.getTitle());
      assertEquals("", adminUserAttributes.getTwoFactorAuthPhone());
      assertEquals("", adminUserAttributes.getWorkPhoneNumber());
      assertEquals("", adminUserAttributes.getMobilePhoneNumber());
      assertEquals("NORMAL", adminUserAttributes.getAccountType());
      assertEquals("San Francisco", adminUserAttributes.getLocation());
      assertEquals("Sales", adminUserAttributes.getJobFunction());

      final List<String> assetClasses = adminUserAttributes.getAssetClasses();
      assertNotNull(assetClasses);
      assertEquals(1, assetClasses.size());
      assertEquals("Commodities", assetClasses.get(0));

      final List<String> industries = adminUserAttributes.getIndustries();
      assertNotNull(industries);
      assertEquals(1, industries.size());
      assertEquals("Basic Materials", industries.get(0));

      final UserKey userKey = adminUserAttributes.getCurrentKey();
      assertNotNull(userKey);
      assertEquals("-----BEGIN PUBLIC KEY-----\nMIICI==\n-----END PUBLIC KEY-----", userKey.getKey());

      final AdminUserSystemInfo adminUserSystemInfo = adminUserInfo.getUserSystemInfo();
      assertNotNull(adminUserSystemInfo);
      assertEquals(7215545078541L, adminUserSystemInfo.getId().longValue());
      assertEquals("ENABLED", adminUserSystemInfo.getStatus());
      assertEquals(1461509290000L, adminUserSystemInfo.getCreatedDate().longValue());
      assertEquals("7215545078229", adminUserSystemInfo.getCreatedBy());
      assertEquals(1461509290000L, adminUserSystemInfo.getLastUpdatedDate().longValue());

      final List<String> roles = adminUserInfo.getRoles();
      assertNotNull(roles);
      assertEquals(1, roles.size());
      assertEquals("INDIVIDUAL", roles.get(0));

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void createUserFailure400() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEUSER))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.createUser(new AdminNewUser());
  }

  @Test(expected = SymClientException.class)
  public void createUserFailure401() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEUSER))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.createUser(new AdminNewUser());
  }

  @Test(expected = ForbiddenException.class)
  public void createUserFailure403() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEUSER))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.createUser(new AdminNewUser());
  }

  @Test(expected = ServerErrorException.class)
  public void createUserFailure500() {
    stubFor(post(urlEqualTo(PodConstants.ADMINCREATEUSER))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.createUser(new AdminNewUser());
  }
  ////////// End createUser

  ///////// updateUser
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


    try {
      assertNotNull(adminClient);

      final AdminUserInfo adminUserInfo = adminClient.updateUser(7215545078541L, new AdminUserAttributes());
      assertNotNull(adminUserInfo);

      final AdminUserAttributes adminUserAttributes = adminUserInfo.getUserAttributes();
      assertNotNull(adminUserAttributes);
      assertEquals("janedoe@symphony.com", adminUserAttributes.getEmailAddress());
      assertEquals("Jane", adminUserAttributes.getFirstName());
      assertEquals("Doe", adminUserAttributes.getLastName());
      assertEquals("janedoe", adminUserAttributes.getUserName());
      assertEquals("Jane Doe", adminUserAttributes.getDisplayName());
      assertEquals("Company Name", adminUserAttributes.getCompanyName());
      assertEquals("", adminUserAttributes.getDepartment());
      assertEquals("", adminUserAttributes.getDivision());
      assertEquals("Sales", adminUserAttributes.getTitle());
      assertEquals("", adminUserAttributes.getTwoFactorAuthPhone());
      assertEquals("", adminUserAttributes.getWorkPhoneNumber());
      assertEquals("", adminUserAttributes.getMobilePhoneNumber());
      assertEquals("NORMAL", adminUserAttributes.getAccountType());
      assertEquals("San Francisco", adminUserAttributes.getLocation());
      assertEquals("Sales", adminUserAttributes.getJobFunction());

      final List<String> assetClasses = adminUserAttributes.getAssetClasses();
      assertNotNull(assetClasses);
      assertEquals(1, assetClasses.size());
      assertEquals("Commodities", assetClasses.get(0));

      final List<String> industries = adminUserAttributes.getIndustries();
      assertNotNull(industries);
      assertEquals(1, industries.size());
      assertEquals("Basic Materials", industries.get(0));

      final UserKey userKey = adminUserAttributes.getCurrentKey();
      assertNotNull(userKey);
      assertEquals("-----BEGIN PUBLIC KEY-----\nMIICI==\n-----END PUBLIC KEY-----", userKey.getKey());

      final AdminUserSystemInfo adminUserSystemInfo = adminUserInfo.getUserSystemInfo();
      assertNotNull(adminUserSystemInfo);
      assertEquals(7215545078541L, adminUserSystemInfo.getId().longValue());
      assertEquals("ENABLED", adminUserSystemInfo.getStatus());
      assertEquals(1461509290000L, adminUserSystemInfo.getCreatedDate().longValue());
      assertEquals("7215545078229", adminUserSystemInfo.getCreatedBy());
      assertEquals(1461509290000L, adminUserSystemInfo.getLastUpdatedDate().longValue());

      final List<String> roles = adminUserInfo.getRoles();
      assertNotNull(roles);
      assertEquals(1, roles.size());
      assertEquals("INDIVIDUAL", roles.get(0));

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void updateUserFailure400() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEUSER.replace("{uid}", "7215545078541")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));
    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.updateUser(7215545078541L, new AdminUserAttributes());
  }

  @Test(expected = SymClientException.class)
  public void updateUserFailure401() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEUSER.replace("{uid}", "7215545078541")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.updateUser(7215545078541L, new AdminUserAttributes());
  }

  @Test(expected = ForbiddenException.class)
  public void updateUserFailure403() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEUSER.replace("{uid}", "7215545078541")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"}")));
    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.updateUser(7215545078541L, new AdminUserAttributes());
  }

  @Test(expected = ServerErrorException.class)
  public void updateUserFailure500() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEUSER.replace("{uid}", "7215545078541")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final AdminUserInfo adminUserInfo = adminClient.updateUser(7215545078541L, new AdminUserAttributes());
  }
  ///////// End updateUser

  //////// getAvatar
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

    try {
      assertNotNull(adminClient);

      final List<Avatar> avatarList = adminClient.getAvatar(1L);
      assertNotNull(avatarList);
      assertEquals(5, avatarList.size());

      final List<String> expectedSizes = new ArrayList(Arrays.asList("600", "150", "orig", "500", "50"));
      final List<String> expectedUrls = new ArrayList(Arrays.asList(
              "../avatars/acme/600/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png",
              "../avatars/acme/150/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png",
              "../avatars/acme/102/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png",
              "../avatars/acme/500/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png",
              "../avatars/acme/50/7215545057281/3gXMhglCCTwLPL9JAprnyHzYn5-PR49-wYDG814n1g8.png"
      ));

      for(int i = 0; i < 5; i++){
        assertEquals(expectedSizes.get(i), avatarList.get(i).getSize());
        assertEquals(expectedUrls.get(i), avatarList.get(i).getUrl());
      }

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getAvatarFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETAVATARADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<Avatar> avatarList = adminClient.getAvatar(1L);
  }

  @Test(expected = SymClientException.class)
  public void getAvatarFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETAVATARADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<Avatar> avatarList = adminClient.getAvatar(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void getAvatarFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETAVATARADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<Avatar> avatarList = adminClient.getAvatar(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void getAvatarFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETAVATARADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<Avatar> avatarList = adminClient.getAvatar(1L);
  }
  //////// End getAvatar

  //////// updateAvatar
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
      assertNotNull(adminClient);

      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
      assertTrue(true);
    } catch (IOException io) {
      fail();
    } catch (SymClientException e){
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void updateAvatarFailure400() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    try {
      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test(expected = SymClientException.class)
  public void updateAvatarFailure401() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    try {
      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test(expected = ForbiddenException.class)
  public void updateAvatarFailure403() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    try {
      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test(expected = ServerErrorException.class)
  public void updateAvatarFailure500() {
    stubFor(post(urlEqualTo(PodConstants.ADMINUPDATEAVATAR.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    try {
      adminClient.updateAvatar(1L, AdminClientTest.class.getResource("/avatar.png").getFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  //////// End updateAvatar

  //////// getUserStatus
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

    try {
      assertNotNull(adminClient);
      final String status = adminClient.getUserStatus(1L);
      assertNotNull(status);
      assertEquals("ENABLED", status);
    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserStatusFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final String status = adminClient.getUserStatus(1L);
  }

  @Test(expected = SymClientException.class)
  public void getUserStatusFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);
    final String status = adminClient.getUserStatus(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserStatusFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);
    final String status = adminClient.getUserStatus(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserStatusFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    final String status = adminClient.getUserStatus(1L);
  }
  //////// End getUserStatus

  //////// updateUserStatus
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

    try {
      assertNotNull(adminClient);
      adminClient.updateUserStatus(1L, "DISABLED");
      assertTrue(true);
    } catch (SymClientException e){
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void updateUserStatusFailure400() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    adminClient.updateUserStatus(1L, "DISABLED");
  }

  @Test(expected = SymClientException.class)
  public void updateUserStatusFailure401() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);
    adminClient.updateUserStatus(1L, "DISABLED");
  }

  @Test(expected = ForbiddenException.class)
  public void updateUserStatusFailure403() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);
    adminClient.updateUserStatus(1L, "DISABLED");
  }

  @Test(expected = ServerErrorException.class)
  public void updateUserStatusFailure500() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERSTATUSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    adminClient.updateUserStatus(1L, "DISABLED");
  }
  //////// End updateUserStatus

  /////// listPodFeatures
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

    try {
      assertNotNull(adminClient);

      final List<String> features = adminClient.listPodFeatures();
      assertNotNull(features);

      assertEquals(24, features.size());

      assertEquals("postReadEnabled", features.get(0));
      assertEquals("postWriteEnabled", features.get(1));
      assertEquals("delegatesEnabled", features.get(2));
      assertEquals("isExternalIMEnabled", features.get(3));
      assertEquals("canShareFilesExternally", features.get(4));
      assertEquals("canCreatePublicRoom", features.get(5));
      assertEquals("canUpdateAvatar", features.get(6));
      assertEquals("isExternalRoomEnabled", features.get(7));
      assertEquals("canCreatePushedSignals", features.get(8));
      assertEquals("canUseCompactMode", features.get(9));
      assertEquals("mustBeRecorded", features.get(10));
      assertEquals("sendFilesEnabled", features.get(11));
      assertEquals("canUseInternalAudio", features.get(12));
      assertEquals("canUseInternalVideo", features.get(13));
      assertEquals("canProjectInternalScreenShare", features.get(14));
      assertEquals("canViewInternalScreenShare", features.get(15));
      assertEquals("canCreateMultiLateralRoom", features.get(16));
      assertEquals("canJoinMultiLateralRoom", features.get(17));
      assertEquals("canUseFirehose", features.get(18));
      assertEquals("canUseInternalAudioMobile", features.get(19));
      assertEquals("canUseInternalVideoMobile", features.get(20));
      assertEquals("canProjectInternalScreenShareMobile", features.get(21));
      assertEquals("canViewInternalScreenShareMobile", features.get(22));
      assertEquals("canManageSignalSubscription", features.get(23));

    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void listPodFeaturesFailure400() {
    stubFor(get(urlEqualTo(PodConstants.PODFEATURESADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<String> features = adminClient.listPodFeatures();
  }

  @Test(expected = SymClientException.class)
  public void listPodFeaturesFailure401() {
    stubFor(get(urlEqualTo(PodConstants.PODFEATURESADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<String> features = adminClient.listPodFeatures();
  }

  @Test(expected = ForbiddenException.class)
  public void listPodFeaturesFailure403() {
    stubFor(get(urlEqualTo(PodConstants.PODFEATURESADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<String> features = adminClient.listPodFeatures();
  }

  @Test(expected = ServerErrorException.class)
  public void listPodFeaturesFailure500() {
    stubFor(get(urlEqualTo(PodConstants.PODFEATURESADMIN))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<String> features = adminClient.listPodFeatures();
  }
  /////// End listPodFeatures

  ////// getUserFeatures
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

    try {
      assertNotNull(adminClient);

      final List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);
      assertNotNull(entitlements);
      assertEquals(6, entitlements.size());

      FeatureEntitlement featureEntitlement = null;
      String expectedEntitlement = null;
      boolean expectedEnable = true;
      for(int i = 0; i < 6; i++){
        featureEntitlement = entitlements.get(i);
        assertNotNull(featureEntitlement);

        if(i == 0){
          expectedEntitlement = "canCreatePublicRoom";
          expectedEnable = true;
        } else if(i == 1){
          expectedEntitlement = "isExternalRoomEnabled";
          expectedEnable = false;
        } else if(i == 2){
          expectedEntitlement = "delegatesEnabled";
          expectedEnable = true;
        } else if(i == 3){
          expectedEntitlement = "isExternalIMEnabled";
          expectedEnable = true;
        } else if(i == 4){
          expectedEntitlement = "sendFilesEnabled";
          expectedEnable = true;
        } else if(i == 5){
          expectedEntitlement = "canUpdateAvatar";
          expectedEnable = true;
        }
        assertEquals(expectedEntitlement, featureEntitlement.getEntitlment());
        assertEquals(expectedEnable, featureEntitlement.getEnabled());
      }
    } catch (SymClientException e){
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserFeaturesFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);
  }

  @Test(expected = SymClientException.class)
  public void getUserFeaturesFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserFeaturesFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"}")));

    assertNotNull(adminClient);

    final List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserFeaturesFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<FeatureEntitlement> entitlements = adminClient.getUserFeatures(1L);
  }
  ////// End getUserFeatures

  ////// updateUserFeatures
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

    try {
      assertNotNull(adminClient);

      adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());

      assertTrue(true);

    } catch (SymClientException e) {
      e.printStackTrace();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void updateUserFeaturesFailure400() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);
    adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());
  }

  @Test(expected = SymClientException.class)
  public void updateUserFeaturesFailure401() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{\"code\": 401,\n" +
                            "  \"message\": \"Invalid session\"\n" +
                            "}}")));

    assertNotNull(adminClient);
    adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());
  }

  @Test(expected = ForbiddenException.class)
  public void updateUserFeaturesFailure403() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{\"code\": 403,\n" +
                            "  \"message\": \"The user lacks the required entitlement to perform this operation\"\n" +
                            "}}")));
    assertNotNull(adminClient);
    adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());
  }

  @Test(expected = ServerErrorException.class)
  public void updateUserFeaturesFailure500() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERFEATURESADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));
    assertNotNull(adminClient);
    adminClient.updateUserFeatures(1L, new ArrayList<FeatureEntitlement>());
  }
  ////// End updateUserFeatures

  ////// getUserApplicationEntitlements
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

    try {
      assertNotNull(adminClient);

      final List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);
      assertNotNull(entitlements);
      assertEquals(3, entitlements.size());

      int num = 0;
      for (final ApplicationEntitlement applicationEntitlement : entitlements) {
        assertNotNull(applicationEntitlement);
        num++;
        if (num == 1) {
          assertEquals("djApp", applicationEntitlement.getAppId());
          assertEquals("Dow Jones", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(false, applicationEntitlement.getInstall());
        } else if (num == 2) {
          assertEquals("spcapiq", applicationEntitlement.getAppId());
          assertEquals("S&P Capital IQ Data", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(false, applicationEntitlement.getInstall());
        } else if (num == 3) {
          assertEquals("selerity", applicationEntitlement.getAppId());
          assertEquals("Selerity Context", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(true, applicationEntitlement.getInstall());

          final List<ApplicationProduct> applicationProductList = applicationEntitlement.getProducts();
          assertEquals(2, applicationProductList.size());

          int cnt = 0;
          for (final ApplicationProduct applicationProduct : applicationProductList) {
            cnt++;
            if (cnt == 1) {
              assertEquals("selerity", applicationProduct.getAppId());
              assertEquals("Standard", applicationProduct.getName());
              assertEquals(true, applicationProduct.getSubscribed());
              assertEquals("default", applicationProduct.getType());
            } else if (cnt == 2) {
              assertEquals("selerity", applicationProduct.getAppId());
              assertEquals("Premium", applicationProduct.getName());
              assertEquals("AcDccU53SsY", applicationProduct.getSku());
              assertEquals(false, applicationProduct.getSubscribed());
              assertEquals("premium", applicationProduct.getType());
            }
          }
        }
      }
    } catch(SymClientException e){
        fail();
      }
  }

  @Test(expected = APIClientErrorException.class)
  public void getUserApplicationEntitlementsFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);
  }

  @Test(expected = SymClientException.class)
  public void getUserApplicationEntitlementsFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void getUserApplicationEntitlementsFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void getUserApplicationEntitlementsFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.getUserApplicationEntitlements(1L);
  }
  ////// End getUserApplicationEntitlements

  ////// updateUserApplicationEntitlements
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

    try {
      assertNotNull(adminClient);

      final List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
              1L, new ArrayList<ApplicationEntitlement>());
      assertNotNull(entitlements);
      assertEquals(3, entitlements.size());

      int num = 0;
      for (final ApplicationEntitlement applicationEntitlement : entitlements) {
        num++;
        if (num == 1) {
          assertEquals("djApp", applicationEntitlement.getAppId());
          assertEquals("Dow Jones", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(false, applicationEntitlement.getInstall());
        } else if (num == 2) {
          assertEquals("spcapiq", applicationEntitlement.getAppId());
          assertEquals("S&P Capital IQ Data", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(false, applicationEntitlement.getInstall());
        } else if (num == 3) {
          assertEquals("selerity", applicationEntitlement.getAppId());
          assertEquals("Selerity Context", applicationEntitlement.getAppName());
          assertEquals(true, applicationEntitlement.getListed());
          assertEquals(true, applicationEntitlement.getInstall());

          List<ApplicationProduct> applicationProductList = applicationEntitlement.getProducts();
          assertEquals(2, applicationProductList.size());

          int cnt = 0;
          for (final ApplicationProduct applicationProduct : applicationProductList) {
            cnt++;
            if (cnt == 1) {
              assertEquals("selerity", applicationProduct.getAppId());
              assertEquals("Standard", applicationProduct.getName());
              assertEquals(true, applicationProduct.getSubscribed());
              assertEquals("default", applicationProduct.getType());
            } else if (cnt == 2) {
              assertEquals("selerity", applicationProduct.getAppId());
              assertEquals("Premium", applicationProduct.getName());
              assertEquals("AcDccU53SsY", applicationProduct.getSku());
              assertEquals(false, applicationProduct.getSubscribed());
              assertEquals("premium", applicationProduct.getType());
            }
          }
        }
      }
    } catch (SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void updateUserApplicationEntitlementsFailure400() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
            1L, new ArrayList<ApplicationEntitlement>());
  }

  @Test(expected = SymClientException.class)
  public void updateUserApplicationEntitlementsFailure401() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 401," +
                            "\"message\": \"Invalid session\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
            1L, new ArrayList<ApplicationEntitlement>());
  }

  @Test(expected = ForbiddenException.class)
  public void updateUserApplicationEntitlementsFailure403() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{" +
                            "\"code\": 403," +
                            "\"message\": \"The user lacks the required entitlement to perform this operation\"" +
                            "}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
            1L, new ArrayList<ApplicationEntitlement>());
  }

  @Test(expected = ServerErrorException.class)
  public void updateUserApplicationEntitlementsFailure500() {
    stubFor(post(urlEqualTo(PodConstants.UPDATEUSERAPPLICATIONSADMIN.replace("{uid}", "1")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .withBody("{}")));

    assertNotNull(adminClient);

    final List<ApplicationEntitlement> entitlements = adminClient.updateUserApplicationEntitlements(
            1L, new ArrayList<ApplicationEntitlement>());
  }
  ////// End updateUserApplicationEntitlements
}
