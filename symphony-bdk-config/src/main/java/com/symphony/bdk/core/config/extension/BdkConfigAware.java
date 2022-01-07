package com.symphony.bdk.core.config.extension;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.apiguardian.api.API;

/**
 * Interface to be implemented by any {@code com.symphony.bdk.extension.BdkExtension} that wishes to access and read
 * the BDK configuration.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkConfigAware {

  /**
   * Set the {@link BdkConfig} object.
   *
   * @param config the {@code BdkConfig} instance to be used by this object
   */
  void setConfiguration(BdkConfig config);
}
