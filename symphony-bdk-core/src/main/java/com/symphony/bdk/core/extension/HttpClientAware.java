package com.symphony.bdk.core.extension;

import com.symphony.bdk.http.api.HttpClient;

public interface HttpClientAware extends Extension {
  void setHttpClientBuilder(HttpClient.Builder builder);
}
