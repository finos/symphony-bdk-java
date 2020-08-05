package com.symphony.bdk.core.api.invoker.jersey2;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientProvider;

/**
 *
 */
public class ApiClientProviderJersey2 implements ApiClientProvider {

  @Override
  public ApiClient newInstance() {
    return new ApiClientJersey2();
  }
}
