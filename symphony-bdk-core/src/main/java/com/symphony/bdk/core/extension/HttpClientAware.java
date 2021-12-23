package com.symphony.bdk.core.extension;

import com.symphony.bdk.http.api.HttpClient;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public interface HttpClientAware extends Extension {
  void setHttpClientBuilder(HttpClient.Builder builder);
}
