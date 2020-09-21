package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Class holding the whole BDK configuration
 */
@Getter
@Setter
public class BdkConfig {

  private static final String DEFAULT_SCHEME = "https";
  private static final int DEFAULT_HTTPS_PORT = 443;

  private String scheme = DEFAULT_SCHEME;
  private String host;
  private Integer port = DEFAULT_HTTPS_PORT;
  private String context = "";

  private BdkClientConfig agent = new BdkClientConfig();
  private BdkClientConfig pod = new BdkClientConfig();
  private BdkClientConfig keyManager = new BdkClientConfig();
  private BdkClientConfig sessionAuth = new BdkClientConfig();

  private BdkBotConfig bot = new BdkBotConfig();
  private BdkExtAppConfig app = new BdkExtAppConfig();
  private BdkSslConfig ssl = new BdkSslConfig();

  private BdkRetryConfig retry = new BdkRetryConfig();
  private BdkDatafeedConfig datafeed = new BdkDatafeedConfig();

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
}
