package com.symphony.bdk.bot.sdk.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.symphony.bdk.bot.sdk.symphony.HealthcheckClient;
import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

/**
 * Retrieves health details for Symphony components (e.g. POD, agent)
 *
 * @author Marcus Secato
 *
 */
public class SymphonyHealthIndicator implements HealthIndicator {
  private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyHealthIndicator.class);

  private HealthcheckClient healthcheckClient;

  public SymphonyHealthIndicator(HealthcheckClient healthcheckClient) {
    this.healthcheckClient = healthcheckClient;
  }

  @Override
  public Health health() {
    LOGGER.debug("Performing health check for Symphony components");
    HealthCheckInfo symphonyHealthResponse = healthcheckClient.healthCheck();

    Health.Builder healthBuilder = Health.up();
    if (!symphonyHealthResponse.checkOverallStatus()) {
      healthBuilder = Health.down();
    }

    return healthBuilder.withDetail("Symphony",
        symphonyHealthResponse).build();
  }

}
