package com.symphony.bdk.core.service.version;

import com.symphony.bdk.core.service.version.model.AgentVersion;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.gen.api.SignalsApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

public class AgentVersionServiceTest {
  private MockApiClient mockApiClient;
  private AgentVersionService agentVersionService;


  @BeforeEach
  void init() {
    this.mockApiClient = new MockApiClient();
    SignalsApi signalsApi = spy(new SignalsApi(mockApiClient.getApiClient("/agent")));
    this.agentVersionService = new AgentVersionService(signalsApi);
  }


  @Test
  void testRetrieveVersion_works() {
    mockApiClient.onGet("/agent/v1/info", "{\"version\":\"Agent-24.10.1-815-Linux-4.14.314-237.533.amzn2.x86_64\",\"onPrem\":false,\"mt\":true}");

    Optional<AgentVersion> agentVersion = agentVersionService.retrieveAgentVersion();

    assertTrue(agentVersion.isPresent());
    assertEquals(24, agentVersion.get().getMajor());
    assertEquals(10, agentVersion.get().getMinor());
  }

  @Test
  void testRetrieveVersion_HttpError() {
    mockApiClient.onGet(500,"/agent/v1/info", "{}");

    Optional<AgentVersion> agentVersion = agentVersionService.retrieveAgentVersion();

    assertFalse(agentVersion.isPresent());
  }

  @Test
  void testRetrieveVersion_unknown() {
    mockApiClient.onGet(500,"/agent/v1/info", "{\"version\":\"unknown\"}");

    Optional<AgentVersion> agentVersion = agentVersionService.retrieveAgentVersion();

    assertFalse(agentVersion.isPresent());
  }
}
