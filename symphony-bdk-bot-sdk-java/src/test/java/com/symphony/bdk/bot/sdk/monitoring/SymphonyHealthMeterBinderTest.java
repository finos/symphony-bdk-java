package com.symphony.bdk.bot.sdk.monitoring;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import model.HealthcheckResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SymphonyHealthMeterBinderTest {

  @Test
  void bindTo_failedHealthCheck() {
    assertDoesNotThrow(() ->
        // failing healthcheck returns null
        new SymphonyHealthMeterBinder(() -> new HealthCheckInfo(null, false, ""))
            .bindTo(new SimpleMeterRegistry()));
  }

  @Test
  void bindTo_workingHealthCheck() {
    assertDoesNotThrow(() ->
        new SymphonyHealthMeterBinder(() -> new HealthCheckInfo(healthyResponse(), false, ""))
            .bindTo(new SimpleMeterRegistry()));
  }

  private HealthcheckResponse healthyResponse() {
    HealthcheckResponse response = new HealthcheckResponse();
    response.setAgentServiceUser(true);
    response.setPodConnectivity(true);
    response.setKeyManagerConnectivity(true);
    response.setPodVersion("123");
    response.setAgentVersion("123");
    return response;
  }
}
