package com.symphony.bdk.core.api.invoker;

/**
 * Builder class to create a new instance of an {@link ApiClient}
 */
public interface ApiClientBuilder {
  /**
   * @return a new {@link ApiClient} based on the previously called methods below.
   */
  ApiClient buildClient();

  /**
   * @param basePath base URL to be used for HTTP calls
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder basePath(String basePath);

  /**
   * @param userAgent user agent value to be put in the User-Agent header
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder userAgent(String userAgent);

  /**
   * @param keyStoreBytes content of the key store (aka client certificate)
   * @param keyStorePassword password of the key store
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder keyStore(byte[] keyStoreBytes, String keyStorePassword);

  /**
   * @param trustStoreBytes content of the trust store
   * @param trustStorePassword password of the truststore
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder trustStore(byte[] trustStoreBytes, String trustStorePassword);

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
  ApiClientBuilder temporaryFolderPath(String temporaryFolderPath);

  /**
   * Set the connection timeout (in milliseconds). A value of 0 means no timeout, otherwise values
   * must be between 1 and {@link Integer#MAX_VALUE}.
   *
   * @param connectionTimeout Connection timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder connectionTimeout(int connectionTimeout);

  /**
   * Set the read timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be
   * between 1 and {@link Integer#MAX_VALUE}.
   *
   * @param readTimeout Read timeout in milliseconds
   * @return the updated instance of {@link ApiClientBuilder}
   */
  ApiClientBuilder readTimeout(int readTimeout);
}
