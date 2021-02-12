package com.symphony.bdk.http.webclient;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.util.ApiUtils;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apiguardian.api.API;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Specific implementation of {@link ApiClientBuilder} which creates a new instance of an {@link ApiClientWebClient}.
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientBuilderWebClient implements ApiClientBuilder {

  private final Map<String, String> defaultHeaders;
  private String basePath;
  private byte[] keyStoreBytes;
  private String keyStorePassword;
  private byte[] trustStoreBytes;
  private String trustStorePassword;
  private int connectionTimeout;
  private int readTimeout;
  private String temporaryFolderPath;
  private String proxyHost;
  private int proxyPort;
  private String proxyUser;
  private String proxyPassword;

  public ApiClientBuilderWebClient() {
    basePath = "";
    defaultHeaders = new HashMap<>();
    connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    readTimeout = DEFAULT_READ_TIMEOUT;
    proxyHost = null;
    proxyPort = -1;
    proxyUser = null;
    proxyPassword = null;
    withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientWebClient} instance.
   */
  @Override
  public ApiClient build() {
    final WebClient build = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
        .baseUrl(this.basePath)
        .build();

    return new ApiClientWebClient(build, this.basePath, this.defaultHeaders);
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
  public ApiClientBuilder withProxy(String proxyHost, int proxyPort) {
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
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

  private HttpClient createHttpClient() {
    HttpClient httpClient = HttpClient.create()
        .secure(t -> t.sslContext(this.createSSLContext()))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout)
        .doOnConnected(connection -> connection.addHandlerLast(
            new ReadTimeoutHandler(this.readTimeout, TimeUnit.MILLISECONDS)));

    return configureProxy(httpClient);
  }

  private SslContext createSSLContext() {
    try {
      SslContextBuilder builder = SslContextBuilder.forClient();
      if (this.trustStoreBytes != null) {
        final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustStore.load(new ByteArrayInputStream(this.trustStoreBytes), this.trustStorePassword.toCharArray());
        trustManagerFactory.init(trustStore);
        builder.trustManager(trustManagerFactory);
        if (this.keyStoreBytes != null) {
          final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
          final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
          keyStore.load(new ByteArrayInputStream(this.keyStoreBytes), this.keyStorePassword.toCharArray());
          keyManagerFactory.init(keyStore, this.keyStorePassword.toCharArray());
          builder.keyManager(keyManagerFactory);
        }
      }
      return builder.build();
    } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpClient configureProxy(HttpClient httpClient) {
    if (this.proxyHost == null) {
      return httpClient;
    }
    return httpClient.proxy(p -> getProxyBuilder(p));
  }

  private ProxyProvider.Builder getProxyBuilder(ProxyProvider.TypeSpec proxySpec) {
    ProxyProvider.Builder builder = proxySpec.type(ProxyProvider.Proxy.HTTP)
        .host(this.proxyHost)
        .port(this.proxyPort)
        .connectTimeoutMillis(this.connectionTimeout);

    if (this.proxyUser != null && this.proxyPassword != null) {
      builder = builder.username(this.proxyUser).password(u -> this.proxyPassword);
    }
    return builder;
  }
}
