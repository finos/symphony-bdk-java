package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.ConnectionsClient;
import configuration.SymConfig;
import java.util.ArrayList;
import java.util.List;
import model.InboundConnectionRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConnectionsClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void ConnectionsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new ConnectionsClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void acceptConnectionRequestTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.acceptConnectionRequest(userId);
  }

  @Test
  public void getAcceptedConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getAcceptedConnections();
  }

  @Test
  public void getAllConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getAllConnections();
  }

  @Test
  public void getConnectionRequestStatusTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getConnectionRequestStatus(userId);
  }

  @Test
  public void getConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    String status = "aaaaa";
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getConnections(status, arrayList);
  }

  @Test
  public void getInboundPendingConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getInboundPendingConnections();
  }

  @Test
  public void getPendingConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getPendingConnections();
  }

  @Test
  public void getRejectedConnectionsTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.getRejectedConnections();
  }

  @Test
  public void rejectConnectionRequestTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.rejectConnectionRequest(userId);
  }

  @Test
  public void removeConnectionTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.removeConnection(userId);
  }

  @Test
  public void sendConnectionRequestTest() throws Exception {
    // Arrange
    ConnectionsClient connectionsClient = new ConnectionsClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    connectionsClient.sendConnectionRequest(userId);
  }
}
