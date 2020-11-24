package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkSslConfig {

    private String trustStorePath;
    private String trustStorePassword;

    private BdkCertificateConfig trustStore = new BdkCertificateConfig();

  /**
   * Check if the trustStore configuration is valid.
   * If both of trustStore path and content, the configuration is invalid.
   *
   * @return true if the trustStore configuration is invalid.
   */
  public boolean isValid() {
      if (trustStore != null && trustStore.isConfigured()) {
        return !isNotEmpty(trustStorePath);
      }
      return true;
    }
}
