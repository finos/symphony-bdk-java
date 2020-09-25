package com.symphony.bdk.http.webclient;

import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.apiguardian.api.API;

/**
 * Provides new {@link ApiClientBuilderWebClient} implementation of the {@link ApiClientBuilder} interface.
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientBuilderProviderWebClient implements ApiClientBuilderProvider {

  /**
   * Creates a new {@link ApiClientBuilder} instance.
   * The provided builder instance will build an {@link ApiClientWebClient} instance.
   *
   * @return a new {@link ApiClientBuilder} instance.
   */
  @Override
  public ApiClientBuilder newInstance() {
    return new ApiClientBuilderWebClient();
  }
}
