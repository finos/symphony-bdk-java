package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.client.ApiClientFactory;

import org.apiguardian.api.API;

/**
 * Interface to be implemented by any {@link com.symphony.bdk.extension.BdkExtension} that wishes to use the {@link ApiClientFactory}.
 *
 * @see com.symphony.bdk.extension.BdkExtension
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkApiClientFactoryAware {

  /**
   * Set the {@link ApiClientFactory} object.
   *
   * @param apiClientFactory the {@code ApiClientFactory} instance to be used by this object.
   */
  void setApiClientFactory(ApiClientFactory apiClientFactory);
}
