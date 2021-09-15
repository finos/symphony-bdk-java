package com.symphony.bdk.core.service.connection;

import static com.symphony.bdk.http.api.Pair.pair;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.connection.constant.ConnectionStatus;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.model.UserConnection;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class ConnectionServiceTest {

  private static final String V1_GET_CONNECTION = "/pod/v1/connection/user/{userId}/info";
  private static final String V1_LIST_CONNECTION = "/pod/v1/connection/list";
  private static final String V1_CREATE_CONNECTION = "/pod/v1/connection/create";
  private static final String V1_ACCEPT_CONNECTION = "/pod/v1/connection/accept";
  private static final String V1_REJECT_CONNECTION = "/pod/v1/connection/reject";
  private static final String V1_REMOVE_CONNECTION = "/pod/v1/connection/user/{uid}/remove";

  private ConnectionService service;
  private ConnectionApi spiedConnectionApi;
  private MockApiClient mockApiClient;
  private AuthSession authSession;

  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    this.authSession = mock(AuthSession.class);
    this.spiedConnectionApi = spy(new ConnectionApi(mockApiClient.getApiClient("/pod")));
    this.service = new ConnectionService(this.spiedConnectionApi, authSession, new RetryWithRecoveryBuilder<>());

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void nonOboEndpointShouldThrowExceptionInOboMode() {
    this.service = new ConnectionService(this.spiedConnectionApi, new RetryWithRecoveryBuilder<>());

    assertThrows(IllegalStateException.class, () -> this.service.createConnection(1234L));
  }

  @Test
  void testAcceptConnectionOboMode() {
    this.mockApiClient.onPost(V1_ACCEPT_CONNECTION,
        "{\n"
            + "  \"userId\": 7078106169577,\n"
            + "  \"status\": \"ACCEPTED\",\n"
            + "  \"firstRequestedAt\": 1471046357339,\n"
            + "  \"updatedAt\": 1471046517684,\n"
            + "  \"requestCounter\": 1\n"
            + "}");


    this.service = new ConnectionService(this.spiedConnectionApi, new RetryWithRecoveryBuilder<>());
    UserConnection connection = this.service.obo(this.authSession).acceptConnection(7078106169577L);

    assertEquals(connection.getStatus(), UserConnection.StatusEnum.ACCEPTED);
  }

  @Test
  void getConnectionTest() {
    this.mockApiClient.onGet(V1_GET_CONNECTION.replace("{userId}", "769658112378"),
        "{\n"
            + "  \"userId\": 769658112378,\n"
            + "  \"status\": \"ACCEPTED\"\n"
            + "}");

    UserConnection userConnection = this.service.getConnection(769658112378L);

    assertEquals(userConnection.getUserId(), 769658112378L);
    assertEquals(userConnection.getStatus(), UserConnection.StatusEnum.ACCEPTED);
  }

  @Test
  void getConnectionFailed() {
    this.mockApiClient.onGet(400, V1_GET_CONNECTION.replace("{userId}", "769658112378"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.getConnection(769658112378L));
  }

  @Test
  void listConnectionsTest() {
    this.mockApiClient.onGet(V1_LIST_CONNECTION,
          asList(
              pair("status", "ALL"),
              pair("userIds", "7078106126503,7078106103809")
          ),
        "[\n"
            + "  {\n"
            + "    \"userId\": 7078106126503,\n"
            + "    \"status\": \"PENDING_OUTGOING\",\n"
            + "    \"updatedAt\": 1471018076255\n"
            + "  },\n"
            + "  {\n"
            + "    \"userId\": 7078106103809,\n"
            + "    \"status\": \"PENDING_INCOMING\",\n"
            + "    \"updatedAt\": 1467562406219\n"
            + "  }\n"
            + "]");

    List<UserConnection> connections = this.service.listConnections(ConnectionStatus.ALL, asList(7078106126503L, 7078106103809L));

    assertEquals(connections.size(), 2);
    assertEquals(connections.get(0).getStatus(), UserConnection.StatusEnum.PENDING_OUTGOING);
    assertEquals(connections.get(0).getUserId(), 7078106126503L);
    assertEquals(connections.get(1).getStatus(), UserConnection.StatusEnum.PENDING_INCOMING);
    assertEquals(connections.get(1).getUserId(), 7078106103809L);
  }

  @Test
  void listConnectionsWithNullStatusTest() {
    this.mockApiClient.onGet(V1_LIST_CONNECTION,
        Collections.singletonList(pair("userIds", "7078106126503,7078106103809")),
        "[\n"
            + "  {\n"
            + "    \"userId\": 7078106126503,\n"
            + "    \"status\": \"PENDING_OUTGOING\",\n"
            + "    \"updatedAt\": 1471018076255\n"
            + "  },\n"
            + "  {\n"
            + "    \"userId\": 7078106103809,\n"
            + "    \"status\": \"PENDING_INCOMING\",\n"
            + "    \"updatedAt\": 1467562406219\n"
            + "  }\n"
            + "]");

    List<UserConnection> connections = this.service.listConnections(null, asList(7078106126503L, 7078106103809L));

    assertEquals(connections.size(), 2);
    assertEquals(connections.get(0).getStatus(), UserConnection.StatusEnum.PENDING_OUTGOING);
    assertEquals(connections.get(0).getUserId(), 7078106126503L);
    assertEquals(connections.get(1).getStatus(), UserConnection.StatusEnum.PENDING_INCOMING);
    assertEquals(connections.get(1).getUserId(), 7078106103809L);
  }

  @Test
  void listConnectionsWithNullListOfUsersTest() {
    this.mockApiClient.onGet(V1_LIST_CONNECTION,
        Collections.singletonList(pair("status", "PENDING_OUTGOING")),
        "[\n"
            + "  {\n"
            + "    \"userId\": 7078106126503,\n"
            + "    \"status\": \"PENDING_OUTGOING\",\n"
            + "    \"updatedAt\": 1471018076255\n"
            + "  },\n"
            + "  {\n"
            + "    \"userId\": 7078106103809,\n"
            + "    \"status\": \"PENDING_INCOMING\",\n"
            + "    \"updatedAt\": 1467562406219\n"
            + "  }\n"
            + "]");

    List<UserConnection> connections = this.service.listConnections(ConnectionStatus.PENDING_OUTGOING, null);

    assertEquals(connections.size(), 2);
    assertEquals(connections.get(0).getStatus(), UserConnection.StatusEnum.PENDING_OUTGOING);
    assertEquals(connections.get(0).getUserId(), 7078106126503L);
    assertEquals(connections.get(1).getStatus(), UserConnection.StatusEnum.PENDING_INCOMING);
    assertEquals(connections.get(1).getUserId(), 7078106103809L);
  }

  @Test
  void listConnectionsFailed() {
    this.mockApiClient.onGet(400, V1_LIST_CONNECTION, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.listConnections(ConnectionStatus.ALL, asList(7078106126503L, 7078106103809L)));
  }

  @Test
  void createConnectionTest() {
    this.mockApiClient.onPost(V1_CREATE_CONNECTION,
        "{\n"
            + "  \"userId\": 7078106126503,\n"
            + "  \"status\": \"PENDING_OUTGOING\",\n"
            + "  \"firstRequestedAt\": 1471018076255,\n"
            + "  \"updatedAt\": 1471018076255,\n"
            + "  \"requestCounter\": 1\n"
            + "}");

    UserConnection connection = this.service.createConnection(7078106126503L);

    assertEquals(connection.getUserId(), 7078106126503L);
    assertEquals(connection.getStatus(), UserConnection.StatusEnum.PENDING_OUTGOING);
    assertEquals(connection.getFirstRequestedAt(), 1471018076255L);
  }

  @Test
  void createConnectionFailed() {
    this.mockApiClient.onPost(400, V1_CREATE_CONNECTION, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.createConnection(7078106126503L));
  }

  @Test
  void acceptConnectionTest() {
    this.mockApiClient.onPost(V1_ACCEPT_CONNECTION,
        "{\n"
            + "  \"userId\": 7078106169577,\n"
            + "  \"status\": \"ACCEPTED\",\n"
            + "  \"firstRequestedAt\": 1471046357339,\n"
            + "  \"updatedAt\": 1471046517684,\n"
            + "  \"requestCounter\": 1\n"
            + "}");

    UserConnection connection = this.service.acceptConnection(7078106169577L);

    assertEquals(connection.getStatus(), UserConnection.StatusEnum.ACCEPTED);
    assertEquals(connection.getUserId(), 7078106169577L);
    assertEquals(connection.getUpdatedAt(), 1471046517684L);
  }

  @Test
  void acceptConnectionFailed() {
    this.mockApiClient.onPost(400, V1_ACCEPT_CONNECTION, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.acceptConnection(7078106169577L));
  }

  @Test
  void rejectConnectionTest() {
    this.mockApiClient.onPost(V1_REJECT_CONNECTION,
        "{\n"
            + "  \"userId\": 7215545059385,\n"
            + "  \"status\": \"REJECTED\",\n"
            + "  \"firstRequestedAt\": 1471044955409,\n"
            + "  \"updatedAt\": 1471045390420,\n"
            + "  \"requestCounter\": 1\n"
            + "}");

    UserConnection connection = this.service.rejectConnection(7215545059385L);

    assertEquals(connection.getStatus(), UserConnection.StatusEnum.REJECTED);
    assertEquals(connection.getUserId(), 7215545059385L);
    assertEquals(connection.getRequestCounter(), 1);
  }

  @Test
  void rejectConnectionFailed() {
    this.mockApiClient.onPost(400, V1_REJECT_CONNECTION, "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.rejectConnection(7215545059385L));
  }

  @Test
  void removeConnectionTest() throws ApiException {
    this.mockApiClient.onPost(V1_REMOVE_CONNECTION.replace("{uid}", "7215545059385"), "{}");

    this.service.removeConnection(7215545059385L);

    verify(spiedConnectionApi).v1ConnectionUserUidRemovePost("1234", 7215545059385L);
  }

  @Test
  void removeConnectionFailed() {
    this.mockApiClient.onPost(400, V1_REMOVE_CONNECTION.replace("{uid}", "7215545059385"), "{}");

    assertThrows(ApiRuntimeException.class, () -> this.service.removeConnection(7215545059385L));
  }
}
