package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@Getter
@Setter
@API(status = Status.EXPERIMENTAL)
public class BdkCustomEnhancedAuthConfig {
  private String headerName;
  private String id;
  private boolean enabled = false;
}
