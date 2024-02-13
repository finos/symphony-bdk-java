package com.symphony.bdk.app.spring.service;

import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.gen.api.model.V3HealthComponent;
import com.symphony.bdk.gen.api.model.V3HealthStatus;
import com.symphony.bdk.http.api.ApiRuntimeException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

/**
 * Symphony BDK custom spring boot actuator health indicator.
 * <p>
 * This indicator will provide BDK connected component health status as well as
 * {@link com.symphony.bdk.core.service.datafeed.DatafeedLoop} and {@link com.symphony.bdk.core.service.datafeed.DatahoseLoop}
 * connectivity status.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class SymphonyBdkHealthIndicator extends AbstractHealthIndicator {

  public static final String DOWN = "DOWN";
  public static final String WARNING = "WARNING";
  private final HealthService healthService;

  private static final String POD = "pod";
  private static final String DF = "datafeed";
  private static final String KM = "key_manager";
  private static final String AGT = "agentservice";
  private static final String CE = "ceservice";
  private static final String DFL = "datafeedloop";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public SymphonyBdkHealthIndicator(HealthService healthService) {
    this.healthService = healthService;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    try {
      V3Health health = healthService.healthCheckExtended();
      buildHealthDetail(builder, health);
    } catch (ApiRuntimeException e) {
      log.warn("Health check response with exception, try parse the response entity - [{}]", e.getMessage());
      try {
        V3Health health = MAPPER.readValue(e.getResponseBody(), V3Health.class);
        buildHealthDetail(builder, health);
      } catch (JsonProcessingException exception) {
        log.debug("Failed to parse the health check failure response body, assume the global health state is down.", e);
        buildHealthDownDetail(builder);
      }
    }
  }

  private void buildHealthDetail(Health.Builder builder, V3Health health) {
    Map<String, V3HealthComponent> services = health.getServices();
    Map<String, V3HealthComponent> users = health.getUsers();
    V3HealthStatus podStatus = services.get(POD).getStatus();
    V3HealthStatus dfStatus = services.get(DF).getStatus();
    V3HealthStatus kmStatus = services.get(KM).getStatus();
    V3HealthStatus agtStatus = users.get(AGT).getStatus();
    V3HealthStatus datafeedLoop = healthService.datafeedHealthCheck();

    if (datafeedLoop != V3HealthStatus.UP) {
      builder.status(Status.OUT_OF_SERVICE);
    } else {
      boolean global = podStatus == V3HealthStatus.UP && dfStatus == V3HealthStatus.UP && kmStatus == V3HealthStatus.UP
          && agtStatus == V3HealthStatus.UP;
      builder.status(global ? Status.UP.getCode() : WARNING);
    }
    builder
        .withDetail(POD, services.get(POD))
        .withDetail(DF, services.get(DF))
        .withDetail(KM, services.get(KM))
        .withDetail(AGT, users.get(AGT))
        .withDetail(CE, users.get(CE))
        .withDetail(DFL, datafeedLoop);
  }

  private void buildHealthDownDetail(Health.Builder builder) {
    builder.status(DOWN)
        .withDetail(POD, DOWN)
        .withDetail(DF, DOWN)
        .withDetail(KM, DOWN)
        .withDetail(AGT, DOWN)
        .withDetail(CE, DOWN)
        .withDetail(DFL, DOWN);
  }
}
