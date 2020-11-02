package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkServerConfig {

  private static final String DEFAULT_SCHEME = "https";
  private static final int DEFAULT_HTTPS_PORT = 443;

  private String scheme;
  private String host;
  private Integer port;
  private String context;

  public BdkServerConfig() {
    scheme = DEFAULT_SCHEME;
    port = DEFAULT_HTTPS_PORT;
    context = "";
  }
}
