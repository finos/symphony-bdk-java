package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

@Getter
@Setter
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class BdkCommonJwtConfig {
  protected Boolean enabled;

  public BdkCommonJwtConfig() {
    this.enabled = false;
  }
}
