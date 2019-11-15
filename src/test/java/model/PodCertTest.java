package model;

import static org.junit.Assert.assertEquals;
import model.PodCert;
import org.junit.Test;

public class PodCertTest {
  @Test
  public void PodCertTest() throws Exception {
    // Arrange and Act
    PodCert podCert = new PodCert();

    // Assert
    assertEquals(null, podCert.getCertificate());
  }

  @Test
  public void getCertificateTest() throws Exception {
    // Arrange
    PodCert podCert = new PodCert();

    // Act
    String actual = podCert.getCertificate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCertificateTest() throws Exception {
    // Arrange
    PodCert podCert = new PodCert();
    String certificate = "aaaaa";

    // Act
    podCert.setCertificate(certificate);

    // Assert
    assertEquals("aaaaa", podCert.getCertificate());
  }
}
