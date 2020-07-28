package com.symphony.bdk.bot.sdk.symphony;

import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

/**
 * Performs a health check to Symphony
 *
 * @author msecato
 *
 */
public interface HealthcheckClient {

  /**
   * Retrieves health details for Symphony components (e.g. POD, agent)
   *
   * @return health check info
   */
  HealthCheckInfo healthCheck();

}
