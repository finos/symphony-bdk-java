package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.core.client.exception.ApiClientInitializationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

@Getter
@Setter
@API(status = API.Status.STABLE)
@Slf4j
public class BdkSslConfig {

  @Deprecated
  private String trustStorePath;
  @Deprecated
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
      return trustStore.isValid() && !isNotEmpty(trustStorePath);
    }
    return true;
  }

  /**
   * Returns a {@link BdkCertificateConfig} instance equals to {@link #trustStore} if specified
   * or a new instance containing deprecated fields {@link #trustStorePath} and {@link #trustStorePassword}
   *
   * @return a {@link BdkCertificateConfig} instance SSL certificate information.
   */
  public BdkCertificateConfig getCertificateConfig() {
    if (!isValid()) {
      throw new ApiClientInitializationException(
          "Truststore configuration is not valid. This configuration should only be configured under \"trustStore\" field");
    }

    if (trustStore != null && trustStore.isConfigured()) {
      return trustStore;
    }

    final BdkCertificateConfig sslTruststoreConfig = new BdkCertificateConfig(trustStorePath, trustStorePassword);
    if (sslTruststoreConfig.isConfigured()) {
      log.warn("Truststore should be configured under \"trustStore\" field");
    }
    return sslTruststoreConfig;
  }
}
