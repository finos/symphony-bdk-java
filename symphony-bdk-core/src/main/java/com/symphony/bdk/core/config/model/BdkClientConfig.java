package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.function.Supplier;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkClientConfig {

  private BdkConfig parentConfig;

  private String scheme = null;
  private String host = null;
  private Integer port = null;
  private String context = null;

  private String proxyUrl = null;
  private String proxyUsername = null;
  private String proxyPassword = null;

  private Integer connectTimeout = null;
  private Integer readTimeout = null;
  private Integer connectionRequestTimeout = null;

  public BdkClientConfig() {
    // for Jackson deserialization
  }

  public BdkClientConfig(BdkConfig parentConfig) {
    this.parentConfig = parentConfig;
  }

  public String getScheme() {
    return thisOrParent(scheme, parentConfig::getScheme);
  }

  public String getHost() {
    return thisOrParent(host, parentConfig::getHost);
  }

  public Integer getPort() {
    return thisOrParent(port, parentConfig::getPort);
  }

  public String getContext() {
    return thisOrParent(context, parentConfig::getContext);
  }

  private <T> T thisOrParent(T thisValue, Supplier<T> parentValue) {
    return thisValue == null ? parentValue.get() : thisValue;
  }

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
    if (!localContext.equals("") && localContext.endsWith("/")) {
      return localContext.substring(0, localContext.length() - 1);
    }

    return localContext;
  }

  private String getPortAsString() {
    return this.getPort() != null ? ":" + this.getPort() : "";
  }
}
