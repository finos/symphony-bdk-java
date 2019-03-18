package utils;

import configuration.SymConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class HttpClientBuilderHelper {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientBuilderHelper.class);

    public static ClientBuilder getHttpClientBuilderWithTruststore(SymConfig config) {
        KeyStore jksStore = getJksKeystore();
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        if (config.getTruststorePath() != null && jksStore != null) {
            loadTrustStore(config, jksStore, clientBuilder);
        }
        return clientBuilder;
    }

    public static ClientBuilder getHttpClientBotBuilder(SymConfig config) {
        KeyStore pkcsStore = getPkcsKeystore();
        KeyStore jksStore = getJksKeystore();

        try (InputStream keyStoreIS = loadInputStream(config.getBotCertPath() + config.getBotCertName())) {
            if (pkcsStore != null) {
                pkcsStore.load(keyStoreIS, config.getBotCertPassword().toCharArray());
            }
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("Error loading bot keystore file", e);
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(pkcsStore, config.getBotCertPassword().toCharArray());
        if (config.getTruststorePath() != null && jksStore != null) {
            loadTrustStore(config, jksStore, clientBuilder);
        }
        return clientBuilder;
    }

    public static ClientBuilder getHttpClientAppBuilder(SymConfig config) {
        KeyStore pkcsStore = getPkcsKeystore();
        KeyStore jksStore = getJksKeystore();

        try (InputStream keyStoreIS = loadInputStream(config.getAppCertPath() + config.getAppCertName())) {
            if (pkcsStore != null) {
                pkcsStore.load(keyStoreIS, config.getBotCertPassword().toCharArray());
            }
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("Error loading app keystore file", e);
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(pkcsStore, config.getAppCertPassword().toCharArray());
        if (config.getTruststorePath() != null && jksStore != null) {
            loadTrustStore(config, jksStore, clientBuilder);
        }
        return clientBuilder;
    }

    private static void loadTrustStore(SymConfig config, KeyStore tks, ClientBuilder clientBuilder) {
        try (InputStream trustStoreIS = loadInputStream(config.getTruststorePath())) {
            tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            clientBuilder.trustStore(tks);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("Error loading truststore", e);
        }
    }

    private static InputStream loadInputStream(String fileName) throws FileNotFoundException {
        if ((new File(fileName)).exists()) {
            return new FileInputStream(fileName);
        } else if (HttpClientBuilderHelper.class.getResource(fileName) != null) {
            return HttpClientBuilderHelper.class.getResourceAsStream(fileName);
        } else {
            throw new FileNotFoundException();
        }
    }

    private static KeyStore getPkcsKeystore() {
        try {
            return KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            logger.error("Error creating PKCS keystore instance", e);
        }
        return null;
    }

    private static KeyStore getJksKeystore() {
        try {
            return KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            logger.error("Error creating JKS keystore instance", e);
        }
        return null;
    }
}
