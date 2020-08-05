package com.symphony.bdk.core.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BdkConfig {

    private BdkClientConfig agent = new BdkClientConfig();
    private BdkClientConfig pod = new BdkClientConfig();
    private BdkClientConfig keyManager = new BdkClientConfig();

    private BdkBotConfig bot = new BdkBotConfig();
    private BdkExtAppConfig app = new BdkExtAppConfig();
    private BdkSSLConfig ssl = new BdkSSLConfig();
}
