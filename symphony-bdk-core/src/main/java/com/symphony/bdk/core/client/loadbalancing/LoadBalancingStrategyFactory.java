package com.symphony.bdk.core.client.loadbalancing;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.config.model.BdkServerConfig;
import com.symphony.bdk.gen.api.SignalsApi;

import org.apiguardian.api.API;

import java.util.List;

/**
 * Class to instantiate {@link LoadBalancingStrategy} instances according to a specific {@link BdkConfig}.
 */
@API(status = API.Status.INTERNAL)
public class LoadBalancingStrategyFactory {

  /**
   * Returns a concrete implementation instance based on the provided inputs.
   *
   * @param config           the bdk configuration
   * @param apiClientFactory the api client factory needed for the
   *                         {@link com.symphony.bdk.core.config.model.BdkLoadBalancingMode#EXTERNAL} mode.
   * @return a fully initialized instance whose implementation depends on the provided {@link BdkLoadBalancingConfig}
   */
  static LoadBalancingStrategy getInstance(BdkConfig config,
      ApiClientFactory apiClientFactory) {
    final List<BdkServerConfig> nodes = config.getAgents().getNodes();

    switch (config.getAgents().getMode()) {
      case EXTERNAL:
        final String agentLbBasePath = nodes.get(0).getBasePath();
        final SignalsApi signalsApi = new SignalsApi(apiClientFactory.getRegularAgentClient(agentLbBasePath));
        return new ExternalLoadBalancingStrategy(config.getRetry(), signalsApi);
      case RANDOM:
        return new RandomLoadBalancingStrategy(nodes);
      default:
        return new RoundRobinLoadBalancingStrategy(nodes);
    }
  }
}
