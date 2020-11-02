package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.config.model.BdkServerConfig;

import org.apiguardian.api.API;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@API(status = API.Status.INTERNAL)
public class RandomLoadBalancingStrategy implements LoadBalancingStrategy {

  private List<BdkServerConfig> nodes;
  private int currentIndex;
  private ThreadLocalRandom localRandom;

  public RandomLoadBalancingStrategy(List<BdkServerConfig> nodes) {
    this.nodes = nodes;
    this.currentIndex = -1;
    this.localRandom = ThreadLocalRandom.current();
  }

  @Override
  public String getNewBasePath() {
    currentIndex = localRandom.nextInt(0, nodes.size());
    return nodes.get(currentIndex).getBasePath();
  }
}
