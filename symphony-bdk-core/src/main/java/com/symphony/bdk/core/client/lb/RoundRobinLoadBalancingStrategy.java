package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.config.model.BdkServerConfig;

import org.apiguardian.api.API;

import java.util.List;

@API(status = API.Status.INTERNAL)
public class RoundRobinLoadBalancingStrategy implements LoadBalancingStrategy {

  private List<BdkServerConfig> nodes;
  private int currentIndex;

  public RoundRobinLoadBalancingStrategy(List<BdkServerConfig> nodes) {
    this.nodes = nodes;
    this.currentIndex = -1;
  }

  @Override
  public String getNewBasePath() {
    currentIndex = (currentIndex + 1) % nodes.size();
    return nodes.get(currentIndex).getBasePath();
  }
}
