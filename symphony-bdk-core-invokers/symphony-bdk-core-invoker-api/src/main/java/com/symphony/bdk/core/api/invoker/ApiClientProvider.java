package com.symphony.bdk.core.api.invoker;

/**
 * New {@link ApiClient} instances provider.
 */
public interface ApiClientProvider {

  /**
   * Creates a new {@link ApiClient} instance.
   *
   * @return a new {@link ApiClient} instance.
   */
  ApiClient newInstance();
}
