package com.symphony.bdk.extension;

/**
 * {@code BdkExtensionServiceProvider} defines the API or {@link BdkExtension} that wish to provide additional service
 * to BDK developers.
 *
 * <p>Extensions that implement {@code BeforeAllCallback} must also implement {@link BdkExtension}.
 *
 * @param <S>
 */
public interface BdkExtensionServiceProvider<S> {

  S getService();
}
