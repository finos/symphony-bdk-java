package com.symphony.bdk.http.jersey2;

import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.apiguardian.api.API;

/**
 * Provides new {@link ApiClientBuilderJersey2} implementation of the {@link ApiClientBuilder} interface.
 *
 * @deprecated in favor of {@code com.symphony.bdk.http.jdk.ApiClientBuilderProviderJdk} (module
 * {@code symphony-bdk-http-jdk}), the new default HTTP implementation for {@code symphony-bdk-core}, which has no
 * third-party HTTP dependency. This module keeps shipping and working exactly as before; this is a soft,
 * non-removing signal, not a functional change.
 */
@API(status = API.Status.DEPRECATED)
public class ApiClientBuilderProviderJersey2 implements ApiClientBuilderProvider {

  /**
   * Creates a new {@link ApiClientBuilder} instance.
   * The provided builder instance will build an {@link ApiClientJersey2} instance.
   *
   * @return a new {@link ApiClientBuilder} instance.
   */
  @Override
  public ApiClientBuilder newInstance() {
    return new ApiClientBuilderJersey2();
  }
}
