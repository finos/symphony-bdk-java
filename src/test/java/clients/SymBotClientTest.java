package clients;

import static org.junit.Assert.assertEquals;
import authentication.SymBotAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymBotClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void clearBotClientTest() throws Exception {
    // Arrange and Act
    SymBotClient.clearBotClient();
  }

  @Test
  public void getBotClientTest() throws Exception {
    // Arrange and Act
    SymBotClient actual = SymBotClient.getBotClient();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void initBotLoadBalancedRsaTest() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    String lbConfigPath = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.<SymConfig>initBotLoadBalancedRsa(configPath, lbConfigPath, clazz);
  }

  @Test
  public void initBotLoadBalancedRsaTest2() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    String lbConfigPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.initBotLoadBalancedRsa(configPath, lbConfigPath);
  }

  @Test
  public void initBotLoadBalancedTest() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    String lbConfigPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.initBotLoadBalanced(configPath, lbConfigPath);
  }

  @Test
  public void initBotLoadBalancedTest2() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    String lbConfigPath = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.<SymConfig>initBotLoadBalanced(configPath, lbConfigPath, clazz);
  }

  @Test
  public void initBotRsaTest() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.<SymConfig>initBotRsa(configPath, clazz);
  }

  @Test
  public void initBotRsaTest2() throws Exception {
    // Arrange
    String configPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.initBotRsa(configPath);
  }

  @Test
  public void initBotTest() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    SymBotAuth botAuth = null;
    ClientConfig podClientConfig = new ClientConfig();
    ClientConfig agentClientConfig = new ClientConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    SymBotClient.initBot(config, botAuth, podClientConfig, agentClientConfig);
  }

  @Test
  public void initBotTest2() throws Exception {
    // Arrange
    String configPath = "aaaaa";
    Class<SymConfig> clazz = null;

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.<SymConfig>initBot(configPath, clazz);
  }

  @Test
  public void initBotTest3() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    SymBotAuth botAuth = null;
    ClientConfig podClientConfig = new ClientConfig();
    ClientConfig agentClientConfig = new ClientConfig();
    SymLoadBalancedConfig lbConfig = new SymLoadBalancedConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    SymBotClient.initBot(config, botAuth, podClientConfig, agentClientConfig, lbConfig);
  }

  @Test
  public void initBotTest4() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    SymBotAuth botAuth = null;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    SymBotClient.initBot(config, botAuth);
  }

  @Test
  public void initBotTest5() throws Exception {
    // Arrange
    SymConfig config = new SymConfig();
    SymBotAuth botAuth = null;
    SymLoadBalancedConfig lbConfig = new SymLoadBalancedConfig();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    SymBotClient.initBot(config, botAuth, lbConfig);
  }

  @Test
  public void initBotTest6() throws Exception {
    // Arrange
    String configPath = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymBotClient.initBot(configPath);
  }
}
