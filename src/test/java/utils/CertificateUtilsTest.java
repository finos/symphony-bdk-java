package utils;

import java.security.cert.X509Certificate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.CertificateUtils;

public class CertificateUtilsTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void parseX509CertificateTest() throws Exception {
    // Arrange
    String certString = "aaaaa";

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    CertificateUtils.parseX509Certificate(certString);
  }
}
