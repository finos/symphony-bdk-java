package com.symphony.bot.sdk.internal.symphony;

import com.symphony.bot.sdk.internal.symphony.model.HealthCheckInfo;

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
