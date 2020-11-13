package com.symphony.bdk.core.service.health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.test.MockApiClient;

import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.gen.api.model.V2HealthCheckResponse;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.http.api.ApiClient;

import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HealthServiceTest {

  private static final String V2_HEALTH_CHECK = "/agent/v2/HealthCheck";
  private static final String V3_HEALTH_CHECK = "/agent/v3/health";
  private static final String V3_HEALTH_CHECK_EXTENDED = "/agent/v3/health/extended";
  private static final String V1_AGENT_INFO = "/agent/v1/info";

  private HealthService service;
  private MockApiClient mockApiClient;

  @BeforeEach
  void setUp() {
    this.mockApiClient = new MockApiClient();
    AuthSession authSession = mock(AuthSession.class);
    ApiClient agentClient = mockApiClient.getApiClient("/agent");
    this.service = new HealthService(
        new SystemApi(agentClient),
        new SignalsApi(agentClient),
        authSession
    );

    when(authSession.getSessionToken()).thenReturn("1234");
    when(authSession.getKeyManagerToken()).thenReturn("1234");
  }

  @Test
  void v2HealthCheck() {
    this.mockApiClient.onGet(V2_HEALTH_CHECK,
        "{\n"
            + "\"podConnectivity\": true,\n"
            + "\"keyManagerConnectivity\": true,\n"
            + "\"encryptDecryptSuccess\": true,\n"
            + "\"podVersion\": \"1.54.1\",\n"
            + "\"agentVersion\": \"2.54.0\",\n"
            + "\"agentServiceUser\": true,\n"
            + "\"ceServiceUser\": true\n"
            + "}");

    V2HealthCheckResponse response = this.service.v2HeathCheck();

    assertEquals(response.getAgentVersion(), "2.54.0");
    assertTrue(response.getCeServiceUser());
    assertTrue(response.getPodConnectivity());
  }

  @Test
  void v2HealthCheckFailed() {
    this.mockApiClient.onGet(400, V2_HEALTH_CHECK, "{}");

    assertThrows(ApiRuntimeException.class, this.service::v2HeathCheck);
  }

  @Test
  void v3HealthCheck() {
    this.mockApiClient.onGet(V3_HEALTH_CHECK,
        "{\n"
            + "  \"status\": \"UP\"\n"
            + "}");

    V3Health health = this.service.v3HealthCheck();

    assertEquals(health.getStatus().getValue(), "UP");
  }

  @Test
  void v3HealthCheckFailed() {
    this.mockApiClient.onGet(400, V3_HEALTH_CHECK, "{}");

    assertThrows(ApiRuntimeException.class, this.service::v3HealthCheck);
  }

  @Test
  void v3HealthCheckExtended() {
    this.mockApiClient.onGet(V3_HEALTH_CHECK_EXTENDED,
        "{\n"
            + "  \"status\": \"UP\",\n"
            + "  \"version\": \"2.57.0\",\n"
            + "  \"services\": {\n"
            + "    \"pod\": {\n"
            + "      \"status\": \"UP\",\n"
            + "      \"version\": \"1.57.0\"\n"
            + "    },\n"
            + "    \"datafeed\": {\n"
            + "      \"status\": \"UP\",\n"
            + "      \"version\": \"2.1.28\"\n"
            + "    },\n"
            + "    \"key_manager\": {\n"
            + "      \"status\": \"UP\",\n"
            + "      \"version\": \"1.56.0\"\n"
            + "    }\n"
            + "  },\n"
            + "  \"users\": {\n"
            + "    \"agentservice\": {\n"
            + "      \"status\": \"UP\"\n"
            + "    },\n"
            + "    \"ceservice\": {\n"
            + "      \"status\": \"UP\"\n"
            + "    }\n"
            + "  }\n"
            + "}");

    V3Health health = this.service.v3ExtendedHealthCheck();

    assertEquals(health.getServices().get("pod").getVersion(), "1.57.0");
    assertEquals(health.getServices().get("datafeed").getStatus().getValue(), "UP");
    assertEquals(health.getUsers().get("agentservice").getStatus().getValue(), "UP");
  }

  @Test
  void v3HealthCheckExtendedFailed() {
    this.mockApiClient.onGet(400, V3_HEALTH_CHECK_EXTENDED, "{}");

    assertThrows(ApiRuntimeException.class, this.service::v3ExtendedHealthCheck);
  }

  @Test
  void v1AgentInfo() {
    this.mockApiClient.onGet(V1_AGENT_INFO,
        "{\n"
            + "    \"ipAddress\": \"22.222.222.22\",\n"
            + "    \"hostname\": \"agent-75...4b6\",\n"
            + "    \"version\": \"Agent-2.55.0-SNAPSHOT-Linux-4.4.86+\",\n"
            + "    \"url\": \"https://acme.symphony.com:8443/agent\",\n"
            + "    \"commitId\": \"4a3512e70...e46476d\",\n"
            + "    \"onPrem\": true\n"
            + "}");

    AgentInfo agentInfo = this.service.getAgentInfo();

    assertEquals(agentInfo.getHostname(), "agent-75...4b6");
    assertEquals(agentInfo.getIpAddress(), "22.222.222.22");
    assertTrue(agentInfo.getOnPrem());
  }

  @Test
  void v1AgentInfoFailed() {
    this.mockApiClient.onGet(400, V1_AGENT_INFO, "{}");

    assertThrows(ApiRuntimeException.class, this.service::getAgentInfo);
  }
}
