package com.symphony.bdk.bot.sdk.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.symphony.bdk.bot.sdk.symphony.HealthcheckClientImpl;
import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

public class SymphonyHealthIndicatorTest {

  private SymphonyHealthIndicator symphonyHealthIndicator;
  private HealthcheckClientImpl healthcheckClient;
  private HealthCheckInfo healthCheckInfo;

  @Before
  public void initHealthCheck(){
    this.healthcheckClient = Mockito.mock(HealthcheckClientImpl.class);
    this.healthCheckInfo = Mockito.mock(HealthCheckInfo.class);
  }

  @Test
  public void testHealthDown(){
    this.initSymphonyHealthIndicator(false);

    final Health health = this.verifyHealthStatus(Status.DOWN);

    verifyHealthDetailsContainSymphony(health);
  }


  @Test
  public void testHealthUp(){
    this.initSymphonyHealthIndicator(true);

    final Health health = this.verifyHealthStatus(Status.UP);

    verifyHealthDetailsContainSymphony(health);
  }

  //////// Private methods
  private void initSymphonyHealthIndicator(final boolean overallStatus) {
    Mockito.when(healthCheckInfo.checkOverallStatus()).thenReturn(overallStatus);

    Mockito.when(this.healthcheckClient.healthCheck()).thenReturn(healthCheckInfo);

    this.symphonyHealthIndicator = new SymphonyHealthIndicator(this.healthcheckClient);
  }

  private Health verifyHealthStatus(final Status status) {
    final Health health = this.symphonyHealthIndicator.health();
    assertNotNull(health);
    assertEquals(status, health.getStatus());
    return health;
  }

  private void verifyHealthDetailsContainSymphony(final Health health) {
    final Map<String, Object> healthDetails = health.getDetails();
    assertTrue(healthDetails.containsKey("Symphony"));
    assertEquals(this.healthcheckClient.healthCheck(), healthDetails.get("Symphony"));
  }
}
