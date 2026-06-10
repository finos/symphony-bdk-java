package com.symphony.bdk.extension;

import org.apiguardian.api.API;

/**
 * Lifecycle interface for BDK extensions. Implement to receive startup and shutdown callbacks.
 *
 * <p>Both methods have default no-op implementations — extensions only need to override the ones they care about.
 *
 * <p>{@link #onBdkStarted()} is called after the {@code SymphonyBdk} instance is fully constructed.
 * {@link #onBdkStopped()} is called when the BDK is closed or the JVM shutdown hook fires.
 *
 * <p>Extensions that also need access to the {@code SymphonyBdk} instance after construction should implement
 * {@code com.symphony.bdk.core.extension.BdkAware} in addition to this interface.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkExtensionLifecycle {

  default void onBdkStarted() {}

  default void onBdkStopped() {}
}
