package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkExtAppConfig {

    private String appId;
    private String privateKeyPath;
    private String certificatePath;
    private String certificatePassword;
}
