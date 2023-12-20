package com.symphony.bdk.core.test;

import com.migcomponents.migbase64.Base64;
import lombok.SneakyThrows;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Helper class for generating RSA key pair.
 */
public class RsaTestHelper {

  @SneakyThrows
  public static KeyPair generateKeyPair() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    return kpg.generateKeyPair();
  }

  @SneakyThrows
  public static String generatePrivateKeyAsString() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    final KeyPair kp = generateKeyPair();
    return "-----BEGIN PRIVATE KEY-----\n" +
        Base64.encodeToString(kp.getPrivate().getEncoded(), true) +
        "\n-----END PRIVATE KEY-----";
  }
}
