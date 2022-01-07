package com.symphony.bdk.extension;

import org.apiguardian.api.API;

/**
 * {@code BdkExtensionServiceProvider} defines the API of {@link BdkExtension} that wish to provide additional services
 * to BDK developers.
 *
 * <p>Extensions that implement {@code BdkExtensionServiceProvider} must also implement {@link BdkExtension}.
 *
 * @param <S> Type of the service.
 * @see BdkExtension
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkExtensionServiceProvider<S extends BdkExtensionService> {

  /**
   * Returns the extension service instance.
   *
   * @return extension service instance.
   */
  S getService();
}
