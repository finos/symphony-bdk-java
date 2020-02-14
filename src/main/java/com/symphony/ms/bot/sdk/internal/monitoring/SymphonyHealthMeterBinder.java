package com.symphony.ms.bot.sdk.internal.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.symphony.HealthcheckClient;
import com.symphony.ms.bot.sdk.internal.symphony.model.HealthCheckInfo;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * Retrieves health details for Symphony components (e.g. POD, agent) to be
 * exposed by prometheus endpoint.
 *
 * @author Marcus Secato
 *
 */
public class SymphonyHealthMeterBinder implements MeterBinder {
  private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyHealthMeterBinder.class);

  private static final String METRIC_NAME = "symphony_bot_health";
  private static final String METRIC_DESCRIPTION = "Health status of Symphony components";
  private static final String TAG_POD_VERSION = "podVersion";
  private static final String TAG_AGENT_VERSION = "agentVersion";
  private static final String TAG_API_VERSION = "apiClientVersion";
  private static final String BASE_UNIT = "status";

  private HealthcheckClient healthcheckClient;

  public SymphonyHealthMeterBinder(HealthcheckClient healthcheckClient) {
    this.healthcheckClient = healthcheckClient;
  }

  private HealthCheckInfo status() {
    LOGGER.debug("Performing health check for Symphony components (prometheus)");
    return healthcheckClient.healthCheck();
  }

  @Override
  public void bindTo(MeterRegistry registry) {
    LOGGER.info("Registering Symphony health status to Prometheus endpoint");
    HealthCheckInfo healthStatus = status();

    Gauge.builder(METRIC_NAME, this, value -> value.status().checkOverallStatus() ? 1.0 : 0.0)
        .description(METRIC_DESCRIPTION)
        .tags(Tags.of(
            Tag.of(TAG_POD_VERSION, healthStatus.getPodVersion()),
            Tag.of(TAG_AGENT_VERSION, healthStatus.getAgentVersion()),
            Tag.of(TAG_API_VERSION, healthStatus.getSymphonyApiClientVersion())))
        .baseUnit(BASE_UNIT)
        .register(registry);
  }

}
