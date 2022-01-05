package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.client.ApiClientFactory;

import org.apiguardian.api.API;

/**
 * TODO javadoc
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkApiClientFactoryAware {

  void setApiClientFactory(ApiClientFactory apiClientFactory);
}
