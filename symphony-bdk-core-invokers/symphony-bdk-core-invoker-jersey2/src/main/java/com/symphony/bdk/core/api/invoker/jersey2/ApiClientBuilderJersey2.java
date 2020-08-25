package com.symphony.bdk.core.api.invoker.jersey2;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientBuilder;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Specific implementation of {@link ApiClientBuilder} which creates a new instance of an {@link ApiClientJersey2}.
 */
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

  public ApiClientBuilderJersey2() {
    basePath = "https://acme.symphony.com";
    keyStoreBytes = null;
    keyStorePassword = null;
    trustStoreBytes = null;
    trustStorePassword = null;
    defaultHeaders = new HashMap<>();
    connectionTimeout = 0;
    readTimeout = 0;
    temporaryFolderPath = null;
    userAgent("Symphony BDK/2.0/java");
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#buildClient()} which returns an {@link ApiClientJersey2} instance.
   */
  @Override
  public ApiClient buildClient() {
    java.util.logging.Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);

    SSLContext sslContext = createSSLContext();

    Client httpClient = ClientBuilder.newBuilder()
        .sslContext(sslContext)
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
  public ApiClientBuilder basePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder userAgent(String userAgent) {
    withDefaultHeader("User-Agent", userAgent);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder keyStore(byte[] keyStoreBytes, String keyStorePassword) {
    this.keyStoreBytes = keyStoreBytes;
    this.keyStorePassword = keyStorePassword;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder trustStore(byte[] trustStoreBytes, String trustStorePassword) {
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
  public ApiClientBuilder temporaryFolderPath(String temporaryFolderPath) {
    this.temporaryFolderPath = temporaryFolderPath;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder connectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder readTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
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

    return clientConfig;
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
