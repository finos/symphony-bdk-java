package com.symphony.bdk.http.jersey2;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.util.ApiUtils;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apiguardian.api.API;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Specific implementation of {@link ApiClientBuilder} which creates a new instance of an {@link ApiClientJersey2}.
 *
 * <p><b>Please note that overriding this class is an {@link org.apiguardian.api.API.Status#EXPERIMENTAL} feature that we
 * offer to developers for {@link ApiClient} customization. The internal contract of this class (e.g. protected methods)
 * is subject to changes in the future.</b>
 */
@API(status = API.Status.STABLE)
public class ApiClientBuilderJersey2 implements ApiClientBuilder {

  private static final String TRUSTSTORE_FORMAT = "JKS";

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
  protected List<Object> filters;

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
    this.filters = new ArrayList<>();
    this.withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientJersey2} instance.
   */
  @Override
  public ApiClient build() {
    java.util.logging.Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);

    SSLContext sslContext = this.createSSLContext();
    final Client httpClient = ClientBuilder.newBuilder()
        .sslContext(sslContext)
        .withConfig(this.createClientConfig(sslContext))
        .build();

    httpClient.property(ClientProperties.CONNECT_TIMEOUT, this.connectionTimeout);
    httpClient.property(ClientProperties.READ_TIMEOUT, this.readTimeout);

    filters.forEach(httpClient::register);

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
    this.withDefaultHeader("User-Agent", userAgent);
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

  @Override
  public ApiClientBuilder addFilter(Object filter) {
    this.filters.add(filter);
    return this;
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected ClientConfig createClientConfig(SSLContext sslContext) {
    final ClientConfig clientConfig = new ClientConfig();
    this.configureJackson(clientConfig);
    if (this.proxyUrl != null) {
      this.configureProxy(clientConfig);
    }

    clientConfig.register(ApiClientJersey2RequestLogFilter.class);
    clientConfig.register(MultiPartFeature.class);
    clientConfig.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    // turn off compliance validation to be able to send payloads with DELETE calls
    clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("https", sslConnectionSocketFactory)
        .register("http", new PlainConnectionSocketFactory())
        .build();

    // By default PoolingHttpClientConnectionManager, if not configured, has 20 connection in the
    // pool BUT only 2 max connection per route.
    final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
    connectionManager.setMaxTotal(this.connectionPoolMax);
    connectionManager.setDefaultMaxPerRoute(this.connectionPoolPerRoute);
    clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
    clientConfig.connectorProvider(new ApacheConnectorProvider());
    return clientConfig;
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected void configureJackson(ClientConfig clientConfig) {
    clientConfig.register(new JSON());
    clientConfig.register(JacksonFeature.class);
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected void configureProxy(ClientConfig clientConfig) {
    clientConfig.property(ClientProperties.PROXY_URI, proxyUrl);
    clientConfig.property(ClientProperties.PROXY_USERNAME, proxyUser);
    clientConfig.property(ClientProperties.PROXY_PASSWORD, proxyPassword);
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected SSLContext createSSLContext() {
    try {
      SSLContextBuilder builder = new SSLContextBuilder();
      final KeyStore truststore = KeyStore.getInstance(TRUSTSTORE_FORMAT);
      if (isNotEmpty(trustStoreBytes) && isNotEmpty(trustStorePassword)) {
        truststore.load(new ByteArrayInputStream(trustStoreBytes), trustStorePassword.toCharArray());
      } else {
        truststore.load(null, null);
      }
      if (isNotEmpty(keyStoreBytes) && isNotEmpty(keyStorePassword)) {
        final KeyStore keystore = KeyStore.getInstance(TRUSTSTORE_FORMAT);
        keystore.load(new ByteArrayInputStream(keyStoreBytes), keyStorePassword.toCharArray());
        builder.loadKeyMaterial(keystore, null);
      }
      ApiUtils.addDefaultRootCaCertificates(truststore);
      builder.loadTrustMaterial(truststore, null);
      return builder.build();
    }  catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e.getCause().getMessage(), e);
    }
  }
}
