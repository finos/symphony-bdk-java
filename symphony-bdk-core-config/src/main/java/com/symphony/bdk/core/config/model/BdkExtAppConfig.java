package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
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
