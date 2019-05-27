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
import clients.symphony.api.ConnectionsClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
import model.InboundConnectionRequest;

public class ConnectionsClientTest extends BotTest {
  private ConnectionsClient connectionsClient;

  @Before
  public void initClient() {
    connectionsClient = new ConnectionsClient(symBotClient);
  }

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

    List<InboundConnectionRequest> connections = connectionsClient.getConnections(null, null);

    assertNotNull(connections);
    assertEquals(2, connections.size());
  }

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

    InboundConnectionRequest connection = connectionsClient.acceptConnectionRequest(1L);

    assertNotNull(connection);
    assertEquals(1L, connection.getUserId().longValue());
  }

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

    InboundConnectionRequest connection = connectionsClient.rejectConnectionRequest(1L);

    assertNotNull(connection);
    assertEquals(1L, connection.getUserId().longValue());
  }

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

    InboundConnectionRequest connection = connectionsClient.sendConnectionRequest(1L);

    assertNotNull(connection);
    assertEquals(1L, connection.getUserId().longValue());
  }

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

    InboundConnectionRequest connection = connectionsClient.getConnectionRequestStatus(1L);

    assertNotNull(connection);
    assertEquals(1L, connection.getUserId().longValue());
  }

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

    connectionsClient.removeConnection(1L);
  }

}
