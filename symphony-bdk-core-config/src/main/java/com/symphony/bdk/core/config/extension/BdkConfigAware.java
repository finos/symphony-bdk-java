package com.symphony.bdk.core.config.extension;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.apiguardian.api.API;

/**
 * This is interface can be implemented if your extension needs to use the {@link BdkConfig}.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkConfigAware {

  /**
   * Injects the {@link BdkConfig} object.
   *
   * @param config The BDK configuration.
   */
  void setConfiguration(BdkConfig config);
}
