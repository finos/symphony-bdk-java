package com.symphony.bdk.http.jdk;

import static com.symphony.bdk.http.api.util.ApiUtils.addDefaultRootCaCertificates;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.auth.Authentication;
import com.symphony.bdk.http.api.util.ApiUtils;

import org.apiguardian.api.API;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Specific implementation of {@link ApiClientBuilder} which creates a new instance of an {@link ApiClientJdk},
 * backed by {@code java.net.http.HttpClient} rather than a third-party HTTP stack.
 *
 * <p><b>Please note that overriding this class is an {@link org.apiguardian.api.API.Status#EXPERIMENTAL} feature
 * that we offer to developers for {@link ApiClient} customization. The internal contract of this class (e.g.
 * protected methods) is subject to changes in the future.</b></p>
 *
 * <p>Two semantic gaps versus {@code ApiClientBuilderJersey2} are documented here rather than silently absorbed:
 * <ul>
 *   <li>{@code java.net.http.HttpClient} has no distinct read/socket timeout, only a connect timeout and a
 *   per-request total timeout. {@link #withReadTimeout} is mapped to the per-request total timeout as the
 *   closest available approximation.</li>
 *   <li>{@code java.net.http.HttpClient} has no request/response filter chain. {@link #addFilter} accepts a
 *   narrower request-mutation-only functional type, {@code Function<HttpRequest.Builder, HttpRequest.Builder>},
 *   instead of an arbitrary Jersey filter.</li>
 * </ul>
 * </p>
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientBuilderJdk implements ApiClientBuilder {

  private static final String TRUSTSTORE_FORMAT = "JKS";

  protected String basePath;
  protected byte[] keyStoreBytes;
  protected String keyStorePassword;
  protected byte[] trustStoreBytes;
  protected String trustStorePassword;
  protected Map<String, String> defaultHeaders;
  protected int connectionTimeout;
  protected int readTimeout;
  protected String proxyHost;
  protected int proxyPort;
  protected String proxyUser;
  protected String proxyPassword;
  protected Map<String, Authentication> authentications;
  protected List<Function<HttpRequest.Builder, HttpRequest.Builder>> filters;
  protected String temporaryFolderPath;

  public ApiClientBuilderJdk() {
    this.basePath = "https://acme.symphony.com";
    this.keyStoreBytes = null;
    this.keyStorePassword = null;
    this.trustStoreBytes = null;
    this.trustStorePassword = null;
    this.defaultHeaders = new HashMap<>();
    this.connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
    this.readTimeout = DEFAULT_READ_TIMEOUT;
    this.proxyHost = null;
    this.proxyPort = -1;
    this.proxyUser = null;
    this.proxyPassword = null;
    this.authentications = new HashMap<>();
    this.filters = new ArrayList<>();
    this.withUserAgent(ApiUtils.getUserAgent());
  }

  /**
   * Specific implementation of {@link ApiClientBuilder#build()} which returns an {@link ApiClientJdk} instance.
   */
  @Override
  public ApiClient build() {
    // Force HTTP/1.1: the JDK HttpClient's default (HTTP_2 with an automatic cleartext "h2c" upgrade attempt
    // for plain http:// requests) is not supported by the Symphony REST APIs' servers, which would otherwise
    // reject the upgrade with a 426.
    HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .sslContext(this.createSSLContext())
        .connectTimeout(Duration.ofMillis(this.connectionTimeout));

    if (this.proxyHost != null) {
      this.configureProxy(httpClientBuilder);
    }

    HttpClient httpClient = httpClientBuilder.build();

    final ApiClient apiClient =
        new ApiClientJdk(httpClient, this.basePath, this.defaultHeaders, this.temporaryFolderPath,
            Duration.ofMillis(this.readTimeout), this.filters);
    this.authentications.forEach(apiClient.getAuthentications()::put);
    return apiClient;
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
   *
   * <p>Maps directly to {@link HttpClient.Builder#connectTimeout(Duration)}.</p>
   */
  @Override
  public ApiClientBuilder withConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout == null ? DEFAULT_CONNECT_TIMEOUT : connectionTimeout;
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * <p><b>Note:</b> {@code java.net.http.HttpClient} exposes no distinct socket/read timeout, only a per-request
   * total timeout ({@link HttpRequest.Builder#timeout(Duration)}). This is the closest available approximation:
   * it bounds "the whole request took too long" rather than "no bytes arrived for N ms", which may behave
   * differently than {@code ApiClientBuilderJersey2}'s read timeout for large, legitimately slow responses (e.g.
   * large file downloads).</p>
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

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClientBuilder withAuthentication(String name, Authentication authentication) {
    this.authentications.put(name, authentication);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * <p><b>Note:</b> {@code java.net.http.HttpClient} has no request/response filter chain. This implementation
   * only accepts {@code Function<HttpRequest.Builder, HttpRequest.Builder>} instances (request-mutation only,
   * applied before send) and throws {@link IllegalArgumentException} for anything else.</p>
   *
   * <p><b>Warning:</b> due to type erasure, this method can only verify that {@code filter} is a
   * {@link Function}, not that its type parameters are actually
   * {@code <HttpRequest.Builder, HttpRequest.Builder>}. Supplying a {@link Function} with a different
   * signature will not fail here, but will throw a {@link ClassCastException} later, when the filter is
   * applied to an actual request.</p>
   */
  @Override
  @SuppressWarnings("unchecked")
  public ApiClientBuilder addFilter(Object filter) {
    if (!(filter instanceof Function)) {
      throw new IllegalArgumentException(
          "The filter " + filter.getClass().getName()
              + " must be an instance of " + Function.class.getName()
              + " (specifically Function<HttpRequest.Builder, HttpRequest.Builder>) to be used with the JDK "
              + "HttpClient HTTP client");
    }
    this.filters.add((Function<HttpRequest.Builder, HttpRequest.Builder>) filter);
    return this;
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected void configureProxy(HttpClient.Builder httpClientBuilder) {
    httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(this.proxyHost, this.proxyPort)));
    if (this.proxyUser != null) {
      httpClientBuilder.authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          if (getRequestorType() == RequestorType.PROXY) {
            return new PasswordAuthentication(
                ApiClientBuilderJdk.this.proxyUser,
                ApiClientBuilderJdk.this.proxyPassword == null
                    ? new char[0]
                    : ApiClientBuilderJdk.this.proxyPassword.toCharArray());
          }
          return null;
        }
      });
    }
  }

  @API(status = API.Status.EXPERIMENTAL)
  protected SSLContext createSSLContext() {
    try {
      TrustManagerFactory trustManagerFactory = null;
      KeyManagerFactory keyManagerFactory = null;

      if (isNotEmpty(this.trustStoreBytes) && isNotEmpty(this.trustStorePassword)) {
        final KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_FORMAT);
        trustStore.load(new ByteArrayInputStream(this.trustStoreBytes), this.trustStorePassword.toCharArray());
        addDefaultRootCaCertificates(trustStore);
        ApiUtils.logTrustStore(trustStore);
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
      }

      if (isNotEmpty(this.keyStoreBytes) && isNotEmpty(this.keyStorePassword)) {
        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new ByteArrayInputStream(this.keyStoreBytes), this.keyStorePassword.toCharArray());
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, this.keyStorePassword.toCharArray());
      }

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(
          keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers(),
          trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers(),
          null);
      return sslContext;
    } catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private static boolean isNotEmpty(byte[] bytes) {
    return bytes != null && bytes.length > 0;
  }

  private static boolean isNotEmpty(String str) {
    return str != null && !str.isEmpty();
  }
}
