package com.symphony.bdk.bot.sdk.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientConnectionException;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientImpl;
import com.symphony.bdk.bot.sdk.symphony.HealthcheckClientImpl;

import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

public class SymphonyHealthIndicatorTest {

  private SymphonyHealthIndicator symphonyHealthIndicator;
  private HealthcheckClientImpl healthcheckClient;
  private SymBotClient symBotClient;
  private RestClient restClient;

  @Before
  public void initSymphonyHealthIndicator(){
    this.initRestClient();
    this.initSymBotClient();
    this.healthcheckClient = new HealthcheckClientImpl(this.symBotClient, this.restClient);
    this.symphonyHealthIndicator = new SymphonyHealthIndicator(this.healthcheckClient);
  }

  @Test
  public void testHealth(){
    final Health health = this.symphonyHealthIndicator.health();
    assertNotNull(health);

    final Map<String, Object> healthDetails = health.getDetails();
    assertTrue(healthDetails.containsKey("Symphony"));
    assertEquals(this.healthcheckClient.healthCheck(), healthDetails.get("Symphony"));
  }

  /////// Private methods

  private void initSymBotClient() {
    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);
  }

  private void initRestClient() {
    final CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
        .slidingWindowSize(20)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .recordExceptions(RestClientConnectionException.class)
        .build();

    assertNotNull(cbConfig);

    final BulkheadConfig bhConfig = BulkheadConfig.custom()
        .maxConcurrentCalls(30)
        .maxWaitDuration(Duration.ofMillis(500))
        .build();

    assertNotNull(bhConfig);

    this.restClient = new RestClientImpl(new RestTemplate(), cbConfig, bhConfig);
    assertNotNull(this.restClient);
  }
}
