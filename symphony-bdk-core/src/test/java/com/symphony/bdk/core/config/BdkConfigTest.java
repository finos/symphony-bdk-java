package com.symphony.bdk.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.config.model.BdkBotConfig;
import com.symphony.bdk.core.config.model.BdkCertificateConfig;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;

import com.symphony.bdk.core.config.model.BdkRetryConfig;

import org.junit.jupiter.api.Test;

public class BdkConfigTest {

  @Test
  void testIsBotCertificateAuthenticationConfigured() {
    assertIsCertificateAuthenticationConfigured(null, null, false);
    assertIsCertificateAuthenticationConfigured("", "", false);

    assertIsCertificateAuthenticationConfigured("cert", null, false);

    assertIsCertificateAuthenticationConfigured("cert", "", true);
    assertIsCertificateAuthenticationConfigured("cert", "pass", true);
  }

  @Test
  void testIsOboConfigured() {
    assertIsOboConfigured(null, "pk", null, null, false);
    assertIsOboConfigured(null, null, "cert", "pass", false);

    assertIsOboConfigured("", "pk", null, null, false);
    assertIsOboConfigured("", null, "cert", "pass", false);

    assertIsOboConfigured("appId", "pk", null, null, true);

    assertIsOboConfigured("appId", null, "cert", null, false);
    assertIsOboConfigured("appId", null, "cert", "", true);
    assertIsOboConfigured("appId", null, "cert", "pass", true);
  }

  @Test
  void testIsCommonJwtEnabled() {
    final BdkConfig config = new BdkConfig();
    BdkCommonJwtConfig bdkCommonJwtConfig = new BdkCommonJwtConfig();
    bdkCommonJwtConfig.setEnabled(true);
    config.setCommonJwt(bdkCommonJwtConfig);

    assertTrue(config.isCommonJwtEnabled());
  }

  @Test
  void testBdkCertificateConfigFromClasspath() {
    BdkCertificateConfig certificateConfig = new BdkCertificateConfig("classpath:/certs/identity.p12", "password");
    final byte[] certificateBytes = certificateConfig.getCertificateBytes();

    assertTrue(certificateBytes != null && certificateBytes.length > 0);
  }

  @Test
  void testBdkCertificateConfigNotFoundInClasspath() {
    BdkCertificateConfig certificateConfig = new BdkCertificateConfig("classpath:/certs/notfound", "password");

    assertThrows(ApiClientInitializationException.class, certificateConfig::getCertificateBytes);
  }

  private void assertIsCertificateAuthenticationConfigured(String certificatePath, String certificatePassword,
      boolean expected) {
    BdkBotConfig config = new BdkBotConfig();
    config.getCertificate().setPath(certificatePath);
    config.getCertificate().setPassword(certificatePassword);

    assertEquals(expected, config.isCertificateAuthenticationConfigured());
  }

  private void assertIsOboConfigured(String appId, String privateKeyPath, String certificatePath,
      String certificatePassword, boolean expected) {
    BdkExtAppConfig extAppConfig = new BdkExtAppConfig();
    extAppConfig.setAppId(appId);
    extAppConfig.getPrivateKey().setPath(privateKeyPath);
    extAppConfig.getCertificate().setPath(certificatePath);
    extAppConfig.getCertificate().setPassword(certificatePassword);

    BdkConfig bdkConfig = new BdkConfig();
    bdkConfig.setApp(extAppConfig);

    assertEquals(expected, bdkConfig.isOboConfigured());
  }

  @Test
  void checkDefaultDatafeedRetryConfiguration() throws Exception {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/df_retry_config.yaml");

    // check global retry configuration
    // properties are different from default values
    assertThat(config.getRetry().getMaxAttempts()).isEqualTo(2);
    assertThat(config.getRetry().getInitialIntervalMillis()).isEqualTo(1000);
    assertThat(config.getRetry().getMultiplier()).isEqualTo(3.0);
    assertThat(config.getRetry().getMaxIntervalMillis()).isEqualTo(2000);

    // check specific datafeed retry configuration
    assertThat(config.getDatafeed().getRetry().getMaxAttempts()).isEqualTo(Integer.MAX_VALUE);
    assertThat(config.getDatafeed().getRetry().getInitialIntervalMillis()).isEqualTo(BdkRetryConfig.DEFAULT_INITIAL_INTERVAL_MILLIS);
    assertThat(config.getDatafeed().getRetry().getMultiplier()).isEqualTo(BdkRetryConfig.DEFAULT_MULTIPLIER);
    assertThat(config.getDatafeed().getRetry().getMaxIntervalMillis()).isEqualTo(BdkRetryConfig.DEFAULT_MAX_INTERVAL_MILLIS);
  }
}
