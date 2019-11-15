package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import configuration.SymConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.HttpClientBuilderHelper;

public class HttpClientBuilderHelperTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void getAgentClientConfigTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    ClientConfig actual = HttpClientBuilderHelper.getAgentClientConfig(symConfig);

    // Assert
    ScheduledExecutorService scheduledExecutorService = actual.getScheduledExecutorService();
    RuntimeType runtimeType = actual.getRuntimeType();
    JerseyClient client = actual.getClient();
    Connector connector = actual.getConnector();
    assertEquals(null, scheduledExecutorService);
    assertTrue(actual.getConnectorProvider() instanceof org.glassfish.jersey.client.HttpUrlConnectorProvider);
    assertEquals(null, connector);
    assertEquals(null, client);
    assertEquals(RuntimeType.CLIENT, runtimeType);
    assertEquals(35000, symConfig.getConnectionTimeout());
  }

  @Test
  public void getHttpClientAppBuilderTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    HttpClientBuilderHelper.getHttpClientAppBuilder(config);
  }

  @Test
  public void getHttpClientBotBuilderTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    HttpClientBuilderHelper.getHttpClientBotBuilder(config);
  }

  @Test
  public void getKMClientConfigTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    ClientConfig actual = HttpClientBuilderHelper.getKMClientConfig(symConfig);

    // Assert
    ScheduledExecutorService scheduledExecutorService = actual.getScheduledExecutorService();
    RuntimeType runtimeType = actual.getRuntimeType();
    JerseyClient client = actual.getClient();
    Connector connector = actual.getConnector();
    assertEquals(null, scheduledExecutorService);
    assertTrue(actual.getConnectorProvider() instanceof org.glassfish.jersey.client.HttpUrlConnectorProvider);
    assertEquals(null, connector);
    assertEquals(null, client);
    assertEquals(RuntimeType.CLIENT, runtimeType);
    assertEquals(35000, symConfig.getConnectionTimeout());
  }

  @Test
  public void getPodClientConfigTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    ClientConfig actual = HttpClientBuilderHelper.getPodClientConfig(symConfig);

    // Assert
    ScheduledExecutorService scheduledExecutorService = actual.getScheduledExecutorService();
    RuntimeType runtimeType = actual.getRuntimeType();
    JerseyClient client = actual.getClient();
    Connector connector = actual.getConnector();
    assertEquals(null, scheduledExecutorService);
    assertTrue(actual.getConnectorProvider() instanceof org.glassfish.jersey.client.HttpUrlConnectorProvider);
    assertEquals(null, connector);
    assertEquals(null, client);
    assertEquals(RuntimeType.CLIENT, runtimeType);
    assertEquals(35000, symConfig.getConnectionTimeout());
  }
}
