package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkAuthenticationConfig {

  protected String privateKeyPath;
  protected String certificatePath;
  protected String certificatePassword;

  /**
   * Check if the Extension App RSA authentication is configured or not
   *
   * @return true if the Extension App RSA authentication is configured
   */
  protected boolean isRsaAuthenticationConfigured() {
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
