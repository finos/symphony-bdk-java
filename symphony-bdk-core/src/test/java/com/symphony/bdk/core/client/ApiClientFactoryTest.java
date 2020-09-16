package com.symphony.bdk.core.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;
import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.junit.jupiter.api.Test;

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
  void testGetAgentClient()  {
    final ApiClient agentClient = this.factory.getAgentClient();
    assertEquals(ApiClientJersey2.class, agentClient.getClass());
    assertEquals("https://agent-host:443/agent", agentClient.getBasePath());
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

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient());
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

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(bdkConfig).getExtAppSessionAuthClient());
  }

  @Test
  void testAuthClientWithTrustStore() {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore", "changeit");
    ApiClient sessionAuth = new ApiClientFactory(configWithTrustStore).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testAuthClientWithWrongTrustStorePathShouldFail() {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/non_existing_truststore", "changeit");

    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(ApiClientInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongTrustStorePasswordShouldFail() {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore", "wrongpass");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testKeyAuthClient() {
    ApiClient keyAuth = new ApiClientFactory(this.createConfigWithCertificate()).getKeyAuthClient();

    assertEquals(ApiClientJersey2.class, keyAuth.getClass());
    assertEquals("https://km-host:443/keyauth", keyAuth.getBasePath());
  }

  private BdkConfig createConfigWithCertificateAndTrustStore(String trustStorePath, String trustStorePassword) {
    BdkConfig config = createConfigWithCertificate();
    config.getSsl().setTrustStorePath(trustStorePath);
    config.getSsl().setTrustStorePassword(trustStorePassword);

    return config;
  }

  private BdkConfig createConfigWithCertificate() {
    return createConfigWithCertificate("./src/test/resources/certs/identity.p12", "password");
  }

  private BdkConfig createConfigWithCertificate(String certificatePath, String password) {
    BdkConfig config = createConfig();

    config.getBot().setCertificatePath(certificatePath);
    config.getBot().setCertificatePassword(password);

    return config;
  }

  private BdkConfig addExtAppCertificateToConfig(BdkConfig config, String certificatePath, String password) {
    config.getApp().setCertificatePath(certificatePath);
    config.getApp().setCertificatePassword(password);

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
}
