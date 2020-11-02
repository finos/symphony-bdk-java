package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.List;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkLoadBalancingConfig {

  private BdkLoadBalancingMode mode;
  private boolean stickiness;
  private List<BdkServerConfig> nodes;

  public BdkLoadBalancingConfig() {
    this.stickiness = true;
  }
}
