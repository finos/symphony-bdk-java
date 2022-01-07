package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.extension.BdkConfigAware;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.exception.BdkExtensionException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service class for managing extensions.
 *
 * @see BdkExtension
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class ExtensionService {

  private final Map<Class<? extends BdkExtension>, BdkExtension> extensions;

  private final ApiClientFactory apiClientFactory;
  private final AuthSession botSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;
  private final BdkConfig config;

  public ExtensionService(
      @Nonnull ApiClientFactory apiClientFactory,
      @Nullable AuthSession botSession,
      @Nonnull RetryWithRecoveryBuilder<?> retryBuilder,
      @Nonnull BdkConfig config
  ) {
    this.apiClientFactory = apiClientFactory;
    this.botSession = botSession;
    this.retryBuilder = retryBuilder;
    this.config = config;
    this.extensions = Collections.synchronizedMap(new HashMap<>());
  }

  public void register(BdkExtension extension) {

    final Class<? extends BdkExtension> extClz = extension.getClass();

    if (extension instanceof BdkAuthenticationAware) {
      if (this.botSession == null) {
        log.info("Extension <{}> uses authentication, but it has not been configured in BDK config", extClz);
      } else {
        log.debug("Extension <{}> uses authentication", extClz);
        ((BdkAuthenticationAware) extension).setAuthSession(this.botSession);
      }
    }

    if (extension instanceof BdkApiClientFactoryAware) {
      log.debug("Extension <{}> uses the ApiClientFactory", extClz);
      ((BdkApiClientFactoryAware) extension).setApiClientFactory(this.apiClientFactory);
    }

    if (extension instanceof BdkRetryBuilderAware) {
      log.debug("Extension <{}> uses the RetryBuilder", extClz);
      ((BdkRetryBuilderAware) extension).setRetryBuilder(this.retryBuilder);
    }

    if (extension instanceof BdkConfigAware) {
      log.debug("Extension <{}> uses the configuration", extClz);
      ((BdkConfigAware) extension).setConfiguration(this.config);
    }

    this.extensions.put(extClz, extension);
  }

  /**
   * Registers and instantiates an extension.
   *
   * @param extClz Type of the extension.
   * @throws IllegalStateException if the extension has already been registered
   * @throws BdkExtensionException if the extension cannot be instantiated
   * @see BdkExtension
   */
  public void register(Class<? extends BdkExtension> extClz) {
    log.debug("Registering extension <{}>", extClz);

    if (this.extensions.get(extClz) != null) {
      throw new IllegalStateException("Extension <" + extClz + "> has already been registered");
    }

    BdkExtension extension;

    try {
      extension = extClz.getConstructor().newInstance();
      log.debug("Extension {} successfully instantiated", extClz);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new BdkExtensionException("Extension <" + extClz + "> must have a default constructor", e);
    }

    this.register(extension);
  }

  /**
   * Retrieves an extension service instance.
   *
   * @param extClz The extension class.
   * @param <S> Type of the extension service.
   * @param <E> Type of the extension.
   *
   * @return extension service instance.
   * @throws IllegalStateException if the extension is not registered
   * @see BdkExtension
   * @see BdkExtensionServiceProvider
   * @see ExtensionService#register(Class)
   */
  @SuppressWarnings("unchecked")
  public <S extends BdkExtensionService, E extends BdkExtensionServiceProvider<S>> S service(Class<E> extClz) {

    final BdkExtension extension = this.extensions.get(extClz);

    if (extension == null) {
      throw new IllegalStateException("Extension <" + extClz + "> is not registered");
    }

    return ((BdkExtensionServiceProvider<S>) extension).getService();
  }
}
