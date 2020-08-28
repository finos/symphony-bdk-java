package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

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

    public boolean isOboConfigured() {
      return app.isConfigured();
    }
}
