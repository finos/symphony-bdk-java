package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.exception.BdkExtensionException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class ExtensionService {

  private final List<BdkExtension> extensions;

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
    this.extensions = Collections.synchronizedList(new ArrayList<>());
  }

  public void register(Class<? extends BdkExtension> extClz) {
    log.debug("Registering extension <{}>", extClz);

    if (this.find(extClz).isPresent()) {
      throw new IllegalStateException("Extension <" + extClz + "> has already been registered.");
    }

    BdkExtension extension;

    try {
      extension = extClz.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new BdkExtensionException("Extension <" + extClz + "> must have a default constructor", e);
    }

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

    try {
      this.start(extension);
      this.extensions.add(extension);
      log.debug("Extension <{}> has successfully been registered", extClz);
    } catch (Exception ex) {
      log.warn("Failed to register extension <{}>", extClz, ex);
    }
  }

  public void unregister(Class<? extends BdkExtension> extClz) {
    log.debug("Unregistering extension <{}>", extClz);

    Optional<BdkExtension> extension = this.find(extClz);

    if (!extension.isPresent()) {
      throw new IllegalStateException("Extension <" + extClz + "> has not been registered.");
    }

    try {
      this.stop(extension.get());
      this.extensions.remove(extension.get());
      log.debug("Extension <{}> has successfully been unregistered", extClz);
    } catch (Exception ex) {
      log.warn("Failed to unregister extension <{}>", extClz, ex);
    }
  }

  @SuppressWarnings("unchecked")
  public <S, E extends BdkExtensionServiceProvider<S>> S service(Class<E> extClz) {

    Optional<BdkExtension> extension = this.find(extClz);

    if (!extension.isPresent()) {
      throw new IllegalStateException("Extension <" + extClz + "> is not registered.");
    }

    return ((BdkExtensionServiceProvider<S>) extension.get()).getService();
  }

  private Optional<BdkExtension> find(Class<?> extClz) {
    return this.extensions.stream().filter(e -> extClz.equals(e.getClass())).findFirst();
  }

  private void start(BdkExtension extension) {
    if (extension instanceof BdkExtensionLifecycleAware) {
      ((BdkExtensionLifecycleAware) extension).start();
    }
  }

  private void stop(BdkExtension extension) {
    if (extension instanceof BdkExtensionLifecycleAware) {
      ((BdkExtensionLifecycleAware) extension).start();
    }
  }
}
