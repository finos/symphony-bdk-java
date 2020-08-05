package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkSslConfig {

    private String trustStorePath;
    private String trustStorePassword;
}
