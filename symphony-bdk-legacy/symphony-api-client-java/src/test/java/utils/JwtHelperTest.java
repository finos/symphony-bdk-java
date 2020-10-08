package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import model.UserInfo;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

import static org.junit.Assert.*;

public class JwtHelperTest {
  private final String CERT_PASSWORD = "123456";
  private final String PKCS8_PRIVATE_KEY_PATH = "/testprivatekey.pkcs8";

  @Test
  public void loadPkcs8PrivateKey() throws IOException, GeneralSecurityException {
    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(getResourceStream(PKCS8_PRIVATE_KEY_PATH));
    assertNotNull(privateKey);
  }

  @Test
  public void loadPkcs8PrivateKeyFromFile() throws IOException, GeneralSecurityException {
    File file = new File(utils.JwtHelperTest.class.getResource(PKCS8_PRIVATE_KEY_PATH).getPath());
    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(file);
    assertNotNull(privateKey);
  }

  @Test
  public void loadPkcs8PrivateKeyFromBytes() throws IOException, GeneralSecurityException {
    InputStream stream = utils.JwtHelperTest.class.getResourceAsStream(PKCS8_PRIVATE_KEY_PATH);
    byte[] data = IOUtils.toByteArray(stream);
    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(data);
    assertNotNull(privateKey);
  }

  @Test(expected = GeneralSecurityException.class)
  public void parseRSAPrivateKeyInvalidKey() throws IOException, GeneralSecurityException {
    JwtHelper.parseRSAPrivateKey(getResourceStream("/testdatafeed.id"));
  }

  @Test
  public void loadPkcs1PrivateKey() throws IOException, GeneralSecurityException {
    final PrivateKey privateKey = JwtHelper.parseRSAPrivateKey(getResourceStream("/private-key.pem"));
    assertNotNull(privateKey);
  }

  @SneakyThrows
  @Test
  public void validateJwtTest() {
    final UserInfo userInfo = new UserInfo();
    userInfo.setId(12345L);
    userInfo.setUsername("testuser");
    userInfo.setEmailAddress("testuser@email.com");

    final KeyStore keyStore = getKeyStoreFromFile();
    final Certificate certificate = keyStore.getCertificate("1");
    final String certificatePem = java.util.Base64.getEncoder().encodeToString(certificate.getEncoded());

    String jwt = generateJwt(keyStore.getKey("1", CERT_PASSWORD.toCharArray()), userInfo);
    UserInfo result = JwtHelper.validateJwt(jwt, certificatePem);

    assertNotNull(result);
    assertEquals(userInfo.getId(), result.getId());
    assertEquals(userInfo.getUsername(), result.getUsername());
    assertEquals(userInfo.getEmailAddress(), result.getEmailAddress());
  }

  @SneakyThrows
  @Test
  public void validateJwtInvalidJwtTest() {
    final KeyStore keyStore = getKeyStoreFromFile();
    final Certificate certificate = keyStore.getCertificate("1");
    final String certificatePem = java.util.Base64.getEncoder().encodeToString(certificate.getEncoded());
    UserInfo result = JwtHelper.validateJwt("invalid jwt", certificatePem);

    assertNull(result);
  }

  private InputStream getResourceStream(String path) {
    return utils.JwtHelperTest.class.getResourceAsStream(path);
  }

  @SneakyThrows
  private KeyStore getKeyStoreFromFile() {
    FileInputStream fm = new FileInputStream(
        new File(utils.JwtHelperTest.class.getResource("/testkeystore.jks").getPath()));
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(fm, CERT_PASSWORD.toCharArray());

    return keyStore;
  }

  private String generateJwt(Key key, UserInfo userInfo) {
    Date notBefore = new Date(new Date().getTime() - (365 * 1000 * 3600 * 24));
    Date expiration = new Date(new Date().getTime() + (365 * 1000 * 3600 * 24));

    return Jwts.builder()
        .setIssuer("me")
        .setSubject("Bob")
        .setAudience("you")
        .setExpiration(expiration)
        .setNotBefore(notBefore)
        .setIssuedAt(new Date())
        .claim("user", userInfo)
        .signWith(SignatureAlgorithm.RS256, key)
        .setId("123")
        .compact();
  }
}

