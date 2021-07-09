package utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import configuration.SymConfig;
import internal.FileHelper;
import internal.jersey.NoCacheFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;

/**
 * Set of support methods for creating and initializing Jersey {@link javax.ws.rs.client.Client} for Pod, Agent, KM, etc.
 */
@Slf4j
public class HttpClientBuilderHelper {

    private static final String TRUSTSTORE_FORMAT = "JKS";

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
                FileHelper.path(config.getBotCertPath(), config.getBotCertName()),
                config.getBotCertPassword()
            ));
    }

    public static ClientBuilder getHttpClientAppBuilder(SymConfig config) {
        return ClientBuilder.newBuilder().register(NoCacheFeature.class)
            .sslContext(createSSLContext(
                config.getTruststorePath(),
                config.getTruststorePassword(),
                FileHelper.path(config.getAppCertPath(), config.getAppCertName()),
                config.getAppCertPassword()
            ));
    }

    public static ClientConfig getPodClientConfig(SymConfig config) {
        return getClientConfig(
            config,
            getOr(config.getPodProxyURL(), config.getProxyURL()),
            getOr(config.getPodProxyUsername(), config.getProxyUsername()),
            getOr(config.getPodProxyPassword(), config.getProxyPassword())
        );
    }

    public static ClientConfig getAgentClientConfig(SymConfig config) {
        return getClientConfig(
            config,
            getOr(config.getAgentProxyURL(), config.getProxyURL()),
            getOr(config.getAgentProxyUsername(), config.getProxyUsername()),
            getOr(config.getAgentProxyPassword(), config.getProxyPassword())
        );
    }

    public static ClientConfig getKMClientConfig(SymConfig config) {
        return getClientConfig(
            config,
            getOr(config.getKeyManagerProxyURL(), config.getProxyURL()),
            getOr(config.getKeyManagerProxyUsername(), config.getProxyUsername()),
            getOr(config.getKeyManagerProxyPassword(), config.getProxyPassword())
        );
    }

    private static ClientConfig getClientConfig(SymConfig config, String proxyURL, String proxyUser, String proxyPass) {
        final ClientConfig clientConfig = new ClientConfig();

        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, config.getConnectionTimeout());
        clientConfig.property(ClientProperties.READ_TIMEOUT, config.getReadTimeout());

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
    protected static SSLContext createSSLContext(
        @Nullable final String truststorePath,
        @Nullable final String truststorePassword,
        @Nullable final String keystorePath,
        @Nullable final String keystorePassword
    ) {
        final SslConfigurator sslConfig = SslConfigurator.newInstance();

        if (!isEmpty(truststorePath) && !isEmpty(truststorePassword)) {
            final byte[] trustStoreBytes = FileHelper.readFile(truststorePath);
            final KeyStore truststore = KeyStore.getInstance(TRUSTSTORE_FORMAT);
            truststore.load(new ByteArrayInputStream(trustStoreBytes), truststorePassword.toCharArray());
            addDefaultRootCaCertificates(truststore);
            // if logging debug is enabled, we print the truststore entries
            if(logger.isDebugEnabled()) {
                final List<String> aliases = Collections.list(truststore.aliases());
                logger.debug("Your custom truststore ('{}') contains {} entries :", truststorePath, aliases.size());
                for (String alias : aliases) {
                    logger.debug("# {}", alias);
                }
            }
            sslConfig.trustStore(truststore);
        }

        if (!isEmpty(keystorePath) && !isEmpty(keystorePassword)) {
            final byte[] keystoreBytes = FileHelper.readFile(keystorePath);
            sslConfig
                .keyStoreBytes(keystoreBytes)
                .keyStorePassword(keystorePassword);
        }

        return sslConfig.createSSLContext();
    }

  private static String getOr(final String preferredValue, final String fallbackValue) {
    return !isEmpty(preferredValue) ? preferredValue : fallbackValue;
  }

  private static void addDefaultRootCaCertificates(KeyStore trustStore) throws GeneralSecurityException {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    // Loads default Root CA certificates (generally, from JAVA_HOME/lib/cacerts)
    trustManagerFactory.init((KeyStore)null);
    for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
      if (trustManager instanceof X509TrustManager) {
        for (X509Certificate acceptedIssuer : ((X509TrustManager) trustManager).getAcceptedIssuers()) {
          trustStore.setCertificateEntry(acceptedIssuer.getSubjectDN().getName(), acceptedIssuer);
        }
      }
    }
  }
}
