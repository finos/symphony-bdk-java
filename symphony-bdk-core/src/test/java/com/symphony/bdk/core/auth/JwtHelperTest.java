package com.symphony.bdk.core.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.migcomponents.migbase64.Base64;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

/**
 * Test class for the {@link JwtHelper}.
 */
@Slf4j
public class JwtHelperTest {

  public static final String CERT_PASSWORD = "changeit";
  public static final String CERT_ALIAS = "1";
  public static final String JWT = "Bearer eyJraWQiOiJGNG5Xak9WbTRBZU9JYUtEL2JCUWNleXI5MW89IiwiYWxnIjoiUlMyNTYifQ."
      + "eyJleHAiOjE2NDEzMDgyNzgsInN1YiI6IjEzMDU2NzAwNTgwOTE1IiwiZXh0X3BvZF9pZCI6MTkwLCJwb2xpY3lfaWQiOiJhcHAiLCJlbnRpdGx"
      + "lbWVudHMiOiIifQ.signature";
  private static final String JWT_EXP_INVALID = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.zhWFI4bw81QLE49UnklwMlThgt2ktUOs5M1HKjENgRE.signature";
  public static final String JWT_SKD_ENABLED = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiY2FuVXNlU2ltcGxpZmllZEtleURlbGl2ZXJ5Ijp0cnVlfQ.signature";

  @Test
  void loadPkcs8PrivateKey() throws GeneralSecurityException {
    final PrivateKey privateKey = JwtHelper.parseRsaPrivateKey(generatePkcs8RsaPrivateKey());
    assertNotNull(privateKey);
  }

  @Test
  void loadInvalidPkcs8PrivateKey() {
    String invalidPkc8PrivateKey = "-----BEGIN PRIVATE KEY-----\n"
        + "abcdef\n"
        + "-----END PRIVATE KEY-----";
    assertThrows(GeneralSecurityException.class, () -> JwtHelper.parseRsaPrivateKey(invalidPkc8PrivateKey));
  }

  @Test
  void loadPkcs1PrivateKey() throws GeneralSecurityException {
    final PrivateKey privateKey = JwtHelper.parseRsaPrivateKey(generatePkcs1RsaPrivateKey());
    assertNotNull(privateKey);
  }

  @Test
  void loadInvalidPkcs1PrivateKey() {
    String invalidPkc8PrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n"
        + "abcde\nf"
        + "-----END RSA PRIVATE KEY-----";
    assertThrows(GeneralSecurityException.class, () -> JwtHelper.parseRsaPrivateKey(invalidPkc8PrivateKey));
  }

  @Test
  @SneakyThrows
  public void testValidateJwt() {

    final UserClaim inputUserClaim = new UserClaim();
    inputUserClaim.setId(1234L);
    inputUserClaim.setCompanyId("companyID");

    final KeyStore keyStore = getKeyStoreFromFile();
    final Certificate certificate = keyStore.getCertificate(CERT_ALIAS);
    final String certificatePem = java.util.Base64.getEncoder().encodeToString(certificate.getEncoded());

    String jwt = generateJwt(keyStore.getKey(CERT_ALIAS, CERT_PASSWORD.toCharArray()), inputUserClaim);

    assertEquals(inputUserClaim, JwtHelper.validateJwt(jwt, certificatePem));
  }

  @Test
  @SneakyThrows
  public void testValidateJwtWithInvalidCertificate() {
    final KeyStore keyStore = getKeyStoreFromFile();
    String jwt = generateJwt(keyStore.getKey(CERT_ALIAS, CERT_PASSWORD.toCharArray()), new UserClaim());

    assertThrows(AuthInitializationException.class, () -> JwtHelper.validateJwt(jwt, "invalid pem"));
  }

  @Test
  @SneakyThrows
  public void testValidateJwtWithInvalidJwt() {
    final KeyStore keyStore = getKeyStoreFromFile();
    final Certificate certificate = keyStore.getCertificate(CERT_ALIAS);
    final String certificatePem = java.util.Base64.getEncoder().encodeToString(certificate.getEncoded());

    assertThrows(AuthInitializationException.class, () -> JwtHelper.validateJwt("invalid jwt", certificatePem));
  }

  @Test
  public void testExtractExpirationDate() throws Exception {
    Long expirationDate = JwtHelper.extractExpirationDate(JWT);

    assertNotNull(expirationDate);
  }

  @Test
  public void testExtractExpirationDateInvalidJwt() {
    assertThrows(AuthUnauthorizedException.class, () -> JwtHelper.extractExpirationDate("invalid jwt"));
  }

  @Test
  public void testExtractExpirationDateInvalidParsing() {
    assertThrows(JsonProcessingException.class, () -> JwtHelper.extractExpirationDate("invalid.common.jwt"));
  }

  @Test
  public void testExtractExpirationDateInvalidExpData() {
    assertThrows(AuthUnauthorizedException.class, () -> JwtHelper.extractExpirationDate(JWT_EXP_INVALID));
  }

  @Test
  public void testCheckSkdEnabled() {
    assertTrue(JwtHelper.isSkdEnabled(JWT_SKD_ENABLED));
    assertFalse(JwtHelper.isSkdEnabled("invalid.jwt.value"));
  }

  @SneakyThrows
  private static String generatePkcs8RsaPrivateKey() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(1024);
    final KeyPair kp = kpg.generateKeyPair();
    return "-----BEGIN PRIVATE KEY-----\n" +
        Base64.encodeToString(kp.getPrivate().getEncoded(), true) +
        "\n-----END PRIVATE KEY-----";
  }

  @SneakyThrows
  private static String generatePkcs1RsaPrivateKey() {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(1024);
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

  @SneakyThrows
  private KeyStore getKeyStoreFromFile() {
    FileInputStream fm = new FileInputStream("./src/test/resources/certs/extapp-cert.p12");

    KeyStore ks = KeyStore.getInstance("PKCS12");
    ks.load(fm, CERT_PASSWORD.toCharArray());

    return ks;
  }

  @SneakyThrows
  private String generateJwt(Key key, UserClaim userClaim) {
    Date notBefore = new Date(new Date().getTime() - (365L * 1000 * 3600 * 24));
    Date expiration = new Date(new Date().getTime() + (365L * 1000 * 3600 * 24));

    return Jwts.builder()
        .setIssuer("me")
        .setSubject("Bob")
        .setAudience("you")
        .setExpiration(expiration)
        .setNotBefore(notBefore)
        .setIssuedAt(new Date())
        .claim("user", userClaim)
        .signWith(SignatureAlgorithm.RS256, key)
        .setId("123")
        .compact();
  }
}
