package com.symphony.bdk.core.client;

import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;
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
  void testGetAgentClient() {

    final ApiClient agentClient = this.factory.getAgentClient();
    assertEquals(ApiClientJersey2.class, agentClient.getClass());
    assertEquals("https://agent-host:443/agent", agentClient.getBasePath());
  }

  private BdkConfig createConfig() {
    final BdkConfig config = new BdkConfig();

    config.getPod().setHost("pod-host");
    config.getAgent().setHost("agent-host");
    config.getKeyManager().setHost("km-host");

    return config;
  }
}