package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkServerConfig {

  protected static final String DEFAULT_SCHEME = "https";
  protected static final int DEFAULT_HTTPS_PORT = 443;

  protected String scheme = DEFAULT_SCHEME;
  protected String host;
  protected Integer port = DEFAULT_HTTPS_PORT;
  protected String context = "";
}
