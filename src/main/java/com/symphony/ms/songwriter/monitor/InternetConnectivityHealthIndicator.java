package com.symphony.ms.songwriter.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import com.symphony.ms.songwriter.internal.lib.restclient.RestClient;

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
