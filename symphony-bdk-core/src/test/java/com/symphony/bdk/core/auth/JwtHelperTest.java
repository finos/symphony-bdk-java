package com.symphony.bdk.core.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.symphony.bdk.core.auth.jwt.JwtHelper;

import com.migcomponents.migbase64.Base64;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

/**
 * Test class for the {@link JwtHelper}.
 */
@Slf4j
class JwtHelperTest {

  @Test
  void loadPkcs8PrivateKey() throws GeneralSecurityException {
    final PrivateKey privateKey = new JwtHelper().parseRsaPrivateKey(generatePkcs8RsaPrivateKey());
    assertNotNull(privateKey);
  }

  @Test
  void loadPkcs1PrivateKey() throws GeneralSecurityException {
    final PrivateKey privateKey = new JwtHelper().parseRsaPrivateKey(generatePkcs1RsaPrivateKey());
    assertNotNull(privateKey);
  }

  @SneakyThrows
  private static String generatePkcs8RsaPrivateKey() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(4096);
    final KeyPair kp = kpg.generateKeyPair();
    return "-----BEGIN PRIVATE KEY-----\n" +
        Base64.encodeToString(kp.getPrivate().getEncoded(), true) +
        "\n-----END PRIVATE KEY-----";
  }

  @SneakyThrows
  private static String generatePkcs1RsaPrivateKey() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(4096);
    final KeyPair kp = kpg.generateKeyPair();

    final PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(kp.getPrivate().getEncoded());
    final ASN1Encodable encodable = pkInfo.parsePrivateKey();
    final ASN1Primitive primitive = encodable.toASN1Primitive();
    byte[] privateKeyPKCS1 = primitive.getEncoded();

    final PemObject pemObject = new PemObject("RSA PRIVATE KEY", privateKeyPKCS1);
    final StringWriter stringWriter = new StringWriter();
    final PemWriter pemWriter = new PemWriter(stringWriter);
    pemWriter.writeObject(pemObject);
    pemWriter.close();

    return stringWriter.toString();
  }
}
