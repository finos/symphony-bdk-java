package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.gen.api.SignalsApi;

import org.apiguardian.api.API;

import java.util.List;

@API(status = API.Status.INTERNAL)
public interface LoadBalancingStrategy {

  String getNewBasePath();

  static LoadBalancingStrategy getInstance(BdkLoadBalancingConfig loadBalancingConfig, ApiClientFactory apiClientFactory) {
    final List<BdkServerConfig> nodes = loadBalancingConfig.getNodes();

    switch (loadBalancingConfig.getMode()) {
      case EXTERNAL:
        final String agentLbBasePath = nodes.get(0).getBasePath();
        final SignalsApi signalsApi = new SignalsApi(apiClientFactory.getAgentClient(agentLbBasePath));
        return new ExternalLoadBalancingStrategy(signalsApi);
      case RANDOM:
        return new RandomLoadBalancingStrategy(nodes);
      case ROUND_ROBIN:
        return new RoundRobinLoadBalancingStrategy(nodes);
      default:
        return null;
    }
  }
}
