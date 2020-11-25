package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkAuthenticationConfig {

  @Deprecated
  protected String privateKeyPath;
  @Deprecated
  protected byte[] privateKeyContent;
  @Deprecated
  protected String certificatePath;
  @Deprecated
  protected byte[] certificateContent;
  @Deprecated
  protected String certificatePassword;

  protected BdkRsaKeyConfig privateKey = new BdkRsaKeyConfig();
  protected BdkCertificateConfig certificate = new BdkCertificateConfig();

  /**
   * Check if the RSA authentication is configured or not
   *
   * @return true if the RSA authentication is configured
   */
  public boolean isRsaAuthenticationConfigured() {
    return (privateKey != null && privateKey.isConfigured())
        || isNotEmpty(privateKeyPath)
        || isNotEmpty(privateKeyContent);
  }

  /**
   * Check if the RSA configuration is valid.
   * If privateKey field is configured, the privateKeyPath and privateKeyContent should not be configured.
   * If both of private key path and content, the configuration is invalid.
   *
   * @return true if the RSA configuration is invalid.
   */
  public boolean isRsaConfigurationValid() {
    if (privateKey != null && privateKey.isConfigured()) {
      if (isNotEmpty(privateKeyPath) || isNotEmpty(privateKeyContent)) {
        return false;
      }
      return privateKey.isValid();
    }
    return !(isNotEmpty(privateKeyPath) && isNotEmpty(privateKeyContent));
  }

  /**
   * Check if the certificate authentication is configured or not
   *
   * @return true if the certificate authentication is configured
   */
  public boolean isCertificateAuthenticationConfigured() {
    return (certificate != null && certificate.isConfigured())
        || (isNotEmpty(certificatePath) || isNotEmpty(certificateContent)) && certificatePassword != null;
  }

  /**
   * Check if the certificate configuration is valid.
   * If certificate field is configured, the certificatePath and certificateContent should not be configured.
   * If both of certificate path and content, the configuration is invalid.
   *
   * @return true if the certificate configuration is invalid.
   */
  public boolean isCertificateConfigurationValid() {
    if (certificate != null && certificate.isConfigured()) {
      if (isNotEmpty(certificatePath) || isNotEmpty(certificateContent)) {
        return false;
      }
      return certificate.isValid();
    }
    return !(isNotEmpty(certificatePath) && isNotEmpty(certificateContent));
  }

  /**
   * Check if both of certificate and RSA authentication is configured.
   *
   * @return true if both of certificate and RSA authentication is configured.
   */
  public boolean isBothCertificateAndRsaConfigured() {
    return isRsaAuthenticationConfigured() && isCertificateAuthenticationConfigured();
  }
}
