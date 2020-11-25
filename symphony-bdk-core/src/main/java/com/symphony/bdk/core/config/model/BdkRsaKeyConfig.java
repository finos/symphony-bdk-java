package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkRsaKeyConfig {

  private String path;
  private byte[] content;

  /**
   * Check if the RSA authentication is configured or not
   *
   * @return true if the RSA authentication is configured
   */
  public boolean isConfigured() {
    return isNotEmpty(path) || isNotEmpty(content);
  }

  /**
   * Check if the RSA configuration is valid.
   * If both of private key path and content, the configuration is invalid.
   *
   * @return true if the RSA configuration is invalid.
   */
  public boolean isValid() {
    return !(isNotEmpty(path) && isNotEmpty(content));
  }
}
