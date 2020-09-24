package com.symphony.bdk.http.webclient;

import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

public class ApiClientBuilderProviderWebClient implements ApiClientBuilderProvider {

  @Override
  public ApiClientBuilder newInstance() {
    return new ApiClientBuilderWebClient();
  }
}
