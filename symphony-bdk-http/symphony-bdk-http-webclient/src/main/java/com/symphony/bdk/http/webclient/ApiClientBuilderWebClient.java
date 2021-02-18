package com.symphony.bdk.http.webclient;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.util.ApiUtils;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 * <p><b>Please note that overriding this class is an {@link org.apiguardian.api.API.Status#EXPERIMENTAL} feature that we
 * offer to developers for {@link ApiClient} customization. The internal contract of this class (e.g. protected methods)
 * is subject to changes in the future.</b>
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientBuilderWebClient implements ApiClientBuilder {

  private static final Logger log = LoggerFactory.getLogger(ApiClientBuilderWebClient.class);

  protected final Map<String, String> defaultHeaders;
  protected String basePath;
  protected byte[] keyStoreBytes;
  protected String keyStorePassword;
  protected byte[] trustStoreBytes;
  protected String trustStorePassword;
  protected int connectionTimeout;
  protected int readTimeout;
  protected String proxyHost;
  protected int proxyPort;
  protected String proxyUser;
  protected String proxyPassword;

  public ApiClientBuilderWebClient() {
    this.basePath = "";
    this.defaultHeaders = new HashMap<>();
    this.connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    this.readTimeout = DEFAULT_READ_TIMEOUT;
    this.proxyHost = null;
    this.proxyPort = -1;
    this.proxyUser = null;
    this.proxyPassword = null;
    this.withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientWebClient} instance.
   */
  @Override
  public ApiClient build() {
    final WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(this.createHttpClient()))
        .baseUrl(this.basePath)
        .build();

    return new ApiClientWebClient(webClient, this.basePath, this.defaultHeaders);
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
    log.debug("temporaryFolderPath is not used by ApiClientBuilderWebClient");
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

  @API(status = API.Status.EXPERIMENTAL)
  protected HttpClient createHttpClient() {
    HttpClient httpClient = HttpClient.create()
        .secure(t -> t.sslContext(this.createSSLContext()))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout)
        .doOnConnected(connection -> connection.addHandlerLast(
            new ReadTimeoutHandler(this.readTimeout, TimeUnit.MILLISECONDS))
        );

    if (this.proxyHost != null) {
      httpClient = this.configureProxy(httpClient);
    }

    return httpClient;
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected SslContext createSSLContext() {
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

  @API(status = API.Status.EXPERIMENTAL)
  protected HttpClient configureProxy(HttpClient httpClient) {
    return httpClient.proxy(p -> p.type(ProxyProvider.Proxy.HTTP)
        .host(this.proxyHost)
        .port(this.proxyPort)
        .connectTimeoutMillis(this.connectionTimeout)
        .username(this.proxyUser)
        .password(u -> this.proxyPassword)
    );
  }
}
