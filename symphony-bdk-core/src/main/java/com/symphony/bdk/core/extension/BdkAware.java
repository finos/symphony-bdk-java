package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.SymphonyBdk;

import org.apiguardian.api.API;

/**
 * Implemented by extensions that need access to the fully constructed {@link SymphonyBdk} instance.
 *
 * <p>{@link #setBdk(SymphonyBdk)} is called before {@code BdkExtensionLifecycle.onBdkStarted()},
 * so the bdk reference is available when the lifecycle callback fires.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkAware {

  void setBdk(SymphonyBdk bdk);
}
