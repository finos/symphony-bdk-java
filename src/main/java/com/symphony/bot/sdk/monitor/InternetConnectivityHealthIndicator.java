package com.symphony.bot.sdk.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.symphony.bot.sdk.internal.lib.restclient.RestClient;

/**
 * Sample code. Demonstrates how to aggregate new health indicators to the
 * overall system health check endpoint. This sample simply checks Internet
 * connectivity. To check its results go to:
 * http://<hostname>:<port>/<context_path>/monitor/health
 *
 */
public class InternetConnectivityHealthIndicator implements HealthIndicator {
  private static final Logger LOGGER = LoggerFactory.getLogger(InternetConnectivityHealthIndicator.class);

  private RestClient restClient;

  public InternetConnectivityHealthIndicator(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Health health() {
    try {
      restClient.getRequest("https://symphony.com", String.class);
      return Health.up().withDetail("connectivity", "UP").build();
    } catch (Exception e) {
      return Health.down().withDetail("connectivity", "DOWN").build();
    }
  }

}
