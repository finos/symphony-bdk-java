package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingMode;
import com.symphony.bdk.core.config.model.BdkServerConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

class BdkConfigLoaderTest {
  private final static String YAML_CONFIG_PATH = "/config/config.yaml";
  private final static String JSON_CONFIG_PATH = "/config/config.json";


  @Test
  void loadFromYamlInputStreamTest() throws BdkConfigException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(YAML_CONFIG_PATH);
    BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream, YAML_CONFIG_PATH);
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromJsonInputStreamTest() throws BdkConfigException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(JSON_CONFIG_PATH);
    BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream, JSON_CONFIG_PATH);
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromYamlFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(YAML_CONFIG_PATH);
    Path configPath = tempDir.resolve("config.yaml");
    Files.copy(inputStream, configPath);
    BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromJsonFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(JSON_CONFIG_PATH);
    Path configPath = tempDir.resolve("config.json");
    Files.copy(inputStream, configPath);
    BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromJsonClasspathTest() throws BdkConfigException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath(JSON_CONFIG_PATH);
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromYamlClasspathTest() throws BdkConfigException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath(YAML_CONFIG_PATH);
    assertEquals(config.getBot().getUsername(), "tibot");
  }

  @Test
  void loadFromFileNotFoundTest() {
    BdkConfigException exception = assertThrows(BdkConfigException.class, () -> {
      String configPath = "/wrong_path/config.yaml";
      BdkConfigLoader.loadFromFile(configPath);
    });
    assertEquals(exception.getMessage(), "Config file has not been found");
  }

  @Test
  void loadLegacyFromInputStreamTest() throws BdkConfigException {
    String configPath = "/config/legacy_config.json";
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream, configPath);
    assertEquals(config.getBot().getUsername(), "tibot");
    assertEquals(config.getBot().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
    assertEquals(config.getApp().getPrivateKeyPath(), "/Users/local/conf/agent/privatekey.pem");
  }

  @Test
  void loadFromClasspathNotFoundTest() {
    BdkConfigException exception = assertThrows(BdkConfigException.class,
        () -> BdkConfigLoader.loadFromClasspath("/wrong_classpath/config.yaml"));
    assertEquals(exception.getMessage(), "Config file is not found");
  }

  @Test
  void loadClientGlobalConfig() throws BdkConfigException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_client_global.yaml");

    assertEquals(config.getPod().getScheme(), "https");
    assertEquals(config.getPod().getHost(), "diff-pod.symphony.com");
    assertEquals(config.getPod().getPort(), 8443);
    assertEquals(config.getPod().getContext(), "context");
    assertEquals(config.getPod().getConnectionTimeout(), 10000);
    assertEquals(config.getPod().getReadTimeout(), 30000);
    assertEquals(config.getPod().getConnectionPoolMax(), 20);
    assertEquals(config.getPod().getConnectionPoolPerRoute(), 10);
    assertEquals("Keep-Alive", config.getPod().getDefaultHeaders().get("Connection"));
    assertEquals("close", config.getPod().getDefaultHeaders().get("Keep-Alive"));

    assertEquals(config.getScheme(), "https");
    assertEquals("Keep-Alive", config.getDefaultHeaders().get("Connection"));
    assertEquals("timeout=5, max=1000", config.getDefaultHeaders().get("Keep-Alive"));

    assertEquals(config.getAgent().getScheme(), "https");
    assertEquals(config.getAgent().getHost(), "devx1.symphony.com");
    assertEquals(config.getAgent().getPort(), 443);
    assertEquals(config.getAgent().getFormattedContext(), "/context");
    assertEquals(config.getAgent().getConnectionTimeout(), 20000);
    assertEquals(config.getAgent().getReadTimeout(), 60000);
    assertEquals(config.getAgent().getConnectionPoolMax(), 30);
    assertEquals(config.getAgent().getConnectionPoolPerRoute(), 20);
    assertEquals("Keep-Alive", config.getAgent().getDefaultHeaders().get("Connection"));
    assertEquals("timeout=5, max=1000", config.getAgent().getDefaultHeaders().get("Keep-Alive"));

    assertEquals(config.getKeyManager().getScheme(), "https");
    assertEquals(config.getKeyManager().getHost(), "devx1.symphony.com");
    assertEquals(config.getKeyManager().getPort(), 8443);
    assertEquals(config.getKeyManager().getFormattedContext(), "/diff-context");
    assertEquals(config.getKeyManager().getConnectionTimeout(), 10000);
    assertEquals(config.getKeyManager().getReadTimeout(), 30000);
    assertEquals(config.getKeyManager().getConnectionPoolMax(), 20);
    assertEquals(config.getKeyManager().getConnectionPoolPerRoute(), 10);
    assertEquals("Keep-Alive", config.getKeyManager().getDefaultHeaders().get("Connection"));
    assertEquals("timeout=5, max=1000", config.getKeyManager().getDefaultHeaders().get("Keep-Alive"));

    assertEquals(config.getSessionAuth().getScheme(), "http");
    assertEquals(config.getSessionAuth().getHost(), "devx1.symphony.com");
    assertEquals(config.getSessionAuth().getPort(), 8443);
    assertEquals(config.getSessionAuth().getContext(), "context");
    assertEquals(config.getSessionAuth().getConnectionTimeout(), 10000);
    assertEquals(config.getSessionAuth().getReadTimeout(), 30000);
    assertEquals(config.getSessionAuth().getConnectionPoolMax(), 20);
    assertEquals(config.getSessionAuth().getConnectionPoolPerRoute(), 10);
    assertEquals("Keep-Alive", config.getSessionAuth().getDefaultHeaders().get("Connection"));
    assertEquals("timeout=5, max=1000", config.getSessionAuth().getDefaultHeaders().get("Keep-Alive"));
  }


  @Test
  void parseLbAgentField() throws BdkConfigException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();
    final List<BdkServerConfig> nodes = agentLoadBalancing.getNodes();

    assertEquals(BdkLoadBalancingMode.RANDOM, agentLoadBalancing.getMode());
    assertFalse(agentLoadBalancing.isStickiness());
    assertEquals(2, nodes.size());

    assertEquals("http", nodes.get(0).getScheme());
    assertEquals("agent1.acme.org", nodes.get(0).getHost());
    assertEquals(8443, nodes.get(0).getPort());
    assertEquals("/app", nodes.get(0).getContext());

    assertEquals("https", nodes.get(1).getScheme());
    assertEquals("agent2.acme.org", nodes.get(1).getHost());
    assertEquals(443, nodes.get(1).getPort());
    assertEquals("", nodes.get(1).getContext());
  }

  @Test
  void parseLbAgentFieldsWithNoDefinedStickiness() throws BdkConfigException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_no_stickiness.yaml");
    assertTrue(config.getAgent().getLoadBalancing().isStickiness());
  }

  @Test
  void parseLbAgentFieldsWithRoundRobinMode() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_round_robin.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();

    assertTrue(agentLoadBalancing.isStickiness());
    assertEquals(BdkLoadBalancingMode.ROUND_ROBIN, agentLoadBalancing.getMode());
  }

  @Test
  void parseLbAgentFieldsWithExternalMode() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_external.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();

    assertEquals(BdkLoadBalancingMode.EXTERNAL, agentLoadBalancing.getMode());
  }

  @Test
  void parseLbAgentFieldsWithInvalidMode() {
    final BdkConfigException bdkConfigException = assertThrows(BdkConfigException.class,
        () -> BdkConfigLoader.loadFromClasspath("/config/config_invalid_mode.yaml"));

    assertEquals("Config file is not found", bdkConfigException.getMessage());
  }

  @Test
  @DisabledIfEnvironmentVariable(named = "CIRCLECI", matches = "true")
    // cf. https://circleci.com/docs/2.0/env-vars/#built-in-environment-variables
  void testLoadConfigFromSymphonyDirectory() throws Exception {

    final String tmpConfigFileName = UUID.randomUUID().toString() + "-config.yaml";
    final Path tmpConfigPath = Paths.get(System.getProperty("user.home"), ".symphony", tmpConfigFileName);
    FileUtils.forceMkdirParent(tmpConfigPath.toFile());
    final InputStream configInputStream = this.getClass().getResourceAsStream("/config/config.yaml");
    IOUtils.copy(configInputStream, new FileOutputStream(tmpConfigPath.toFile()));

    final BdkConfig config = BdkConfigLoader.loadFromSymphonyDir(tmpConfigFileName);
    assertNotNull(config);

    Files.delete(tmpConfigPath);
  }
}
