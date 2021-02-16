package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;

import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import lombok.Generated;
import org.apiguardian.api.API;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fluent builder for advanced configuration of the {@link SymphonyBdk} entry point.
 */
@Generated // a bit tricky to get acceptable coverage
@API(status = API.Status.STABLE)
public class SymphonyBdkBuilder {

  private BdkConfig config;
  private ApiClientBuilderProvider apiClientBuilderProvider;
  private AuthenticatorFactory authenticatorFactory;
  private ApiClientFactory apiClientFactory;

  /**
   * With {@link BdkConfig} POJO.
   *
   * @param config config POJO.
   * @return updated builder.
   */
  public SymphonyBdkBuilder config(@Nonnull BdkConfig config) {
    this.config = config;
    return this;
  }

  /**
   * With config from Symphony directory.
   *
   * @param relPath relative configuration file location from your ${user.home}/.symphony directory.
   * @return updated builder.
   * @throws BdkConfigException config file loading issue.
   */
  public SymphonyBdkBuilder configFromSymphonyDir(@Nonnull String relPath) throws BdkConfigException {
    this.config = BdkConfigLoader.loadFromSymphonyDir(relPath);
    return this;
  }

  /**
   * With config from classpath.
   *
   * @param configPath relative classpath location of the configuration file.
   * @return updated builder.
   * @throws BdkConfigException config file loading issue.
   */
  public SymphonyBdkBuilder configFromClasspath(@Nonnull String configPath) throws BdkConfigException {
    this.config = BdkConfigLoader.loadFromClasspath(configPath);
    return this;
  }

  /**
   * With config from {@link InputStream}.
   *
   * @param inputStream configuration file {@link InputStream} content.
   * @return updated builder.
   * @throws BdkConfigException config file loading issue.
   */
  public SymphonyBdkBuilder configFromInputStream(@Nonnull InputStream inputStream) throws BdkConfigException {
    this.config = BdkConfigLoader.loadFromInputStream(inputStream);
    return this;
  }

  /**
   * With config from file system.
   *
   * @param configPath absolute path to the configuration file.
   * @return updated builder.
   * @throws BdkConfigException config file loading issue.
   */
  public SymphonyBdkBuilder configFromFile(@Nonnull String configPath) throws BdkConfigException {
    this.config = BdkConfigLoader.loadFromFile(configPath);
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
   * With custom {@link AuthenticatorFactory} instance.
   *
   * @param authenticatorFactory a custom {@link AuthenticatorFactory} instance.
   * @return updated builder.
   */
  public SymphonyBdkBuilder authenticatorFactory(@Nullable AuthenticatorFactory authenticatorFactory) {
    this.authenticatorFactory = authenticatorFactory;
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
      this.authenticatorFactory = new AuthenticatorFactory(this.config, this.apiClientFactory);
    }

    return new SymphonyBdk(this.config, this.apiClientFactory, this.authenticatorFactory);
  }
}
