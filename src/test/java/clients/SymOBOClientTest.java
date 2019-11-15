package clients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.ConnectionsClient;
import clients.symphony.api.MessagesClient;
import clients.symphony.api.PresenceClient;
import clients.symphony.api.SignalsClient;
import clients.symphony.api.StreamsClient;
import clients.symphony.api.UsersClient;
import configuration.SymConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.JerseyClient;
import org.junit.Test;

public class SymOBOClientTest {
  @Test
  public void SymOBOClientTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    SymBotAuth symAuth = null;

    // Act
    SymOBOClient symOBOClient = new SymOBOClient(symConfig, symAuth);

    // Assert
    SymConfig config = symOBOClient.getConfig();
    Client podClient = symOBOClient.getPodClient();
    ISymAuth symAuth1 = symOBOClient.getSymAuth();
    Client agentClient = symOBOClient.getAgentClient();
    assertTrue(agentClient instanceof JerseyClient);
    boolean isDefaultSslContextResult = ((JerseyClient) agentClient).isDefaultSslContext();
    ScheduledExecutorService scheduledExecutorService = ((JerseyClient) agentClient).getScheduledExecutorService();
    ExecutorService executorService = ((JerseyClient) agentClient).getExecutorService();
    HostnameVerifier hostnameVerifier = agentClient.getHostnameVerifier();
    boolean isClosedResult = ((JerseyClient) agentClient).isClosed();
    assertTrue(podClient instanceof JerseyClient);
    assertEquals(null, symAuth1);
    boolean isDefaultSslContextResult1 = ((JerseyClient) podClient).isDefaultSslContext();
    ScheduledExecutorService scheduledExecutorService1 = ((JerseyClient) podClient).getScheduledExecutorService();
    ExecutorService executorService1 = ((JerseyClient) podClient).getExecutorService();
    HostnameVerifier hostnameVerifier1 = podClient.getHostnameVerifier();
    assertSame(symConfig, config);
    assertEquals(null, hostnameVerifier1);
    assertEquals(null, executorService1);
    assertEquals(null, scheduledExecutorService1);
    assertTrue(isDefaultSslContextResult1);
    assertEquals(null, scheduledExecutorService);
    assertTrue(isDefaultSslContextResult);
    assertEquals(null, executorService);
    assertEquals(null, hostnameVerifier);
    assertFalse(isClosedResult);
    assertFalse(((JerseyClient) podClient).isClosed());
  }

  @Test
  public void getAgentClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    Client actual = symOBOClient.getAgentClient();

    // Assert
    assertEquals("TLS", (actual.getSslContext()).getProtocol());
  }

  @Test
  public void getConfigTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    SymConfig actual = symOBOClient.getConfig();

    // Assert
    assertEquals(null, actual.getAppCertName());
  }

  @Test
  public void getConnectionsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getConnectionsClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getMessagesClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getMessagesClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getPodClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    Client actual = symOBOClient.getPodClient();

    // Assert
    assertEquals("TLS", (actual.getSslContext()).getProtocol());
  }

  @Test
  public void getPresenceClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getPresenceClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getSignalsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getSignalsClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getStreamsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getStreamsClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getSymAuthTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    ISymAuth actual = symOBOClient.getSymAuth();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUsersClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    symOBOClient.getUsersClient();

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void setAgentClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);
    JerseyClient agentClient = null;

    // Act
    symOBOClient.setAgentClient(agentClient);

    // Assert
    assertEquals(null, symOBOClient.getAgentClient());
  }

  @Test
  public void setPodClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);
    JerseyClient podClient = null;

    // Act
    symOBOClient.setPodClient(podClient);

    // Assert
    assertEquals(null, symOBOClient.getPodClient());
  }
}
