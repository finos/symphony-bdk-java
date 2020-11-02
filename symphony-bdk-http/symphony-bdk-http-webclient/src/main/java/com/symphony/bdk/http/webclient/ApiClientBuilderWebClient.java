package com.symphony.bdk.http.webclient;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.RegularApiClient;
import com.symphony.bdk.http.api.util.ApiUtils;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apiguardian.api.API;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

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

  public ApiClientBuilderWebClient() {
    basePath = "";
    defaultHeaders = new HashMap<>();
    connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    readTimeout = DEFAULT_READ_TIMEOUT;
    withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientWebClient} instance.
   */
  @Override
  public RegularApiClient build() {
    WebClient.Builder builder = WebClient.builder();
    SslContext sslContext = this.createSSLContext();
    HttpClient httpConnector =
        HttpClient.create().secure(t -> t.sslContext(sslContext)).tcpConfiguration(tcpClient -> tcpClient.option(
            ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout)
            .doOnConnected(connection -> connection.addHandlerLast(
                new ReadTimeoutHandler(this.readTimeout / 1000))));

    builder.clientConnector(new ReactorClientHttpConnector(httpConnector));
    return new ApiClientWebClient(builder.baseUrl(this.basePath).build(), this.basePath, this.defaultHeaders);
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
}
