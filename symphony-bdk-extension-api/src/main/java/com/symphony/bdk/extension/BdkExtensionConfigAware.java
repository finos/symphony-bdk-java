package com.symphony.bdk.extension;

import org.apiguardian.api.API;

/**
 * Interface for BDK extensions that need typed per-extension configuration.
 *
 * <p>The BDK will deserialize the {@code BdkConfig.extensions.<key>} YAML block into an instance of {@code C}
 * and call {@link #setExtensionConfig(Object)} before {@code onBdkStarted} is invoked.
 *
 * <p>A {@code BdkExtensionException} is thrown if the key is absent or the YAML cannot be deserialized.
 *
 * @param <C> the extension-specific configuration class
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkExtensionConfigAware<C> {

  String getConfigKey();

  Class<C> getConfigClass();

  void setExtensionConfig(C config);
}
