package utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import configuration.SymConfig;
import internal.FileHelper;
import internal.jersey.NoCacheFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;

@Slf4j
public class HttpClientBuilderHelper {

    public static ClientBuilder getHttpClientBuilderWithTruststore(SymConfig config) {
        return ClientBuilder.newBuilder().register(NoCacheFeature.class)
            .sslContext(createSSLContext(
                config.getTruststorePath(),
                config.getTruststorePassword(),
                null,
                null
            ));
    }

    public static ClientBuilder getHttpClientBotBuilder(SymConfig config) {
        return ClientBuilder.newBuilder().register(NoCacheFeature.class)
            .sslContext(createSSLContext(
                config.getTruststorePath(),
                config.getTruststorePassword(),
                config.getBotCertPath() + config.getBotCertName(),
                config.getBotCertPassword()
            ));
    }

    public static ClientBuilder getHttpClientAppBuilder(SymConfig config) {
        return ClientBuilder.newBuilder().register(NoCacheFeature.class)
            .sslContext(createSSLContext(
                config.getTruststorePath(),
                config.getTruststorePassword(),
                config.getAppCertPath() + config.getAppCertName(),
                config.getAppCertPassword()
            ));
    }

    public static ClientConfig getPodClientConfig(SymConfig config) {
        final String proxyURL = !isEmpty(config.getPodProxyURL()) ? config.getPodProxyURL() : config.getProxyURL();
        final String proxyUser = !isEmpty(config.getPodProxyUsername()) ? config.getPodProxyUsername() : config.getProxyUsername();
        final String proxyPass = !isEmpty(config.getPodProxyPassword()) ? config.getPodProxyPassword() : config.getProxyPassword();
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

    private static ClientConfig getClientConfig(SymConfig config, String proxyURL, String proxyUser, String proxyPass) {
        final ClientConfig clientConfig = new ClientConfig();

        if (config.getConnectionTimeout() == 0) {
            config.setConnectionTimeout(35000);
        }

        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, config.getConnectionTimeout());
        clientConfig.property(ClientProperties.READ_TIMEOUT, config.getConnectionTimeout());

        if (!isEmpty(proxyURL)) {
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            clientConfig.property(ClientProperties.PROXY_URI, proxyURL);
            if (!isEmpty(proxyUser) && !isEmpty(proxyPass)) {
                clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
                clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPass);
            }
        }

        return clientConfig;
    }

    @SneakyThrows
    private static SSLContext createSSLContext(
        @Nullable final String truststorePath,
        @Nullable final String truststorePassword,
        @Nullable final String keystorePath,
        @Nullable final String keystorePassword
    ) {
        final SslConfigurator sslConfig = SslConfigurator.newInstance();

        if (!isEmpty(truststorePath) && !isEmpty(truststorePassword)) {
            byte[] trustStoreBytes = FileHelper.readFile(truststorePath);
            sslConfig
                .trustStoreBytes(trustStoreBytes)
                .trustStorePassword(truststorePassword);
        }

        if (!isEmpty(keystorePath) && !isEmpty(keystorePassword)) {
            byte[] keystoreBytes = FileHelper.readFile(keystorePath);
            sslConfig
                .trustStoreBytes(keystoreBytes)
                .trustStorePassword(keystorePassword);
        }

        return sslConfig.createSSLContext();
    }
}
