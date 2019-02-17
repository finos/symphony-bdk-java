package utils;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class HttpClientBuilderHelper {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientBuilderHelper.class);

    public static ClientBuilder getHttpClientBuilderWithTruststore (SymConfig config){
        KeyStore tks = null;
        try {
            tks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            logger.error("getHttpClientBuilderWithTruststore", e);
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        if (config.getTruststorePath() != null) {
            loadTrustStore(config, tks, clientBuilder);
        }

        return clientBuilder;
    }

    public static ClientBuilder getHttpClientBotBuilder (SymConfig config){
        KeyStore cks = null;
        KeyStore tks = null;
        try {
            cks = KeyStore.getInstance("PKCS12");
            tks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            logger.error("getHttpClientBotBuilder", e);
        }

        try (InputStream keyStoreIS = loadInputStream(config.getBotCertPath()+config.getBotCertName())) {
            cks.load(keyStoreIS,config.getBotCertPassword().toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("getHttpClientBotBuilder", e);
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(cks, config.getBotCertPassword().toCharArray());
        if (config.getTruststorePath() != null) {
            loadTrustStore(config, tks, clientBuilder);
        }
        return clientBuilder;
    }

    public static ClientBuilder getHttpClientAppBuilder (SymConfig config){
        KeyStore cks = null;
        KeyStore tks = null;
        try {
            cks = KeyStore.getInstance("PKCS12");
            tks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            logger.error("getHttpClientAppBuilder", e);
        }

        try (InputStream keyStoreIS = loadInputStream(config.getAppCertPath()+config.getAppCertName())) {
            cks.load(keyStoreIS,config.getBotCertPassword().toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("getHttpClientBotBuilder", e);
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(cks, config.getAppCertPassword().toCharArray());
        if(config.getTruststorePath()!=null){
            loadTrustStore(config, tks, clientBuilder);
        }
        return clientBuilder;
    }

    private static void loadTrustStore(SymConfig config, KeyStore tks, ClientBuilder clientBuilder) {
        //load truststore
        try (InputStream trustStoreIS = loadInputStream(config.getTruststorePath())) {
            tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            clientBuilder.trustStore(tks);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("getHttpClientBuilderWithTruststore", e);
        }
    }

    private static InputStream loadInputStream(String fileName) throws FileNotFoundException{
        if (fileName.startsWith("/")) {
            return HttpClientBuilderHelper.class.getResourceAsStream(fileName);
        } else {
            return new FileInputStream(fileName);
        }
    }
}
