package com.symphony.bdk.core.client.loadbalancing;

import com.symphony.bdk.core.config.model.BdkServerConfig;

import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link LoadBalancingStrategy} corresponding to the
 * {@link com.symphony.bdk.core.config.model.BdkLoadBalancingMode#ROUND_ROBIN} mode.
 */
@API(status = API.Status.INTERNAL)
public class RoundRobinLoadBalancingStrategy implements LoadBalancingStrategy {

  private final List<BdkServerConfig> nodes;
  private AtomicInteger currentIndex;

  /**
   *
   * @param nodes the list of nodes to be load balanced across in a round-robin way.
   */
  public RoundRobinLoadBalancingStrategy(List<BdkServerConfig> nodes) {
    this.nodes = new ArrayList<>(nodes);
    this.currentIndex = new AtomicInteger(-1);
  }

  /**
   * Gets a new base path by taking the next item in {@link #nodes}.
   *
   * @return the base path of the next node.
   */
  @Override
  public String getNewBasePath() {
    final int nextIndex = currentIndex.updateAndGet(operand -> (operand + 1) % nodes.size());
    return nodes.get(nextIndex).getBasePath();
  }
}
