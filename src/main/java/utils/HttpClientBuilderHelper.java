package utils;

import configuration.SymConfig;

import javax.ws.rs.client.ClientBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class HttpClientBuilderHelper {

    public static ClientBuilder getHttpClientBuilderWithTruststore (SymConfig config){
        KeyStore tks = null;
        try {
            tks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        FileInputStream keyStoreIS = null;

        FileInputStream trustStoreIS = null;
        try {
            if (config.getTruststorePath()!=null) {
                trustStoreIS = new FileInputStream(config.getTruststorePath());
                tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (trustStoreIS != null) {
                try {
                    trustStoreIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if(config.getTruststorePath()!=null){
            clientBuilder.trustStore(tks);
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
            e.printStackTrace();
        }

        FileInputStream keyStoreIS = null;

        FileInputStream trustStoreIS = null;
        try {
            keyStoreIS = new FileInputStream(config.getBotCertPath()+config.getBotCertName()+".p12");
            cks.load(keyStoreIS,config.getBotCertPassword().toCharArray());

            if (config.getTruststorePath()!=null) {
                trustStoreIS = new FileInputStream(config.getTruststorePath());
                tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (keyStoreIS != null) {
                try {
                    keyStoreIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (trustStoreIS != null) {
                try {
                    keyStoreIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(cks, config.getBotCertPassword().toCharArray());
        if(config.getTruststorePath()!=null){
            clientBuilder.trustStore(tks);
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
            e.printStackTrace();
        }

        FileInputStream keyStoreIS = null;

        FileInputStream trustStoreIS = null;
        try {
            keyStoreIS = new FileInputStream(config.getAppCertPath()+config.getAppCertName()+".p12");
            cks.load(keyStoreIS,config.getAppCertPassword().toCharArray());

            if (config.getTruststorePath()!=null) {
                trustStoreIS = new FileInputStream(config.getTruststorePath());
                tks.load(trustStoreIS, config.getTruststorePassword().toCharArray());
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (keyStoreIS != null) {
                try {
                    keyStoreIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (trustStoreIS != null) {
                try {
                    keyStoreIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ClientBuilder clientBuilder = ClientBuilder.newBuilder().keyStore(cks, config.getBotCertPassword().toCharArray());
        if(config.getTruststorePath()!=null){
            clientBuilder.trustStore(tks);
        }
        return clientBuilder;
    }
}
