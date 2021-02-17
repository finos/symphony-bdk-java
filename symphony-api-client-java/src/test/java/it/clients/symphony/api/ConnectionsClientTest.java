package it.clients.symphony.api;

import clients.symphony.api.ConnectionsClient;
import clients.symphony.api.constants.PodConstants;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.SymClientException;
import it.commons.BotTest;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import model.InboundConnectionRequest;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class ConnectionsClientTest extends BotTest {
  private ConnectionsClient connectionsClient;

  @Before
  public void initClient() {
    connectionsClient = new ConnectionsClient(symBotClient);
  }

  // getConnections
  @Test
  public void getConnectionsSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("[\r\n" +
                "  {\r\n" +
                "    \"userId\": 7078106126503,\r\n" +
                "    \"status\": \"PENDING_OUTGOING\",\r\n" +
                "    \"updatedAt\": 1471018076255\r\n" +
                "  },\r\n" +
                "  {\r\n" +
                "    \"userId\": 7078106103809,\r\n" +
                "    \"status\": \"PENDING_INCOMING\",\r\n" +
                "    \"updatedAt\": 1467562406219\r\n" +
                "  }\r\n" +
                "]")));

    try {
      assertNotNull(connectionsClient);

      final List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);
      assertNotNull(connections);

      assertEquals(2, connections.size());
      int n = 0;
      for(final InboundConnectionRequest connection : connections){
        n++;
        if(n == 1){
          assertEquals(7078106126503L, connection.getUserId().longValue());
          assertEquals("PENDING_OUTGOING", connection.getStatus());
          assertEquals(1471018076255L, connection.getUpdatedAt().longValue());
        } else if(n == 2){
          assertEquals(7078106103809L, connection.getUserId().longValue());
          assertEquals("PENDING_INCOMING", connection.getStatus());
          assertEquals(1467562406219L, connection.getUpdatedAt().longValue());
        }
      }
    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getConnectionsFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);
  }

  @Test(expected = SymClientException.class)
  public void getConnectionsFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);
  }

  @Test(expected = ForbiddenException.class)
  public void getConnectionsFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);
  }

  @Test(expected = ServerErrorException.class)
  public void getConnectionsFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONS))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);
  }
  // End getConnections

  // acceptConnectionRequest
  @Test
  public void acceptConnectionRequestSuccess() {
    stubFor(post(urlEqualTo(PodConstants.ACCEPTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userId\": 1,\r\n" +
                "  \"status\": \"ACCEPTED\",\r\n" +
                "  \"firstRequestedAt\": 1471046357339,\r\n" +
                "  \"updatedAt\": 1471046517684,\r\n" +
                "  \"requestCounter\": 1\r\n" +
                "}")));

    try {

      assertNotNull(connectionsClient);

      final InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);
      assertNotNull(connection);

      assertEquals(1L, connection.getUserId().longValue());
      assertEquals("ACCEPTED", connection.getStatus());
      assertEquals(1471046357339L, connection.getFirstRequestedAt().longValue());
      assertEquals(1471046517684L, connection.getUpdatedAt().longValue());
      assertEquals(1L, connection.getRequestCounter().byteValue());

    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void acceptConnectionRequestFailure400() {
    stubFor(post(urlEqualTo(PodConstants.ACCEPTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);
  }

  @Test(expected = SymClientException.class)
  public void acceptConnectionRequestFailure401() {
    stubFor(post(urlEqualTo(PodConstants.ACCEPTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void acceptConnectionRequestFailure403() {
    stubFor(post(urlEqualTo(PodConstants.ACCEPTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void acceptConnectionRequestFailure500() {
    stubFor(post(urlEqualTo(PodConstants.ACCEPTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);
  }
  //End acceptConnectionRequest

  // rejectConnectionRequest
  @Test
  public void rejectConnectionRequestSuccess() {
    stubFor(post(urlEqualTo(PodConstants.REJECTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userId\": 1,\r\n" +
                "  \"status\": \"REJECTED\",\r\n" +
                "  \"firstRequestedAt\": 1471044955409,\r\n" +
                "  \"updatedAt\": 1471045390420,\r\n" +
                "  \"requestCounter\": 1\r\n" +
                "}")));

    try {

      assertNotNull(connectionsClient);

      final InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);
      assertNotNull(connection);

      assertEquals(1L, connection.getUserId().longValue());
      assertEquals("REJECTED", connection.getStatus());
      assertEquals(1471044955409L, connection.getFirstRequestedAt().longValue());
      assertEquals(1471045390420L, connection.getUpdatedAt().longValue());
      assertEquals(1L, connection.getRequestCounter().byteValue());

    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void rejectConnectionRequestFailure400() {
    stubFor(post(urlEqualTo(PodConstants.REJECTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);
  }

  @Test(expected = SymClientException.class)
  public void rejectConnectionRequestFailure401() {
    stubFor(post(urlEqualTo(PodConstants.REJECTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void rejectConnectionRequestFailure403() {
    stubFor(post(urlEqualTo(PodConstants.REJECTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void rejectConnectionRequestFailure500() {
    stubFor(post(urlEqualTo(PodConstants.REJECTCONNECTION))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);
  }
  // End rejectConnectionRequest

  // sendConnectionRequest
  @Test
  public void sendConnectionRequestSuccess() {
    stubFor(post(urlEqualTo(PodConstants.SENDCONNECTIONREQUEST))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userId\": 1,\r\n" +
                "  \"status\": \"PENDING_OUTGOING\",\r\n" +
                "  \"firstRequestedAt\": 1471018076255,\r\n" +
                "  \"updatedAt\": 1471018076255,\r\n" +
                "  \"requestCounter\": 1\r\n" +
                "}")));

    try {

      assertNotNull(connectionsClient);

      final InboundConnectionRequest connection = connectionsClient.sendConnectionRequest(1L);
      assertNotNull(connection);

      assertEquals(1L, connection.getUserId().longValue());
      assertEquals("PENDING_OUTGOING", connection.getStatus());
      assertEquals(1471018076255L, connection.getFirstRequestedAt().longValue());
      assertEquals(1471018076255L, connection.getUpdatedAt().longValue());
      assertEquals(1L, connection.getRequestCounter().byteValue());

    } catch (final SymClientException e) {
      fail();
    }
  }
  // End sendConnectionRequest

  // getConnectionRequestStatus
  @Test
  public void getConnectionRequestStatusSuccess() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "  \"userId\": 1,\r\n" +
                "  \"status\": \"ACCEPTED\"\r\n" +
                "}")));

    try {

      assertNotNull(connectionsClient);

      final InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);
      assertNotNull(connection);

      assertEquals(1L, connection.getUserId().longValue());
      assertEquals("ACCEPTED", connection.getStatus());

    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void getConnectionRequestStatusFailure400() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);
  }

  @Test(expected = SymClientException.class)
  public void getConnectionRequestStatusFailure401() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void getConnectionRequestStatusFailure403() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void getConnectionRequestStatusFailure500() {
    stubFor(get(urlEqualTo(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    final InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);
  }
  // End getConnectionRequestStatus

  // removeConnection
  @Test
  public void removeConnectionSuccess() {
    stubFor(post(urlEqualTo(PodConstants.REMOVECONNECTION.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{\r\n" +
                "    \"format\": \"TEXT\",\r\n" +
                "    \"message\": \"Connection Removed.\"\r\n" +
                "}")));

    try {

      assertNotNull(connectionsClient);

      connectionsClient.removeConnection(1L);

      assertTrue(true);

    } catch (final SymClientException e) {
      fail();
    }
  }

  @Test(expected = APIClientErrorException.class)
  public void removeConnectionFailure400() {
    stubFor(post(urlEqualTo(PodConstants.REMOVECONNECTION.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    connectionsClient.removeConnection(1L);
  }

  @Test(expected = SymClientException.class)
  public void removeConnectionFailure401() {
    stubFor(post(urlEqualTo(PodConstants.REMOVECONNECTION.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(401)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    connectionsClient.removeConnection(1L);
  }

  @Test(expected = ForbiddenException.class)
  public void removeConnectionFailure403() {
    stubFor(post(urlEqualTo(PodConstants.REMOVECONNECTION.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(403)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    connectionsClient.removeConnection(1L);
  }

  @Test(expected = ServerErrorException.class)
  public void removeConnectionFailure500() {
    stubFor(post(urlEqualTo(PodConstants.REMOVECONNECTION.replace("{userId}", "1")))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
        .willReturn(aResponse()
            .withStatus(500)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .withBody("{}")));

    assertNotNull(connectionsClient);

    connectionsClient.removeConnection(1L);
  }
  // removeConnection

}
