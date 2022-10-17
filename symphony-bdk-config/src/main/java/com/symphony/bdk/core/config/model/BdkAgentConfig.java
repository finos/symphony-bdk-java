package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkAgentConfig extends BdkClientConfig {

  private BdkLoadBalancingConfig loadBalancing;

  public BdkAgentConfig() {
    super();
  }

  public BdkAgentConfig(BdkConfig parentConfig) {
    super(parentConfig);
  }
}
