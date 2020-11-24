package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkProxyConfig {

  private String host;
  private int port;
  private String username;
  private String password;
}
