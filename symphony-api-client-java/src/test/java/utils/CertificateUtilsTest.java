package utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;

import static org.junit.Assert.*;

public class CertificateUtilsTest {

  @Test(expected = CertificateExpiredException.class)
  public void testParseX509Certificate() throws IOException, CertificateException {
    final String currentPath = System.getProperty("user.dir");
    final String megabotPath = currentPath + "/src/test/resources/megabot-ca.crt";
    final String textCertificate = new String(Files.readAllBytes(Paths.get(megabotPath)));
    assertNotNull(textCertificate);

    final X509Certificate x509Certificate = CertificateUtils.parseX509Certificate(textCertificate);
    x509Certificate.checkValidity();
  }

  @Test
  public void testParseX509CertificateBadFormat() throws IOException {
    final String currentPath = System.getProperty("user.dir");
    final String megabotPath = currentPath + "/src/test/resources/bad-megabot-ca.crt";
    final String textCertificate = new String(Files.readAllBytes(Paths.get(megabotPath)));
    assertNotNull(textCertificate);

    try {

      CertificateUtils.parseX509Certificate(textCertificate);

    } catch (final CertificateException e) {
      assertTrue(e.getMessage().contains("Could not parse certificate"));
    }
  }
}
