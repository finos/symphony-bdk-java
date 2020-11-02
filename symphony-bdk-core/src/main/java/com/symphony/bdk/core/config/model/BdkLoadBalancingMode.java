package com.symphony.bdk.core.config.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public enum BdkLoadBalancingMode {
  EXTERNAL("external"),
  RANDOM("random"),
  ROUND_ROBIN("roundRobin");

  private String name;

  BdkLoadBalancingMode(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
