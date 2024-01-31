package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.Map;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkServerConfig {

  protected static final String DEFAULT_SCHEME = "https";
  protected static final int DEFAULT_HTTPS_PORT = 443;

  protected BdkProxyConfig proxy;

  protected String scheme = DEFAULT_SCHEME;
  protected String host;
  protected Integer port = DEFAULT_HTTPS_PORT;
  protected String context = "";
  protected Integer connectionTimeout;
  protected Integer readTimeout;
  protected Integer connectionPoolMax;
  protected Integer connectionPoolPerRoute;
  protected Map<String, String> defaultHeaders;

  public String getBasePath() {
    return this.getScheme() + "://" + this.getHost() + this.getPortAsString() + this.getFormattedContext();
  }

  public String getFormattedContext() {
    final String localContext = this.getContext();
    if (localContext == null) {
      return "";
    }
    if (!localContext.equals("") && localContext.charAt(0) != '/') {
      return "/" + localContext;
    }
    if (localContext.endsWith("/")) {
      return localContext.substring(0, localContext.length() - 1);
    }

    return localContext;
  }

  private String getPortAsString() {
    return this.getPort() != null ? ":" + this.getPort() : "";
  }
}
