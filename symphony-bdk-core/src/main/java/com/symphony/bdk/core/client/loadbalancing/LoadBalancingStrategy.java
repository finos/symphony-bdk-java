package com.symphony.bdk.core.client.loadbalancing;

import org.apiguardian.api.API;

/**
 * Interface to provide a load balancing strategy, i.e. provide a new base URL when needed.
 */
@API(status = API.Status.INTERNAL)
public interface LoadBalancingStrategy {

  /**
   * Produces a new base path according to the load balancing strategy.
   *
   * @return the new base path.
   */
  String getNewBasePath();
}
