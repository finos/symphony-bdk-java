package configuration;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class SymConfigLoaderTest {

  @Test
  public void testLoadFromFile(){
    final SymConfig symConfig = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    assertNotNull(symConfig);
    verifySymConfig(symConfig);
  }

  @Test
  public void testLoad() throws IOException {
    final InputStream inputStream = new FileInputStream("src/test/resources/sym-config.json");
    final SymConfig symConfig = SymConfigLoader.load(inputStream);
    assertNotNull(symConfig);
    verifySymConfig(symConfig);
  }

  @Test
  public void testLoadLoadBalancerFromFile(){
    final SymLoadBalancedConfig symConfig = SymConfigLoader.loadLoadBalancerFromFile("src/test/resources/symloadbalancedconfig.json");
    assertNotNull(symConfig);
    verifySymBalancedConfig(symConfig);
  }

  @Test
  public void testLoadLoadBalancer() throws IOException{
    final InputStream inputStream = new FileInputStream("src/test/resources/symloadbalancedconfig.json");
    final SymLoadBalancedConfig symConfig = SymConfigLoader.loadLoadBalancer(inputStream);
    assertNotNull(symConfig);
    verifySymBalancedConfig(symConfig);
  }

  @Test
  public void testLoadConfig() {
    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");
    assertNotNull(symConfig);
    verifySymConfig(symConfig);
  }

  @Test
  public void testLoadLoadBalancerConfig() {
    final SymLoadBalancedConfig symConfig = SymConfigLoader.loadLoadBalancerConfig("src/test/resources/symloadbalancedconfig.json");
    assertNotNull(symConfig);
    verifySymBalancedConfig(symConfig);
  }

  private void verifySymBalancedConfig(final SymLoadBalancedConfig symConfig) {
    final LoadBalancing loadBalancing = symConfig.getLoadBalancing();
    assertNotNull(loadBalancing);
    assertEquals("random", loadBalancing.getMethod().toString());
    assertTrue(loadBalancing.isStickySessions());

    final List<String> agentServers  = symConfig.getAgentServers();
    assertNotNull(agentServers);
    assertEquals(2, agentServers.size());
    assertEquals("server1", agentServers.get(0));
    assertEquals("server2", agentServers.get(1));

    assertEquals(-1, symConfig.getCurrentAgentIndex());
    assertEquals("localhost", symConfig.getActualAgentHost());
    assertEquals(4345, symConfig.getActualAgentPort());
  }

  private void verifySymConfig(final SymConfig symConfig) {
    assertEquals("localhost", symConfig.getSessionAuthHost());
    assertEquals(7443, symConfig.getSessionAuthPort());
    assertEquals("test1", symConfig.getSessionAuthContextPath());
    assertEquals("localhost", symConfig.getKeyAuthHost());
    assertEquals(7443, symConfig.getKeyAuthPort());
    assertEquals("src/test/resources/", symConfig.getKeyAuthContextPath());
    assertEquals("key.manager.proxy.url", symConfig.getKeyManagerProxyURL());
    assertEquals("test_un", symConfig.getKeyManagerProxyUsername());
    assertEquals("test_pw", symConfig.getKeyManagerProxyPassword());
    assertEquals("localhost", symConfig.getPodHost());
    assertEquals(7443, symConfig.getPodPort());
    assertEquals("src/test/resources/", symConfig.getPodContextPath());
    assertEquals("pod.proxy.url", symConfig.getPodProxyURL());
    assertEquals("pod_un", symConfig.getPodProxyUsername());
    assertEquals("pod.proxy.pw", symConfig.getPodProxyPassword());
    assertEquals("localhost", symConfig.getAgentHost());
    assertEquals(7443, symConfig.getAgentPort());
    assertEquals("src", symConfig.getAgentContextPath());
    assertEquals("agent.proxy.url", symConfig.getAgentProxyURL());
    assertEquals("agent_un", symConfig.getAgentProxyUsername());
    assertEquals("agent_pw", symConfig.getAgentProxyPassword());
    assertEquals("bot", symConfig.getBotUsername());
    assertEquals("bot@symphony.com", symConfig.getBotEmailAddress());
    assertEquals("src/test/resources/", symConfig.getBotPrivateKeyPath());
    assertEquals("private-key.pem", symConfig.getBotPrivateKeyName());
    assertEquals("testapp", symConfig.getAppId());
    assertEquals("src/test/resources/", symConfig.getAppPrivateKeyPath());
    assertEquals("testprivatekey.pkcs8", symConfig.getAppPrivateKeyName());
    assertEquals("src/test/resources/", symConfig.getAppCertPath());
    assertEquals("testcertificate.crt", symConfig.getAppCertName());
    assertEquals("etttty", symConfig.getAppCertPassword());
    assertEquals(5000, symConfig.getReadTimeout());
    assertEquals(1000, symConfig.getConnectionTimeout());
    assertEquals("src/test/resources/testkeystore.jks", symConfig.getTruststorePath());
    assertEquals("123456", symConfig.getTruststorePassword());
    assertEquals("2.0", symConfig.getDatafeedVersion());
    assertEquals(4, symConfig.getDatafeedEventsThreadpoolSize());
    assertEquals(6, symConfig.getDatafeedEventsErrorTimeout());
    assertTrue(symConfig.getReuseDatafeedID());
    assertEquals("src/test/resources/testdatafeed.id"+ File.separator, symConfig.getDatafeedIdFilePath());
    assertEquals("auth", symConfig.getAuthenticationFilterUrlPattern());
    assertFalse(symConfig.isShowFirehoseErrors());

    final List<String> supportedUriSchemes = symConfig.getSupportedUriSchemes();
    assertNotNull(supportedUriSchemes);
    assertEquals(3, supportedUriSchemes.size());
    assertTrue(supportedUriSchemes.contains("http"));
    assertTrue(supportedUriSchemes.contains("https"));
    assertTrue(supportedUriSchemes.contains("ftp"));

    final RetryConfiguration retryConfiguration = symConfig.getRetry();
    assertNotNull(retryConfiguration);
    assertEquals(2, retryConfiguration.getMaxAttempts());
    assertEquals(200, retryConfiguration.getInitialIntervalMillis());
    assertEquals(1.5, retryConfiguration.getMultiplier(), 0);
  }
}
