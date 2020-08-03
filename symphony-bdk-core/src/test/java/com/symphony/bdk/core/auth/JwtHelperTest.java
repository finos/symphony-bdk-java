package com.symphony.bdk.core.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.migcomponents.migbase64.Base64;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

/**
 * Test class for the {@link JwtHelper}.
 * @author Thibault Pensec
 * @since 29/02/2020
 */
@Slf4j
class JwtHelperTest {

  @Test
  void loadRSAPrivateKey() throws GeneralSecurityException {
    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(generateRSAPrivateKey());
    assertNotNull(privateKey);
  }

  @SneakyThrows
  private static String generateRSAPrivateKey() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(4096);
    final KeyPair kp = kpg.generateKeyPair();
    return "-----BEGIN PRIVATE KEY-----\n" +
        Base64.encodeToString(kp.getPrivate().getEncoded(), true) +
        "\n-----END PRIVATE KEY-----";
  }
}
