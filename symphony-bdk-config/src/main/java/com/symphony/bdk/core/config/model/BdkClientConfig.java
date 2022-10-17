package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.function.Supplier;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkClientConfig extends BdkServerConfig {

  private BdkConfig parentConfig;

  public BdkClientConfig() {
    // for Jackson deserialization
    this.scheme = null;
    this.host = null;
    this.port = null;
    this.context = null;

    this.connectionTimeout = null;
    this.readTimeout = null;
    this.connectionPoolMax = null;
    this.connectionPoolPerRoute = null;
    this.defaultHeaders = null;
  }

  public BdkClientConfig(BdkConfig parentConfig) {
    this();
    this.parentConfig = parentConfig;
  }

  public boolean overridesParentConfig() {
    return scheme != null || host != null || port != null || context != null;
  }

  @Override
  public String getScheme() {
    return thisOrParent(scheme, parentConfig::getScheme);
  }

  @Override
  public String getHost() {
    return thisOrParent(host, parentConfig::getHost);
  }

  @Override
  public Integer getPort() {
    return thisOrParent(port, parentConfig::getPort);
  }

  @Override
  public String getContext() {
    return thisOrParent(context, parentConfig::getContext);
  }

  @Override
  public Integer getConnectionTimeout() {
    return thisOrParent(connectionTimeout, parentConfig::getConnectionTimeout);
  }

  @Override
  public Integer getReadTimeout() {
    return thisOrParent(readTimeout, parentConfig::getReadTimeout);
  }

  @Override
  public Integer getConnectionPoolMax() {
    return thisOrParent(connectionPoolMax, parentConfig::getConnectionPoolMax);
  }

  @Override
  public Integer getConnectionPoolPerRoute() {
    return thisOrParent(connectionPoolPerRoute, parentConfig::getConnectionPoolPerRoute);
  }

  @Override
  public BdkProxyConfig getProxy() {
    return thisOrParent(proxy, parentConfig::getProxy);
  }

  @Override
  public Map<String, String> getDefaultHeaders() {
    return thisOrParent(defaultHeaders, parentConfig::getDefaultHeaders);
  }

  private <T> T thisOrParent(T thisValue, Supplier<T> parentValue) {
    return thisValue == null ? parentValue.get() : thisValue;
  }
}
