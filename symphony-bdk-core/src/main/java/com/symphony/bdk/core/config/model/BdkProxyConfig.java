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

  public BdkProxyConfig() {
  }

  public BdkProxyConfig(String host, int port, String username, String password) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
  }
}
