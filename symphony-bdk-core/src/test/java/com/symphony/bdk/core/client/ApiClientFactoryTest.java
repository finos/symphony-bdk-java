package com.symphony.bdk.core.client;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.config.model.BdkConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for the {@link ApiClientFactory}.
 */
class ApiClientFactoryTest {

  private final ApiClientFactory factory = new ApiClientFactory(this.createConfig());

  @Test
  void testGetLoginClient() throws AuthInitializationException {
    final ApiClient loginClient = this.factory.getLoginClient();
    assertEquals(ApiClientJersey2.class, loginClient.getClass());
    assertEquals("https://pod-host:443/login", loginClient.getBasePath());
  }

  @Test
  void testGetRelayClient() throws AuthInitializationException {
    final ApiClient relayClient = this.factory.getRelayClient();
    assertEquals(ApiClientJersey2.class, relayClient.getClass());
    assertEquals("https://km-host:443/relay", relayClient.getBasePath());
  }

  @Test
  void testGetAgentClient() throws AuthInitializationException {
    final ApiClient agentClient = this.factory.getAgentClient();
    assertEquals(ApiClientJersey2.class, agentClient.getClass());
    assertEquals("https://agent-host:443/agent", agentClient.getBasePath());
  }

  @Test
  void testGetPodClient() throws AuthInitializationException {
    final ApiClient podClient = this.factory.getPodClient();
    assertEquals(ApiClientJersey2.class, podClient.getClass());
    assertEquals("https://pod-host:443/pod", podClient.getBasePath());
  }

  @Test
  void testSessionAuthClientWithoutCertificateConfiguredShouldFail() {
    assertThrows(AuthInitializationException.class, () -> this.factory.getSessionAuthClient());
  }

  @Test
  void testSessionAuthClient() throws AuthInitializationException {
    ApiClient sessionAuth = new ApiClientFactory(this.createConfigWithCertificate()).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testKeyAuthClientWithoutCertificateConfiguredShouldFail() {
    assertThrows(AuthInitializationException.class, () -> this.factory.getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongCertPathShouldFail() {
    BdkConfig bdkConfig = this.createConfigWithCertificate("./non/existent/file.p12", "password");

    assertThrows(AuthInitializationException.class, () -> new ApiClientFactory(bdkConfig).getSessionAuthClient());
    assertThrows(AuthInitializationException.class, () -> new ApiClientFactory(bdkConfig).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongPasswordShouldFail() {
    BdkConfig bdkConfig = this.createConfigWithCertificate("./src/test/resources/certs/identity.p12", "wrongPasswprd");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(bdkConfig).getSessionAuthClient());
    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(bdkConfig).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithTrustStore() throws AuthInitializationException {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore", "changeit");
    ApiClient sessionAuth = new ApiClientFactory(configWithTrustStore).getSessionAuthClient();

    assertEquals(ApiClientJersey2.class, sessionAuth.getClass());
    assertEquals("https://sa-host:443/sessionauth", sessionAuth.getBasePath());
  }

  @Test
  void testAuthClientWithWrongTrustStorePathShouldFail() {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/non_existing_truststore", "changeit");

    assertThrows(AuthInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(AuthInitializationException.class, () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testAuthClientWithWrongTrustStorePasswordShouldFail() {
    BdkConfig configWithTrustStore = this.createConfigWithCertificateAndTrustStore("./src/test/resources/certs/all_symphony_certs_truststore", "wrongpass");

    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getSessionAuthClient());
    assertThrows(IllegalStateException.class, () -> new ApiClientFactory(configWithTrustStore).getKeyAuthClient());
  }

  @Test
  void testKeyAuthClient() throws AuthInitializationException {
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

  private BdkConfig createConfig() {
    final BdkConfig config = new BdkConfig();

    config.getPod().setHost("pod-host");
    config.getAgent().setHost("agent-host");
    config.getKeyManager().setHost("km-host");
    config.getSessionAuth().setHost("sa-host");

    return config;
  }
}
