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
  protected byte[] privateKeyContent;
  protected String certificatePath;
  protected byte[] certificateContent;
  protected String certificatePassword;

  /**
   * Check if the RSA authentication is configured or not
   *
   * @return true if the RSA authentication is configured
   */
  public boolean isRsaAuthenticationConfigured() {
    return isNotEmpty(privateKeyPath) || isNotEmpty(privateKeyContent);
  }

  /**
   * Check if the RSA configuration is valid.
   * If both of private key path and content, the configuration is invalid.
   *
   * @return true if the RSA configuration is invalid.
   */
  public boolean isRsaConfigurationValid() {
    return !(isNotEmpty(privateKeyPath) && isNotEmpty(privateKeyContent));
  }

  /**
   * Check if the Extension App Certificate authentication is configured or not
   *
   * @return true if the Extension App Certificate authentication is configured
   */
  public boolean isCertificateAuthenticationConfigured() {
    return (isNotEmpty(certificatePath) || isNotEmpty(certificateContent)) && certificatePassword != null;
  }

  /**
   * Check if the certificate configuration is valid.
   * If both of certificate path and content, the configuration is invalid.
   *
   * @return true if the certificate configuration is invalid.
   */
  public boolean isCertificateConfigurationValid() {
    return !(isNotEmpty(certificatePath) && isNotEmpty(certificateContent));
  }
}
