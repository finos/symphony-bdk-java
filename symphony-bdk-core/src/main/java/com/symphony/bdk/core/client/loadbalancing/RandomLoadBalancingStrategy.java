package com.symphony.bdk.core.client.loadbalancing;

import com.symphony.bdk.core.config.model.BdkServerConfig;

import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The {@link LoadBalancingStrategy} corresponding to the
 * {@link com.symphony.bdk.core.config.model.BdkLoadBalancingMode#RANDOM} mode.
 */
@API(status = API.Status.INTERNAL)
public class RandomLoadBalancingStrategy implements LoadBalancingStrategy {

  private final List<BdkServerConfig> nodes;
  private final ThreadLocalRandom localRandom;
  private int currentIndex;

  /**
   *
   * @param nodes the list of nodes to be load balanced across in a random way.
   */
  public RandomLoadBalancingStrategy(List<BdkServerConfig> nodes) {
    this.nodes = new ArrayList<>(nodes);
    this.currentIndex = -1;
    this.localRandom = ThreadLocalRandom.current();
  }

  /**
   * Gets a new base path by taking a random item in {@link #nodes}.
   *
   * @return the base path of a randomly selected node.
   */
  @Override
  public String getNewBasePath() {
    currentIndex = localRandom.nextInt(0, nodes.size());
    return nodes.get(currentIndex).getBasePath();
  }
}
