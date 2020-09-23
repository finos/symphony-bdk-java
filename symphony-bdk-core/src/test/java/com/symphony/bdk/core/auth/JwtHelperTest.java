package com.symphony.bdk.core.auth;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.jwt.JwtHelper;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migcomponents.migbase64.Base64;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link JwtHelper}.
 */
@Slf4j
class JwtHelperTest {

  public static final String CERT_PASSWORD = "changeit";
  public static final String CERT_ALIAS = "1";

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

  @Test
  @SneakyThrows
  public void testValidateJwt() {

    final UserClaim inputUserClaim = new UserClaim();
    inputUserClaim.setId("app-id");
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

  @SneakyThrows
  private KeyStore getKeyStoreFromFile() {
    FileInputStream fm = new FileInputStream(new File("./src/test/resources/certs/extapp-cert.p12"));

    KeyStore ks = KeyStore.getInstance("PKCS12");
    ks.load(fm, CERT_PASSWORD.toCharArray());

    return ks;
  }

  @SneakyThrows
  private String generateJwt(Key key, UserClaim userClaim) {
    JwtClaims claims = new JwtClaims();
    claims.setIssuer("Issuer");
    claims.setAudience("Audience");
    claims.setExpirationTimeMinutesInTheFuture(10);
    claims.setGeneratedJwtId();
    claims.setIssuedAtToNow();
    claims.setNotBeforeMinutesInThePast(2);
    claims.setSubject("subject");
    claims.setClaim("email","mail@example.com");
    claims.setClaim("user", new ObjectMapper().writeValueAsString(userClaim));

    JsonWebSignature jws = new JsonWebSignature();
    jws.setKey(key);
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    jws.setPayload(claims.toJson());

    return jws.getCompactSerialization();
  }
}
