package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import model.UserInfo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sun.security.pkcs.PKCS8Key;
import utils.JwtHelper;

public class JwtHelperTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void createSignedJwtTest() throws Exception {
    // Arrange
    String user = "aaaaa";
    long expiration = 1L;
    PKCS8Key privateKey = new PKCS8Key();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    JwtHelper.createSignedJwt(user, expiration, privateKey);
  }

  @Test
  public void parseRSAPrivateKeyTest() throws Exception {
    // Arrange
    ByteArrayInputStream pemPrivateKeyFile = new ByteArrayInputStream(
        new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    // Act and Assert
    thrown.expect(GeneralSecurityException.class);
    JwtHelper.parseRSAPrivateKey(pemPrivateKeyFile);
  }

  @Test
  public void parseRSAPrivateKeyTest2() throws Exception {
    // Arrange
    File pemPrivateKeyFile = new File("aaaaa");

    // Act and Assert
    thrown.expect(FileNotFoundException.class);
    JwtHelper.parseRSAPrivateKey(pemPrivateKeyFile);
  }

  @Test
  public void validateJwtTest() throws Exception {
    // Arrange
    String jwt = "aaaaa";
    String certificate = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    JwtHelper.validateJwt(jwt, certificate);
  }
}
