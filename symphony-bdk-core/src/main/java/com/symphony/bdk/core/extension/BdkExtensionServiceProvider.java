package com.symphony.bdk.core.extension;

/**
 * {@code BdkExtensionServiceProvider} defines the API or {@link BdkExtension} that wish to provide additional service
 * to BDK developers.
 *
 * <p>Extensions that implement {@code BeforeAllCallback} must also implement {@link BdkExtension}.
 *
 * <p>Concrete implementation often implement {@link BdkAuthenticationAware}, {@link BdkConfigAware}
 * or {@link BdkApiClientFactoryAware} as well.
 *
 * @param <S>
 */
public interface BdkExtensionServiceProvider<S> {

  S getService();
}
