package com.symphony.bdk.core.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * Class holding the whole BDK configuration
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkConfig extends BdkServerConfig {

  private BdkClientConfig agent = new BdkClientConfig(this);
  private BdkClientConfig pod = new BdkClientConfig(this);
  private BdkClientConfig keyManager = new BdkClientConfig(this);
  private BdkClientConfig sessionAuth = new BdkClientConfig(this);

  private BdkBotConfig bot = new BdkBotConfig();
  private BdkExtAppConfig app = new BdkExtAppConfig();
  private BdkSslConfig ssl = new BdkSslConfig();

  private BdkRetryConfig retry = new BdkRetryConfig();
  private BdkDatafeedConfig datafeed = new BdkDatafeedConfig();

  @JsonProperty("lb-agent")
  private BdkLoadBalancingConfig agentLoadBalancing;

  /**
   * Check if OBO is configured. Checks {@link BdkExtAppConfig#isConfigured()} on field {@link #app}.
   *
   * @return true if OBO is configured.
   */
  public boolean isOboConfigured() {
    return app.isConfigured();
  }

  /**
   * Returns the retry configuration used for DataFeed services.
   *
   * @return {@link BdkDatafeedConfig#getRetry()} from {@link #datafeed} if not null, {@link #retry} otherwise
   */
  public BdkRetryConfig getDatafeedRetryConfig() {
    return datafeed.getRetry() == null ? retry : datafeed.getRetry();
  }

  public void setAgent(BdkClientConfig agent) {
    this.agent = attachParent(agent);
  }

  public void setPod(BdkClientConfig pod) {
    this.pod = attachParent(pod);
  }

  public void setKeyManager(BdkClientConfig keyManager) {
    this.keyManager = attachParent(keyManager);
  }

  public void setSessionAuth(BdkClientConfig sessionAuth) {
    this.sessionAuth = attachParent(sessionAuth);
  }

  private BdkClientConfig attachParent(BdkClientConfig config) {
    config.setParentConfig(this);
    return config;
  }
}
