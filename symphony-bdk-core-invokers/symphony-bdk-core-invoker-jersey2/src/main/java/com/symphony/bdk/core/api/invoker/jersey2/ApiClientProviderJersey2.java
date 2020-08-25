package com.symphony.bdk.core.api.invoker.jersey2;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientProvider;

/**
 * Provides new {@link ApiClientJersey2} implementation of the {@link ApiClient} interface.
 */
public class ApiClientProviderJersey2 implements ApiClientProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public ApiClient newInstance() {
    return new ApiClientJersey2();
  }
}
