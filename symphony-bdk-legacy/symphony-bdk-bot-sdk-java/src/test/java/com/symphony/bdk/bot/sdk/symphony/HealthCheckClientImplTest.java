package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;

import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientConnectionException;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientImpl;
import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import model.HealthcheckResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

public class HealthCheckClientImplTest {

  private SymBotClient symBotClient;
  private RestClient restClient;
  private HealthcheckClientImpl healthcheckClientImpl;

  @Value("${management.symphony-api-client-version}")
  private String symphonyApiClientVersion;

  @Before
  public void initBot() {
    this.initRestClient();

    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");
    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);

    this.healthcheckClientImpl = Mockito.mock(HealthcheckClientImpl.class);
  }

  @Test
  public void testHealthCheckWithoutMock() {
    final HealthCheckInfo healthCheckInfoExpected = this.initHealthCheckInfoWithoutMock();

    Mockito.when(this.healthcheckClientImpl.healthCheck()).thenReturn(healthCheckInfoExpected);

    final HealthCheckInfo healthCheckInfo = this.healthcheckClientImpl.healthCheck();
    assertEquals(healthCheckInfoExpected, healthCheckInfo);
  }

  private HealthCheckInfo initHealthCheckInfoWithoutMock() {
    final HealthcheckResponse healthcheckResponse = this.initHealthCheckResponse();
    final HealthCheckInfo healthCheckInfo =
        new HealthCheckInfo(healthcheckResponse, false, this.symphonyApiClientVersion);
    return healthCheckInfo;
  }

  private HealthcheckResponse initHealthCheckResponse() {
    final HealthcheckResponse healthcheckResponse = new HealthcheckResponse();
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
    return healthcheckResponse;
  }

  private void initRestClient() {
    final CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(20)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .recordExceptions(RestClientConnectionException.class)
        .build();

    final BulkheadConfig bhConfig = BulkheadConfig.custom()
        .maxConcurrentCalls(30)
        .maxWaitDuration(Duration.ofMillis(500))
        .build();

    this.restClient = new RestClientImpl(new RestTemplate(), cbConfig, bhConfig);
  }
}
