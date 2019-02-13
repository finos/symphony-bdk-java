package utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtils {

  public static X509Certificate parseX509Certificate(final String certificateString)
      throws GeneralSecurityException {
    try {
      CertificateFactory f = CertificateFactory.getInstance("X.509");
      byte[] bytes = certificateString.getBytes("UTF-8");
      ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
      X509Certificate certificate = (X509Certificate) f.generateCertificate(stream);
      return certificate;
    } catch (UnsupportedEncodingException e) {
      throw new GeneralSecurityException(e);
    }
  }

}
