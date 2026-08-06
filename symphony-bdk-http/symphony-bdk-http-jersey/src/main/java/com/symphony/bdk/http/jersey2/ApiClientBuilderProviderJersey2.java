package com.symphony.bdk.http.jersey2;

import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import org.apiguardian.api.API;

/**
 * Provides new {@link ApiClientBuilderJersey2} implementation of the {@link ApiClientBuilder} interface.
 */
@API(status = API.Status.INTERNAL)
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
