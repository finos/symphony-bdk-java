package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Getter
@Setter
public class BdkExtAppConfig {

  private String appId;
  private String privateKeyPath;
  private String certificatePath;
  private String certificatePassword;

  public boolean isConfigured() {
    return isNotEmpty(appId) && (isRsaAuthenticationConfigured() || isCertificateAuthenticationConfigured());
  }

  private boolean isRsaAuthenticationConfigured() {
    return isNotEmpty(privateKeyPath);
  }

  private boolean isCertificateAuthenticationConfigured() {
    return isNotEmpty(certificatePath) && certificatePassword != null;
  }
}
