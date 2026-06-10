package com.symphony.bdk.core.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.exception.BdkExtensionException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionConfigAware;
import com.symphony.bdk.extension.BdkExtensionLifecycle;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Map<Class<? extends BdkExtension>, BdkExtension> extensions;

  private final ApiClientFactory apiClientFactory;
  private final AuthSession botSession;
  private final RetryWithRecoveryBuilder<?> retryBuilder;
  private final BdkConfig config;

  private boolean capabilitiesExtracted = false;

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
    this.extensions = Collections.synchronizedMap(new LinkedHashMap<>());
  }

  public void register(BdkExtension extension) {

    final Class<? extends BdkExtension> extClz = extension.getClass();

    this.checkAlreadyRegistered(extClz);

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

    if (extension instanceof BdkExtensionConfigAware) {
      log.debug("Extension <{}> uses typed extension config", extClz);
      injectExtensionConfig((BdkExtensionConfigAware<?>) extension);
    }

    if (capabilitiesExtracted) {
      if (extension instanceof BdkMessageSenderOverrideProvider) {
        log.warn("Extension <{}> implements BdkMessageSenderOverrideProvider but was registered after services were "
            + "constructed — the message sender override has no effect. Use SymphonyBdkBuilder.extension(Class) "
            + "to pre-register capability-providing extensions.", extClz);
      }
      if (extension instanceof BdkDatafeedEventSourceProvider) {
        log.warn("Extension <{}> implements BdkDatafeedEventSourceProvider but was registered after services were "
            + "constructed — the datafeed event source has no effect. Use SymphonyBdkBuilder.extension(Class) "
            + "to pre-register capability-providing extensions.", extClz);
      }
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

    this.checkAlreadyRegistered(extClz);

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
   * Returns the first registered {@link MessageSenderOverride} from a {@link BdkMessageSenderOverrideProvider}.
   * Logs a warning if more than one provider is registered.
   *
   * @return an {@link Optional} containing the first override, or empty if no provider is registered
   */
  public Optional<MessageSenderOverride> findMessageSenderOverride() {
    MessageSenderOverride first = null;
    int count = 0;
    for (BdkExtension ext : this.extensions.values()) {
      if (ext instanceof BdkMessageSenderOverrideProvider) {
        count++;
        if (first == null) {
          first = ((BdkMessageSenderOverrideProvider) ext).getMessageSenderOverride();
        }
      }
    }
    if (count > 1) {
      log.warn("More than one BdkMessageSenderOverrideProvider registered ({}). Only the first will be used.", count);
    }
    return Optional.ofNullable(first);
  }

  /**
   * Returns the first registered {@link DatafeedEventSource} from a {@link BdkDatafeedEventSourceProvider}.
   *
   * @return an {@link Optional} containing the first source, or empty if no provider is registered
   */
  public Optional<DatafeedEventSource> findDatafeedEventSource() {
    return this.extensions.values().stream()
        .filter(ext -> ext instanceof BdkDatafeedEventSourceProvider)
        .map(ext -> ((BdkDatafeedEventSourceProvider) ext).getDatafeedEventSource())
        .findFirst();
  }

  /**
   * Marks capabilities as extracted. Called after capabilities have been read from this service
   * and wired into {@code ServiceFactory}. Any provider registered after this point will trigger
   * a warning.
   */
  public void markCapabilitiesExtracted() {
    this.capabilitiesExtracted = true;
  }

  /**
   * Injects {@link SymphonyBdk} into all {@link BdkAware} extensions, then calls
   * {@link BdkExtensionLifecycle#onBdkStarted()} on all lifecycle extensions.
   *
   * @param bdk the fully constructed {@link SymphonyBdk} instance
   */
  public void onBdkStarted(@Nonnull SymphonyBdk bdk) {
    for (BdkExtension ext : this.extensions.values()) {
      if (ext instanceof BdkAware) {
        ((BdkAware) ext).setBdk(bdk);
      }
    }
    for (BdkExtension ext : this.extensions.values()) {
      if (ext instanceof BdkExtensionLifecycle) {
        ((BdkExtensionLifecycle) ext).onBdkStarted();
      }
    }
  }

  /**
   * Calls {@link BdkExtensionLifecycle#onBdkStopped()} on all registered lifecycle extensions.
   */
  public void onBdkStopped() {
    for (BdkExtension ext : this.extensions.values()) {
      if (ext instanceof BdkExtensionLifecycle) {
        ((BdkExtensionLifecycle) ext).onBdkStopped();
      }
    }
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

  @SuppressWarnings("unchecked")
  private <C> void injectExtensionConfig(BdkExtensionConfigAware<C> extension) {
    final String key = extension.getConfigKey();
    final Object rawConfig = this.config.getExtensions().get(key);
    if (rawConfig == null) {
      throw new BdkExtensionException(
          "Missing extension config key '" + key + "' for extension <" + extension.getClass() + ">. "
              + "Add 'bdk.extensions." + key + "' to your configuration.");
    }
    try {
      final C typed = MAPPER.convertValue(rawConfig, extension.getConfigClass());
      extension.setExtensionConfig(typed);
    } catch (IllegalArgumentException e) {
      throw new BdkExtensionException(
          "Failed to deserialize extension config key '" + key + "' into "
              + extension.getConfigClass().getName() + " for extension <" + extension.getClass() + ">",
          e);
    }
  }

  private void checkAlreadyRegistered(Class<? extends BdkExtension> extClz) {
    if (this.extensions.get(extClz) != null) {
      throw new IllegalStateException("Extension <" + extClz + "> has already been registered");
    }
  }
}
