package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkConfig {

    private String _version = "2.0";

    private BdkClientConfig agent = new BdkClientConfig();
    private BdkClientConfig pod = new BdkClientConfig();
    private BdkClientConfig keyManager = new BdkClientConfig();
    private BdkClientConfig sessionAuth = new BdkClientConfig();

    private BdkBotConfig bot = new BdkBotConfig();
    private BdkExtAppConfig app = new BdkExtAppConfig();
    private BdkSslConfig ssl = new BdkSslConfig();

    private BdkRetryConfig retry = new BdkRetryConfig();
    private BdkDatafeedConfig datafeed = new BdkDatafeedConfig();

}
