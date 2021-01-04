package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;

import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;
import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

import clients.SymBotClient;
import clients.symphony.api.HealthcheckClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import model.HealthcheckResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;

public class HealthCheckClientImplTest {

  private SymBotClient symBotClient;
  private RestClient restClient;
  private HealthcheckResponse healthcheckResponse;
  private HealthcheckClientImpl healthcheckClientImpl;

  @Value("${management.symphony-api-client-version}")
  private String symphonyApiClientVersion;

  @Before
  public void initBot() {
    this.initHealthCheckResponse();

    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");

    final HealthcheckClient healthcheckClient = Mockito.mock(HealthcheckClient.class);
    Mockito.when(healthcheckClient.performHealthCheck()).thenReturn(healthcheckResponse);

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);
    Mockito.when(this.symBotClient.getHealthcheckClient()).thenReturn(healthcheckClient);

    this.initRestClient();

    this.healthcheckClientImpl = new HealthcheckClientImpl(this.symBotClient, this.restClient);
  }

  @Test
  public void testHealthCheck() {
    final HealthCheckInfo healthCheckInfoExpected = this.initHealthCheckInfo();

    final HealthCheckInfo healthCheckInfo = this.healthcheckClientImpl.healthCheck();
    assertEquals(healthCheckInfoExpected, healthCheckInfo);
  }

  private void initRestClient() {
    this.restClient = Mockito.mock(RestClient.class);

    final RestResponse<String> restResponse = new RestResponse<>();

    Mockito.when(this.restClient.getRequest(
        "https://localhost/pod/v1/podcert", String.class
    )).thenReturn(restResponse);
  }

  private HealthCheckInfo initHealthCheckInfo() {
    return new HealthCheckInfo(this.healthcheckResponse, true, this.symphonyApiClientVersion);
  }

  private void initHealthCheckResponse() {
    this.healthcheckResponse = new HealthcheckResponse();
    healthcheckResponse.setPodConnectivity(false);
    healthcheckResponse.setPodConnectivityError(null);
    healthcheckResponse.setKeyManagerConnectivity(false);
    healthcheckResponse.setKeyManagerConnectivityError(null);
    healthcheckResponse.setFirehoseConnectivity(false);
    healthcheckResponse.setFirehoseConnectivityError(null);
    healthcheckResponse.setEncryptDecryptSuccess(null);
    healthcheckResponse.setEncryptDecryptError(null);
    healthcheckResponse.setPodVersion(null);
    healthcheckResponse.setAgentVersion(null);
    healthcheckResponse.setAgentServiceUser(false);
    healthcheckResponse.setAgentServiceUserError(null);
    healthcheckResponse.setCeServiceUser(false);
    healthcheckResponse.setCeServiceUserError(null);
  }
}
