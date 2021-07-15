package com.symphony.bdk.http.api;

import org.apiguardian.api.API;

/**
 * Builder class to create a new instance of an {@link ApiClient}
 */
@API(status = API.Status.STABLE)
public interface ApiClientBuilder {

  int DEFAULT_READ_TIMEOUT = 60_000;
  int DEFAULT_CONNECT_TIMEOUT = 15_000;
  int DEFAULT_CONNECTION_POOL_MAX = 20;

  /**
   * @return a new {@link ApiClient} based on the previously called methods below.
   */
  ApiClient build();

  /**
   * @param basePath base URL to be used for HTTP calls
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withBasePath(String basePath);

  /**
   * @param userAgent user agent value to be put in the User-Agent header
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withUserAgent(String userAgent);

  /**
   * @param keyStoreBytes    content of the key store (aka client certificate)
   * @param keyStorePassword password of the key store
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withKeyStore(byte[] keyStoreBytes, String keyStorePassword);

  /**
   * @param trustStoreBytes    content of the trust store
   * @param trustStorePassword password of the truststore
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withTrustStore(byte[] trustStoreBytes, String trustStorePassword);

  /**
   * @param key   name of the header to be added in all HTTP calls
   * @param value header value
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withDefaultHeader(String key, String value);

  /**
   * @param temporaryFolderPath temporary folder path where to download files sent as response
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withTemporaryFolderPath(String temporaryFolderPath);

  /**
   * Sets the connection timeout (in milliseconds). A value of 0 means no timeout, otherwise values
   * must be between 1 and {@link Integer#MAX_VALUE}. If not set or set null, connection timeout will be 15000.
   *
   * @param connectionTimeout Connection timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withConnectionTimeout(Integer connectionTimeout);

  /**
   * Sets the read timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be
   * between 1 and {@link Integer#MAX_VALUE}. If not set or set null, read timeout will be 60000.
   *
   * @param readTimeout Read timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withReadTimeout(Integer readTimeout);

  /**
   * Custom setting specific for {@link com.symphony.bdk.http.jersey2.ApiClientBuilderJersey2} only,
   * it set {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager#setMaxTotal}
   * If not set or set null, maximum connections per each route will be 20
   *
   * @param connectionPoolMax maximum connections in the pool
   * @return the updated instance of {@link ApiClientBuilder}
   *
   */
  default ApiClientBuilder withConnectionPoolMax(Integer connectionPoolMax){
    // Only ApiClientBuilderJersey2 override default method, otherwise it does nothing
    return this;
  }

  /**
   * Custom setting specific for {@link com.symphony.bdk.http.jersey2.ApiClientBuilderJersey2} only,
   * it set {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager#setDefaultMaxPerRoute}
   * If not set or set null, maximum connections per each route will be 20
   *
   * @param connectionPoolPerRoute maximum connections per each route
   * @return the updated instance of {@link ApiClientBuilder}
   *
   */
  default ApiClientBuilder withConnectionPoolPerRoute(Integer connectionPoolPerRoute){
    // Only ApiClientBuilderJersey2 override default method, otherwise it does nothing
    return this;
  }

  /**
   * Sets a proxy host and port.
   *
   * @param proxyHost the proxy host
   * @param proxyPort the proxy port
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withProxy(String proxyHost, int proxyPort);

  /**
   * Sets proxy credentials.
   *
   * @param proxyUser     the proxy user to be specified
   * @param proxyPassword the proxy password to be specified
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withProxyCredentials(String proxyUser, String proxyPassword);
}
