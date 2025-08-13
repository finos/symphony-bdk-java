package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.impl.AuthenticatorFactoryImpl;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import lombok.Generated;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fluent builder for advanced configuration of the {@link SymphonyBdk} entry point.
 *
 * <p>Please note that some of the parameters (such as {@link ApiClientBuilderProvider}, {@link ApiClientFactory} or
 * {@link AuthenticatorFactoryImpl}) have to be used with caution.
 */
@Generated
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdkBuilder {

  private BdkConfig config;
  private ApiClientBuilderProvider apiClientBuilderProvider;
  private AuthenticatorFactory authenticatorFactory;
  private ApiClientFactory apiClientFactory;
  private final List<Class<? extends BdkExtension>> extensions = new ArrayList<>();

  /**
   * With {@link BdkConfig}.
   *
   * <p>The config object can be loaded in different ways (file system, classpath or {@link java.io.InputStream}) using
   * the {@link com.symphony.bdk.core.config.BdkConfigLoader}.
   *
   * @param config config POJO.
   * @return updated builder.
   * @see com.symphony.bdk.core.config.BdkConfigLoader
   */
  public SymphonyBdkBuilder config(@Nonnull BdkConfig config) {
    this.config = config;
    return this;
  }

  /**
   * With custom {@link ApiClientBuilderProvider} instance.
   *
   * @param apiClientBuilderProvider a custom {@link ApiClientBuilderProvider} instance.
   * @return updated builder.
   */
  public SymphonyBdkBuilder apiClientBuilderProvider(@Nullable ApiClientBuilderProvider apiClientBuilderProvider) {
    this.apiClientBuilderProvider = apiClientBuilderProvider;
    return this;
  }

  /**
   * With custom {@link ApiClientFactory} instance.
   *
   * @param apiClientFactory a custom {@link ApiClientFactory} instance.
   * @return updated builder.
   */
  public SymphonyBdkBuilder apiClientFactory(@Nullable ApiClientFactory apiClientFactory) {
    this.apiClientFactory = apiClientFactory;
    return this;
  }

  /**
   * With custom {@link AuthenticatorFactoryImpl} instance.
   *
   * @param authenticatorFactory a custom {@link AuthenticatorFactoryImpl} instance.
   * @return updated builder.
   */
  public SymphonyBdkBuilder authenticatorFactory(@Nullable AuthenticatorFactory authenticatorFactory) {
    this.authenticatorFactory = authenticatorFactory;
    return this;
  }

  /**
   * Registers a {@link BdkExtension}.
   *
   * @param extension {@link BdkExtension} class to be registered.
   * @return updated builder.
   */
  public SymphonyBdkBuilder extension(@Nonnull Class<? extends BdkExtension> extension) {
    this.extensions.add(extension);
    return this;
  }

  /**
   * Build new {@link SymphonyBdk}.
   *
   * @return a new {@link SymphonyBdk} instance.
   * @throws AuthInitializationException when unable to read/parse a RSA Private Key or a certificate
   * @throws AuthUnauthorizedException authentication issue (e.g. 401)
   */
  public SymphonyBdk build() throws AuthUnauthorizedException, AuthInitializationException {

    if (this.config == null) {
      throw new IllegalStateException("BDK configuration is mandatory.");
    }

    if (this.apiClientBuilderProvider == null) {
      this.apiClientBuilderProvider = ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class);
    }

    if (this.apiClientFactory == null) {
      this.apiClientFactory = new ApiClientFactory(this.config, this.apiClientBuilderProvider);
    }

    if (this.authenticatorFactory == null) {
      this.authenticatorFactory = new AuthenticatorFactoryImpl(this.config, this.apiClientFactory);
    }

    final SymphonyBdk bdk = new SymphonyBdk(this.config, this.apiClientFactory, this.authenticatorFactory);
    this.extensions.forEach(bdk.extensions()::register);
    return bdk;
  }
}
