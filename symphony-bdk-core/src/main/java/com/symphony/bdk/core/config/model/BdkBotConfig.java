package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class BdkBotConfig {

    private String username;
    private String privateKeyPath;
    private String certificatePath;
    private String certificatePassword;

    /**
     *
     * @return true if certificate authentication is configured
     */
    public boolean isCertificateAuthenticationConfigured() {
        return StringUtils.isNotBlank(certificatePath) && certificatePassword != null;
    }
}
