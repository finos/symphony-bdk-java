package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkBotConfig {

    private String username;
    private String privateKeyPath;
    private String certificatePath;
    private String certificatePassword;
}
