package com.symphony.bdk.http.jdk;

import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.apiguardian.api.API;

/**
 * Provides new {@link ApiClientBuilderJdk} implementation of the {@link ApiClientBuilder} interface.
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientBuilderProviderJdk implements ApiClientBuilderProvider {

  /**
   * Creates a new {@link ApiClientBuilder} instance.
   * The provided builder instance will build an {@link ApiClientJdk} instance.
   *
   * @return a new {@link ApiClientBuilder} instance.
   */
  @Override
  public ApiClientBuilder newInstance() {
    return new ApiClientBuilderJdk();
  }
}
