package com.symphony.bdk.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    final InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(YAML_CONFIG_PATH);
    final BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromJsonInputStreamTest() throws BdkConfigException {
    final InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(JSON_CONFIG_PATH);
    final BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromYamlFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
    final InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(YAML_CONFIG_PATH);
    final Path configPath = tempDir.resolve("config.yaml");
    Files.copy(inputStream, configPath);
    final BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromJsonFileTest(@TempDir Path tempDir) throws BdkConfigException, IOException {
    final InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(JSON_CONFIG_PATH);
    final Path configPath = tempDir.resolve("config.json");
    Files.copy(inputStream, configPath);
    final BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromJsonClasspathTest() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath(JSON_CONFIG_PATH);
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromYamlClasspathTest() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath(YAML_CONFIG_PATH);
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
  }

  @Test
  void loadFromFileNotFoundTest() {
    assertThatThrownBy(() -> BdkConfigLoader.loadFromFile("/wrong_path/config.yaml"))
        .isInstanceOf(BdkConfigException.class)
        .hasMessage("Config file has not been found from location: /wrong_path/config.yaml");
  }

  @Test
  void loadLegacyFromInputStreamTest() throws BdkConfigException {
    final String configPath = "/config/legacy_config.json";
    final InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream(configPath);
    final BdkConfig config = BdkConfigLoader.loadFromInputStream(inputStream);
    assertThat(config.getBot().getUsername()).isEqualTo("tibot");
    assertThat(config.getBot().getPrivateKeyPath()).isEqualTo("/Users/local/conf/agent/privatekey.pem");
    assertThat(config.getApp().getPrivateKeyPath()).isEqualTo("/Users/local/conf/agent/privatekey.pem");
  }

  @Test
  void loadFromClasspathNotFoundTest() {
    assertThatThrownBy(() -> BdkConfigLoader.loadFromClasspath("/wrong_classpath/config.yaml"))
        .isInstanceOf(BdkConfigException.class)
        .hasMessage("Config file has not found from classpath location: /wrong_classpath/config.yaml");
  }

  @Test
  void loadClientGlobalConfig() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_client_global.yaml");

    assertThat(config.getScheme()).isEqualTo("https");
    assertThat(config.getDefaultHeaders().get("Connection")).isEqualTo("Keep-Alive");
    assertThat(config.getDefaultHeaders().get("Keep-Alive")).isEqualTo("timeout=5, max=1000");

    assertThat(config.getPod().getScheme()).isEqualTo("https");
    assertThat(config.getPod().getHost()).isEqualTo("diff-pod.symphony.com");
    assertThat(config.getPod().getPort()).isEqualTo(8443);
    assertThat(config.getPod().getContext()).isEqualTo("context");
    assertThat(config.getPod().getConnectionTimeout()).isEqualTo(10000);
    assertThat(config.getPod().getReadTimeout()).isEqualTo(30000);
    assertThat(config.getPod().getConnectionPoolMax()).isEqualTo(20);
    assertThat(config.getPod().getConnectionPoolPerRoute()).isEqualTo(10);
    assertThat(config.getPod().getDefaultHeaders().get("Connection")).isEqualTo("Keep-Alive");
    assertThat(config.getPod().getDefaultHeaders().get("Keep-Alive")).isEqualTo("close");

    assertThat(config.getAgent().getScheme()).isEqualTo("https");
    assertThat(config.getAgent().getHost()).isEqualTo("devx1.symphony.com");
    assertThat(config.getAgent().getPort()).isEqualTo(443);
    assertThat(config.getAgent().getFormattedContext()).isEqualTo("/context");
    assertThat(config.getAgent().getConnectionTimeout()).isEqualTo(20000);
    assertThat(config.getAgent().getReadTimeout()).isEqualTo(60000);
    assertThat(config.getAgent().getConnectionPoolMax()).isEqualTo(30);
    assertThat(config.getAgent().getConnectionPoolPerRoute()).isEqualTo(20);
    assertThat(config.getAgent().getDefaultHeaders().get("Connection")).isEqualTo("Keep-Alive");
    assertThat(config.getAgent().getDefaultHeaders().get("Keep-Alive")).isEqualTo("timeout=5, max=1000");

    assertThat(config.getKeyManager().getScheme()).isEqualTo("https");
    assertThat(config.getKeyManager().getHost()).isEqualTo("devx1.symphony.com");
    assertThat(config.getKeyManager().getPort()).isEqualTo(8443);
    assertThat(config.getKeyManager().getFormattedContext()).isEqualTo("/diff-context");
    assertThat(config.getKeyManager().getConnectionTimeout()).isEqualTo(10000);
    assertThat(config.getKeyManager().getReadTimeout()).isEqualTo(30000);
    assertThat(config.getKeyManager().getConnectionPoolMax()).isEqualTo(20);
    assertThat(config.getKeyManager().getConnectionPoolPerRoute()).isEqualTo(10);
    assertThat(config.getKeyManager().getDefaultHeaders().get("Connection")).isEqualTo("Keep-Alive");
    assertThat(config.getKeyManager().getDefaultHeaders().get("Keep-Alive")).isEqualTo("timeout=5, max=1000");

    assertThat(config.getSessionAuth().getScheme()).isEqualTo("http");
    assertThat(config.getSessionAuth().getHost()).isEqualTo("devx1.symphony.com");
    assertThat(config.getSessionAuth().getPort()).isEqualTo(8443);
    assertThat(config.getSessionAuth().getContext()).isEqualTo("context");
    assertThat(config.getSessionAuth().getConnectionTimeout()).isEqualTo(10000);
    assertThat(config.getSessionAuth().getReadTimeout()).isEqualTo(30000);
    assertThat(config.getSessionAuth().getConnectionPoolMax()).isEqualTo(20);
    assertThat(config.getSessionAuth().getConnectionPoolPerRoute()).isEqualTo(10);
    assertThat(config.getSessionAuth().getDefaultHeaders().get("Connection")).isEqualTo("Keep-Alive");
    assertThat(config.getSessionAuth().getDefaultHeaders().get("Keep-Alive")).isEqualTo("timeout=5, max=1000");
  }

  @Test
  void parseLbAgentField() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();
    final List<BdkServerConfig> nodes = agentLoadBalancing.getNodes();

    assertThat(agentLoadBalancing.getMode()).isEqualTo(BdkLoadBalancingMode.RANDOM);
    assertThat(agentLoadBalancing.isStickiness()).isFalse();
    assertThat(nodes.size()).isEqualTo(2);

    assertThat(nodes.get(0).getScheme()).isEqualTo("http");
    assertThat(nodes.get(0).getHost()).isEqualTo("agent1.acme.org");
    assertThat(nodes.get(0).getPort()).isEqualTo(8443);
    assertThat(nodes.get(0).getContext()).isEqualTo("/app");

    assertThat(nodes.get(1).getScheme()).isEqualTo("https");
    assertThat(nodes.get(1).getHost()).isEqualTo("agent2.acme.org");
    assertThat(nodes.get(1).getPort()).isEqualTo(443);
    assertThat(nodes.get(1).getContext()).isEmpty();
  }

  @Test
  void parseLbAgentFieldsWithNoDefinedStickiness() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_no_stickiness.yaml");
    assertThat(config.getAgent().getLoadBalancing().isStickiness()).isTrue();
  }

  @Test
  void parseLbAgentFieldsWithRoundRobinMode() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_round_robin.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();

    assertThat(agentLoadBalancing.isStickiness()).isTrue();
    assertThat(agentLoadBalancing.getMode()).isEqualTo(BdkLoadBalancingMode.ROUND_ROBIN);
  }

  @Test
  void parseLbAgentFieldsWithExternalMode() throws BdkConfigException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config_lb_external.yaml");
    final BdkLoadBalancingConfig agentLoadBalancing = config.getAgent().getLoadBalancing();

    assertThat(agentLoadBalancing.getMode()).isEqualTo(BdkLoadBalancingMode.EXTERNAL);
  }

  @Test
  void parseLbAgentFieldsWithInvalidMode() {
    assertThatThrownBy(() -> BdkConfigLoader.loadFromClasspath("/config/config_invalid_mode.yaml"))
        .isInstanceOf(BdkConfigException.class)
        .hasMessage("Config file has not found from classpath location: /config/config_invalid_mode.yaml");
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
    assertThat(config).isNotNull();

    Files.delete(tmpConfigPath);
  }
}
