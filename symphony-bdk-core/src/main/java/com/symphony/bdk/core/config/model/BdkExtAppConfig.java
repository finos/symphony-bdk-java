package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkExtAppConfig {

  private String appId;
  private String privateKeyPath;
  private String certificatePath;
  private String certificatePassword;

  /**
   * Check if the Extension App is configured or not
   *
   * @return true if the Extension App is configured
   */
  public boolean isConfigured() {
    return isNotEmpty(appId) && (isRsaAuthenticationConfigured() || isCertificateAuthenticationConfigured());
  }

  private boolean isRsaAuthenticationConfigured() {
    return isNotEmpty(privateKeyPath);
  }

  /**
   * Check if the Extension App Certificate authentication is configured or not
   *
   * @return true if the Extension App Certificate authentication is configured
   */
  public boolean isCertificateAuthenticationConfigured() {
    return isNotEmpty(certificatePath) && certificatePassword != null;
  }
}
