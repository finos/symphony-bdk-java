package utils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertificateUtils {
    private static final String X509_PADDING_REGEX = "[\\r\\n]?-{5}(BEGIN|END) CERTIFICATE-{5}[\\r\\n]?";

    public static X509Certificate parseX509Certificate(String certString) throws CertificateException {
        certString = certString.replaceAll(X509_PADDING_REGEX, "").replaceAll("\n", "");
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        byte[] bytes = Base64.getDecoder().decode(certString);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return (X509Certificate) f.generateCertificate(stream);
    }
}
