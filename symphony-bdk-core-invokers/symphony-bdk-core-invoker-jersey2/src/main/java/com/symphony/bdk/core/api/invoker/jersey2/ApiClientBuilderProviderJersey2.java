package com.symphony.bdk.core.api.invoker.jersey2;

import com.symphony.bdk.core.api.invoker.ApiClientBuilder;
import com.symphony.bdk.core.api.invoker.ApiClientBuilderProvider;

/**
 * Provides new {@link ApiClientBuilderJersey2} implementation of the {@link ApiClientBuilder} interface.
 */
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
