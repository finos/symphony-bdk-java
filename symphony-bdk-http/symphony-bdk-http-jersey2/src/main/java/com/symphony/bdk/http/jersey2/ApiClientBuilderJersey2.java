package com.symphony.bdk.http.jersey2;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.util.ApiUtils;

import org.apiguardian.api.API;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Specific implementation of {@link ApiClientBuilder} which creates a new instance of an {@link ApiClientJersey2}.
 */
@API(status = API.Status.STABLE)
public class ApiClientBuilderJersey2 implements ApiClientBuilder {

  private String basePath;
  private byte[] keyStoreBytes;
  private String keyStorePassword;
  private byte[] trustStoreBytes;
  private String trustStorePassword;
  private Map<String, String> defaultHeaders;
  private int connectionTimeout;
  private int readTimeout;
  private String temporaryFolderPath;
  private String proxyUrl;
  private String proxyUser;
  private String proxyPassword;

  public ApiClientBuilderJersey2() {
    basePath = "https://acme.symphony.com";
    keyStoreBytes = null;
    keyStorePassword = null;
    trustStoreBytes = null;
    trustStorePassword = null;
    defaultHeaders = new HashMap<>();
    connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    readTimeout = DEFAULT_READ_TIMEOUT;
    temporaryFolderPath = null;
    proxyUrl = null;
    proxyUser = null;
    proxyPassword = null;
    withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientJersey2} instance.
   */
  @Override
  public ApiClient build() {
    java.util.logging.Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);

    Client httpClient = ClientBuilder.newBuilder()
        .sslContext(createSSLContext())
        .withConfig(createClientConfig())
        .build();

    httpClient.property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout);
    httpClient.property(ClientProperties.READ_TIMEOUT, readTimeout);

    return new ApiClientJersey2(httpClient, basePath, defaultHeaders, temporaryFolderPath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withBasePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withUserAgent(String userAgent) {
    withDefaultHeader("User-Agent", userAgent);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withKeyStore(byte[] keyStoreBytes, String keyStorePassword) {
    this.keyStoreBytes = keyStoreBytes;
    this.keyStorePassword = keyStorePassword;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withTrustStore(byte[] trustStoreBytes, String trustStorePassword) {
    this.trustStoreBytes = trustStoreBytes;
    this.trustStorePassword = trustStorePassword;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withDefaultHeader(String key, String value) {
    this.defaultHeaders.put(key, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withTemporaryFolderPath(String temporaryFolderPath) {
    this.temporaryFolderPath = temporaryFolderPath;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withProxy(String proxyHost, int proxyPort) {
    this.proxyUrl = proxyHost != null ? "http://" + proxyHost + ":" + proxyPort : null;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withProxyCredentials(String proxyUser, String proxyPassword) {
    this.proxyUser = proxyUser;
    this.proxyPassword = proxyPassword;
    return this;
  }

  private ClientConfig createClientConfig() {
    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.register(MultiPartFeature.class);
    clientConfig.register(new JSON());
    clientConfig.register(JacksonFeature.class);
    clientConfig.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    // turn off compliance validation to be able to send payloads with DELETE calls
    clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

    if (proxyUrl != null) {
      configureProxy(clientConfig);
    }

    return clientConfig;
  }

  private void configureProxy(ClientConfig clientConfig) {
    clientConfig.connectorProvider(new ApacheConnectorProvider());
    clientConfig.property(ClientProperties.PROXY_URI, proxyUrl);
    clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
    clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPassword);
  }

  private SSLContext createSSLContext() {
    final SslConfigurator sslConfig = SslConfigurator.newInstance();

    if (isNotEmpty(trustStoreBytes) && isNotEmpty(trustStorePassword)) {
      sslConfig
          .trustStoreBytes(trustStoreBytes)
          .trustStorePassword(trustStorePassword);
    }

    if (isNotEmpty(keyStoreBytes) && isNotEmpty(keyStorePassword)) {
      sslConfig
          .keyStoreBytes(keyStoreBytes)
          .keyStorePassword(keyStorePassword);
    }
    return sslConfig.createSSLContext();
  }
}
