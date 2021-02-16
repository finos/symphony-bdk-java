package com.symphony.bdk.http.jersey2;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.util.ApiUtils;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apiguardian.api.API;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
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

  protected String basePath;
  protected byte[] keyStoreBytes;
  protected String keyStorePassword;
  protected byte[] trustStoreBytes;
  protected String trustStorePassword;
  protected Map<String, String> defaultHeaders;
  protected int connectionTimeout;
  protected int readTimeout;
  protected int connectionPoolMax;
  protected int connectionPoolPerRoute;
  protected String temporaryFolderPath;
  protected String proxyUrl;
  protected String proxyUser;
  protected String proxyPassword;

  public ApiClientBuilderJersey2() {
    this.basePath = "https://acme.symphony.com";
    this.keyStoreBytes = null;
    this.keyStorePassword = null;
    this.trustStoreBytes = null;
    this.trustStorePassword = null;
    this.defaultHeaders = new HashMap<>();
    this.connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    this.readTimeout = DEFAULT_READ_TIMEOUT;
    this.connectionPoolMax = DEFAULT_CONNECTION_POOL_MAX;
    this.connectionPoolPerRoute = DEFAULT_CONNECTION_POOL_MAX;
    this.temporaryFolderPath = null;
    this.proxyUrl = null;
    this.proxyUser = null;
    this.proxyPassword = null;
    this.withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientJersey2} instance.
   */
  @Override
  public ApiClient build() {
    java.util.logging.Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);

    final Client httpClient = ClientBuilder.newBuilder()
        .sslContext(this.createSSLContext())
        .withConfig(this.createClientConfig())
        .build();

    httpClient.property(ClientProperties.CONNECT_TIMEOUT, this.connectionTimeout);
    httpClient.property(ClientProperties.READ_TIMEOUT, this.readTimeout);

    return new ApiClientJersey2(httpClient, this.basePath, this.defaultHeaders, this.temporaryFolderPath);
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
  public ApiClientBuilder withConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout == null ? DEFAULT_CONNECT_TIMEOUT : connectionTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withReadTimeout(Integer readTimeout) {
    this.readTimeout = readTimeout == null ? DEFAULT_READ_TIMEOUT : readTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withConnectionPoolMax(Integer connectionPoolMax) {
    this.connectionPoolMax = connectionPoolMax == null ? DEFAULT_CONNECTION_POOL_MAX : connectionPoolMax;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withConnectionPoolPerRoute(Integer connectionPoolPerRoute) {
    this.connectionPoolPerRoute = connectionPoolPerRoute == null ? DEFAULT_CONNECTION_POOL_MAX : connectionPoolPerRoute;
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

  protected ClientConfig createClientConfig() {
    final ClientConfig clientConfig = new ClientConfig();
    this.configureJackson(clientConfig);
    if (this.proxyUrl != null) {
      this.configureProxy(clientConfig);
    }
    clientConfig.register(MultiPartFeature.class);
    clientConfig.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    // turn off compliance validation to be able to send payloads with DELETE calls
    clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
    // By default PoolingHttpClientConnectionManager, if not configured, has 20 connection in the
    // pool BUT only 2 max connection per route.
    final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(this.connectionPoolMax);
    connectionManager.setDefaultMaxPerRoute(this.connectionPoolPerRoute);
    clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
    return clientConfig;
  }

  protected void configureJackson(ClientConfig clientConfig) {
    clientConfig.register(new JSON());
    clientConfig.register(JacksonFeature.class);
  }

  protected void configureProxy(ClientConfig clientConfig) {
    clientConfig.connectorProvider(new ApacheConnectorProvider());
    clientConfig.property(ClientProperties.PROXY_URI, proxyUrl);
    clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
    clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPassword);
  }

  protected SSLContext createSSLContext() {
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
    try {
      return sslConfig.createSSLContext();
    } catch (IllegalStateException e) {
        throw new IllegalStateException(e.getCause().getMessage(), e);
    }
  }
}
