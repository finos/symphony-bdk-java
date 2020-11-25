package com.symphony.bdk.core.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.client.loadbalancing.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.core.client.loadbalancing.RegularLoadBalancedApiClient;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingMode;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.jersey2.ApiClientJersey2;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * Test class for the {@link ApiClientFactory}.
 */
class ApiClientFactoryTest {

  private final ApiClientFactory factory = new ApiClientFactory(this.createConfig());

  @Test
  void testGetLoginClient() {
    final ApiClient loginClient = this.factory.getLoginClient();
    assertEquals(ApiClientJersey2.class, loginClient.getClass());
    assertEquals("https://pod-host:443/login", loginClient.getBasePath());
  }

  @Test
  void testGetRelayClient() {
    final ApiClient relayClient = this.factory.getRelayClient();
    assertEquals(ApiClientJersey2.class, relayClient.getClass());
    assertEquals("https://km-host:443/relay", relayClient.getBasePath());
  }

  @Test
  void testGetAgentClient() {
    final ApiClient agentClient = this.factory.getAgentClient();
    assertEquals(ApiClientJersey2.class, agentClient.getClass());
    assertEquals("https://agent-host:443/agent", agentClient.getBasePath());
  }

  @Test
  void testGetLoadBalancedAgentClient() {
    ApiClientFactory factory = new ApiClientFactory(this.createLoadBalancedConfig());
    final ApiClient agentClient = factory.getAgentClient();
    assertEquals(RegularLoadBalancedApiClient.class, agentClient.getClass());
    assertEquals("https://lb-agent-host:443/agent", agentClient.getBasePath());
  }

  @Test
  void testGetDatafeedAgentClient() {
    final ApiClient agentClient = this.factory.getDatafeedAgentClient();
    assertEquals(ApiClientJersey2.class, agentClient.getClass());
    assertEquals("https://agent-host:443/agent", agentClient.getBasePath());
  }

  @Test
  void testGetLoadBalancedDatafeedAgentClient() {
    ApiClientFactory factory = new ApiClientFactory(this.createLoadBalancedConfig());
    final ApiClient agentClient = factory.getDatafeedAgentClient();
    assertEquals(DatafeedLoadBalancedApiClient.class, agentClient.getClass());
    assertEquals("https://lb-agent-host:443/agent", agentClient.getBasePath());
  }

  @Test
  void testGetPodClient() {
    final ApiClient podClient = this.factory.getPodClient();
    assertEquals(ApiClientJersey2.class, podClient.getClass());
    assertEquals("https://pod-host:443/pod", podClient.getBasePath());
  }

  @Test
  void testSessionAuthClientWithoutCertificateConfiguredShouldFail() {
    assertThrows(ApiClientInitializationException.class, this.factory::getSessionAuthClient);
  }

  @Test
  void testSessionAuthClient() {
    ApiClient sessionAuth = new ApiClientFactory(this.createConfigWithCertificate()).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testKeyAuthClientWithoutCertificateConfiguredShouldFail() {
    assertThrows(ApiClientInitializationException.class, this.factory::getKeyAuthClient);
  }

  @Test
  void testAuthClientWithWrongCertPathShouldFail() {
    BdkConfig bdkConfig = this.createConfigWithCertificate("./non/existent/file.p12", "password");

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(bdkConfig).getSessionAuthClient());
    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(bdkConfig).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongPasswordShouldFail() {
    BdkConfig bdkConfig = this.createConfigWithCertificate("./src/test/resources/certs/identity.p12", "wrongPasswprd");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(bdkConfig).getSessionAuthClient());
    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(bdkConfig).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithCertificateContent() throws IOException {
    InputStream is = ApiClientFactoryTest.class.getResourceAsStream("/certs/identity.p12");

    BdkConfig config = createConfig();
    config.getBot().setCertificateContent(IOUtils.toByteArray(is));
    config.getBot().setCertificatePassword("password");

    ApiClient sessionAuth = new ApiClientFactory(config).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testAuthClientWithInvalidCertificateContent() {
    BdkConfig config = createConfig();
    config.getBot().setCertificateContent("invalid certificate".getBytes());
    config.getBot().setCertificatePassword("password");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(config).getSessionAuthClient());
  }

  @Test
  void testExtAppSessionAuthClient() {
    BdkConfig bdkConfig = this.createConfig();
    this.addExtAppCertificateToConfig(bdkConfig);
    ApiClient extAppSessionAuth = new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient();

    assertEquals(ApiClientJersey2.class, extAppSessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", extAppSessionAuth.getBasePath());
  }

  @Test
  void testExtAppAuthClientWithWrongCertPathShouldFail() {
    BdkConfig bdkConfig = this.createConfig();
    this.addExtAppCertificateToConfig(bdkConfig, "./non/existent/file.p12", "password");

    assertThrows(ApiClientInitializationException.class,
        () -> new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient());
  }

  @Test
  void testExtAppAuthClientWithWrongPasswordShouldFail() {
    BdkConfig bdkConfig = this.createConfig();
    this.addExtAppCertificateToConfig(bdkConfig, "./src/test/resources/certs/identity.p12", "wrongPassword");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient());
  }

  @Test
  void testExtAppAuthClientNotConfiguredShouldFail() {
    BdkConfig bdkConfig = this.createConfig();

    assertThrows(ApiClientInitializationException.class,
        () -> new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient());
  }

  @Test
  void testAuthClientWithTrustStore() {
    BdkConfig configWithTrustStore =
        this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore",
            "changeit");
    ApiClient sessionAuth = new ApiClientFactory(configWithTrustStore).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testAuthClientWithWrongTrustStorePathShouldFail() {
    BdkConfig configWithTrustStore =
        this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/non_existing_truststore", "changeit");

    assertThrows(ApiClientInitializationException.class,
        () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(ApiClientInitializationException.class,
        () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongTrustStorePasswordShouldFail() {
    BdkConfig configWithTrustStore =
        this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore",
            "wrongpass");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithInvalidTrustStoreConfigShouldFail() {
    BdkConfig configWithTrustStore =
        this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore",
            "changeit");

    configWithTrustStore.getSsl().getTrustStore().setContent("content".getBytes());

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
  }

  @Test
  void testAuthClientWithInvalidTrustStoreAndTrustStorePathConfigShouldFail() {
    BdkConfig configWithTrustStore =
        this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore",
            "changeit");

    configWithTrustStore.getSsl().setTrustStorePath("./src/test/resources/certs/all_symphony_certs_truststore");
    configWithTrustStore.getSsl().setTrustStorePassword("changeit");

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
  }

  @Test
  void testKeyAuthClient() {
    ApiClient keyAuth = new ApiClientFactory(this.createConfigWithCertificate()).getKeyAuthClient();

    assertEquals(ApiClientJersey2.class, keyAuth.getClass());
    assertEquals("https://km-host:443/keyauth", keyAuth.getBasePath());
  }

  private BdkConfig createConfigWithCertificateAndTrustStore(String trustStorePath, String trustStorePassword) {
    BdkConfig config = createConfigWithCertificate();
    config.getSsl().getTrustStore().setPath(trustStorePath);
    config.getSsl().getTrustStore().setPassword(trustStorePassword);

    return config;
  }

  private BdkConfig createConfigWithCertificate() {
    return createConfigWithCertificate("./src/test/resources/certs/identity.p12", "password");
  }

  private BdkConfig createConfigWithCertificate(String certificatePath, String password) {
    BdkConfig config = createConfig();

    config.getBot().getCertificate().setPath(certificatePath);
    config.getBot().getCertificate().setPassword(password);

    return config;
  }

  private BdkConfig addExtAppCertificateToConfig(BdkConfig config, String certificatePath, String password) {
    config.getApp().getCertificate().setPath(certificatePath);
    config.getApp().getCertificate().setPassword(password);

    return config;
  }

  private BdkConfig addExtAppCertificateToConfig(BdkConfig config) {
    return this.addExtAppCertificateToConfig(config, "./src/test/resources/certs/identity.p12", "password");
  }

  private BdkConfig createConfig() {
    final BdkConfig config = new BdkConfig();

    config.getPod().setHost("pod-host");
    config.getPod().setScheme("https");
    config.getPod().setPort(443);
    config.getAgent().setHost("agent-host");
    config.getAgent().setScheme("https");
    config.getAgent().setPort(443);
    config.getKeyManager().setHost("km-host");
    config.getKeyManager().setScheme("https");
    config.getKeyManager().setPort(443);
    config.getSessionAuth().setHost("sa-host");
    config.getSessionAuth().setScheme("https");
    config.getSessionAuth().setPort(443);

    return config;
  }

  private BdkConfig createLoadBalancedConfig() {
    final BdkServerConfig serverConfig = new BdkServerConfig();
    serverConfig.setHost("lb-agent-host");

    final BdkLoadBalancingConfig loadBalancingConfig = new BdkLoadBalancingConfig();
    loadBalancingConfig.setMode(BdkLoadBalancingMode.ROUND_ROBIN);
    loadBalancingConfig.setNodes(Collections.singletonList(serverConfig));

    final BdkConfig config = new BdkConfig();
    config.setHost("global-host");
    config.getAgent().setLoadBalancing(loadBalancingConfig);

    return config;
  }
}
