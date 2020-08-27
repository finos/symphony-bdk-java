package com.symphony.bdk.core.api.invoker;

import org.apiguardian.api.API;

/**
 * Builder class to create a new instance of an {@link ApiClient}
 */
@API(status = API.Status.STABLE)
public interface ApiClientBuilder {
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
   * @param keyStoreBytes content of the key store (aka client certificate)
   * @param keyStorePassword password of the key store
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withKeyStore(byte[] keyStoreBytes, String keyStorePassword);

  /**
   * @param trustStoreBytes content of the trust store
   * @param trustStorePassword password of the truststore
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withTrustStore(byte[] trustStoreBytes, String trustStorePassword);

  /**
   * @param key name of the header to be added in all HTTP calls
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
   * Set the connection timeout (in milliseconds). A value of 0 means no timeout, otherwise values
   * must be between 1 and {@link Integer#MAX_VALUE}. If not set, connection timeout will be 15000.
   *
   * @param connectionTimeout Connection timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withConnectionTimeout(int connectionTimeout);

  /**
   * Set the read timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be
   * between 1 and {@link Integer#MAX_VALUE}. If not set, read timeout will be 60000.
   *
   * @param readTimeout Read timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder withReadTimeout(int readTimeout);
}
