package utils;

import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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

    public static ClientConfig getPodClientConfig(SymConfig config) {
        String proxyURL = !isEmpty(config.getPodProxyURL()) ?
            config.getPodProxyURL() : config.getProxyURL();
        String proxyUser = !isEmpty(config.getPodProxyUsername()) ?
            config.getPodProxyUsername() : config.getProxyUsername();
        String proxyPass = !isEmpty(config.getPodProxyPassword()) ?
            config.getPodProxyPassword() : config.getProxyPassword();

        return getClientConfig(config, proxyURL, proxyUser, proxyPass);
    }

    public static ClientConfig getAgentClientConfig(SymConfig config) {
        String proxyURL = config.getProxyURL();
        String proxyUser = config.getProxyUsername();
        String proxyPass = config.getProxyPassword();

        return getClientConfig(config, proxyURL, proxyUser, proxyPass);
    }

    public static ClientConfig getKMClientConfig(SymConfig config) {
        String kmProxyURL = config.getKeyManagerProxyURL();
        String kmProxyUser = config.getKeyManagerProxyUsername();
        String kmProxyPass = config.getKeyManagerProxyPassword();

        return getClientConfig(config, kmProxyURL, kmProxyUser, kmProxyPass);
    }

    private static ClientConfig getClientConfig(
        SymConfig config, String proxyURL, String proxyUser, String proxyPass
    ) {
        ClientConfig clientConfig = new ClientConfig();
        if (config.getConnectionTimeout() == 0) {
            config.setConnectionTimeout(35000);
        }
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, config.getConnectionTimeout());
        clientConfig.property(ClientProperties.READ_TIMEOUT, config.getConnectionTimeout());

        if (!isEmpty(proxyURL)) {
            clientConfig.property(ClientProperties.PROXY_URI, proxyURL);
            if (!isEmpty(proxyUser) && !isEmpty(proxyPass)) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
                clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPass);
            }
        }

        return clientConfig;
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
