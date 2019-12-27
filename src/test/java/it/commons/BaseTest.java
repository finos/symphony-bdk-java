package it.commons;

import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseTest {
    protected static SymConfig config;

    @BeforeClass
    public static void setUp() {
        InputStream configFileStream = BaseTest.class.getResourceAsStream("/bot-config.json");
        config = SymConfigLoader.load(configFileStream);

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(
                null,
                new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] c, String a) {}
                        public void checkServerTrusted(X509Certificate[] c, String a) {}
                    }
                },
                new SecureRandom()
            );
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void resetSymBot() {
        SymBotClient.clearBotClient();
    }
}
