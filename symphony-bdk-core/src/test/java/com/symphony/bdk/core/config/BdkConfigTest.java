package com.symphony.bdk.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.symphony.bdk.core.config.model.BdkBotConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkExtAppConfig;

import org.junit.jupiter.api.Test;

public class BdkConfigTest {

  @Test
  public void testIsBotCertificateAuthenticationConfigured() {
    assertIsCertificateAuthenticationConfigured(null, null, false);
    assertIsCertificateAuthenticationConfigured("", "", false);

    assertIsCertificateAuthenticationConfigured("cert", null, false);

    assertIsCertificateAuthenticationConfigured("cert", "", true);
    assertIsCertificateAuthenticationConfigured("cert", "pass", true);
  }

  @Test
  public void testIsOboConfigured() {
    assertIsOboConfigured(null, "pk", null, null, false);
    assertIsOboConfigured(null, null, "cert", "pass", false);

    assertIsOboConfigured("", "pk", null, null, false);
    assertIsOboConfigured("", null, "cert", "pass", false);

    assertIsOboConfigured("appId", "pk", null, null, true);

    assertIsOboConfigured("appId", null, "cert", null, false);
    assertIsOboConfigured("appId", null, "cert", "", true);
    assertIsOboConfigured("appId", null, "cert", "pass", true);
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
}
