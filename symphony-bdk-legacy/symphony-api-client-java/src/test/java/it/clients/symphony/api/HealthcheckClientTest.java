package it.clients.symphony.api;

import clients.symphony.api.HealthcheckClient;
import clients.symphony.api.constants.AgentConstants;
import it.commons.BotTest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import model.HealthcheckResponse;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HealthcheckClientTest extends BotTest {
  private HealthcheckClient healthCheckClient;

  @Before
  public void initClient() {
    healthCheckClient = new HealthcheckClient(symBotClient);
  }

  @Test
  public void performHealthCheckSuccess() {
    stubGet(AgentConstants.HEALTHCHECK, "{\r\n" +
        "\"podConnectivity\": true,\r\n" +
        "\"keyManagerConnectivity\": true,\r\n" +
        "\"encryptDecryptSuccess\": true,\r\n" +
        "\"podVersion\": \"1.54.1\",\r\n" +
        "\"agentVersion\": \"2.54.0\",\r\n" +
        "\"agentServiceUser\": true,\r\n" +
        "\"ceServiceUser\": true\r\n" +
        "}");

    HealthcheckResponse response = healthCheckClient.performHealthCheck();

    assertNotNull(response);
    assertEquals(true, response.getPodConnectivity());
  }

  @Test
  public void performHealthCheckSuccessWithUnknownField() {
    stubGet(AgentConstants.HEALTHCHECK, "{\r\n" +
        "\"podConnectivity\": true,\r\n" +
        "\"keyManagerConnectivity\": true,\r\n" +
        "\"encryptDecryptSuccess\": true,\r\n" +
        "\"podVersion\": \"1.54.1\",\r\n" +
        "\"agentVersion\": \"2.54.0\",\r\n" +
        "\"agentServiceUser\": true,\r\n" +
        "\"ceServiceUser\": true\r\n," +
        "\"unknownField\": \"a value\"\r\n" +
        "}");

    HealthcheckResponse response = healthCheckClient.performHealthCheck();

    assertNotNull(response);
    assertEquals(true, response.getPodConnectivity());
  }
}
