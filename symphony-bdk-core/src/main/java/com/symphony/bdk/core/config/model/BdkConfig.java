package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

  private BdkAgentConfig agent = new BdkAgentConfig(this);
  private BdkClientConfig pod = new BdkClientConfig(this);
  private BdkClientConfig keyManager = new BdkClientConfig(this);
  private BdkClientConfig sessionAuth = new BdkClientConfig(this);

  private BdkBotConfig bot = new BdkBotConfig();
  private BdkExtAppConfig app = new BdkExtAppConfig();
  private BdkSslConfig ssl = new BdkSslConfig();

  private BdkRetryConfig retry = new BdkRetryConfig();
  private BdkDatafeedConfig datafeed = new BdkDatafeedConfig();
  private BdkCommonJwtConfig commonJwt = new BdkCommonJwtConfig();

  /**
   * Check if OBO is configured. Checks {@link BdkExtAppConfig#isConfigured()} on field {@link #app}.
   *
   * @return true if OBO is configured.
   */
  public boolean isOboConfigured() {
    return app.isConfigured();
  }

  public boolean isBotConfigured() {
    return bot != null && isNotEmpty(bot.getUsername());
  }

  public boolean isCommonJwtEnabled() {
    return this.getCommonJwt().getEnabled();
  }

  /**
   * Returns the retry configuration used for DataFeed services.
   *
   * @return {@link BdkDatafeedConfig#getRetry()} from {@link #datafeed} if not null, {@link #retry} otherwise
   */
  public BdkRetryConfig getDatafeedRetryConfig() {
    return datafeed.getRetry() == null ? retry : datafeed.getRetry();
  }

  public void setAgent(BdkAgentConfig agent) {
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

  private <T extends BdkClientConfig> T attachParent(T config) {
    config.setParentConfig(this);
    return config;
  }
}
