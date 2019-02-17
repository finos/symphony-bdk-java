package utils;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private static void loadTrustStore(SymConfig config, KeyStore tks, ClientBuilder clientBuilder) {
        //load truststore
        try (FileInputStream trustStoreIS = new FileInputStream(config.getTruststorePath())) {
            tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            clientBuilder.trustStore(tks);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.error("getHttpClientBuilderWithTruststore", e);
        }
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

        try (FileInputStream keyStoreIS = new FileInputStream(config.getBotCertPath()+config.getBotCertName())) {
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

        try (FileInputStream keyStoreIS = new FileInputStream(config.getAppCertPath()+config.getAppCertName())) {
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
}
