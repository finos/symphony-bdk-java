package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.function.Supplier;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkClientConfig extends BdkServerConfig {

  private BdkConfig parentConfig;

  private Integer connectTimeout;
  private Integer readTimeout;
  private Integer connectionRequestTimeout;

  public BdkClientConfig() {
    // for Jackson deserialization
    this.scheme = null;
    this.host = null;
    this.port = null;
    this.context = null;

    this.connectTimeout = null;
    this.readTimeout = null;
    this.connectionRequestTimeout = null;
  }

  public BdkClientConfig(BdkConfig parentConfig) {
    this();
    this.parentConfig = parentConfig;
  }

  public boolean overridesParentConfig() {
    return scheme != null || host != null || port != null || context!= null;
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

  public BdkProxyConfig getProxy() {
    return thisOrParent(proxy, parentConfig::getProxy);
  }

  private <T> T thisOrParent(T thisValue, Supplier<T> parentValue) {
    return thisValue == null ? parentValue.get() : thisValue;
  }
}
