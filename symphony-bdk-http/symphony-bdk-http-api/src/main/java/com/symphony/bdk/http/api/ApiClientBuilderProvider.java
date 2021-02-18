package com.symphony.bdk.http.api;

/**
 * New {@link ApiClientBuilder} instances provider.
 */
@FunctionalInterface
public interface ApiClientBuilderProvider {

  /**
   * Creates a new {@link ApiClientBuilder} instance.
   *
   * @return a new {@link ApiClientBuilder} instance.
   */
  ApiClientBuilder newInstance();
}
