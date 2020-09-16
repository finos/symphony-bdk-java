package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BdkExtAppConfig extends BdkAuthenticationConfig {

  private String appId;

  /**
   * Check if the Extension App is configured or not
   *
   * @return true if the Extension App is configured
   */
  public boolean isConfigured() {
    return isNotEmpty(appId) && (isRsaAuthenticationConfigured() || isCertificateAuthenticationConfigured());
  }

}
