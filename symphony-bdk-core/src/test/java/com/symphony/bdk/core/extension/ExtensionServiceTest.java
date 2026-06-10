package com.symphony.bdk.core.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.exception.BdkExtensionException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionLifecycle;
import com.symphony.bdk.extension.BdkExtensionService;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ExtensionServiceTest {

  @Mock
  private ApiClientFactory apiClientFactory;
  @Mock
  private AuthSession authSession;
  @Mock
  private RetryWithRecoveryBuilder<?> retryBuilder;

  private BdkConfig config;
  private ExtensionService extensionService;

  @BeforeEach
  void setUp() {
    this.config = new BdkConfig();
    this.extensionService = new ExtensionService(apiClientFactory, authSession, retryBuilder, config);
  }

  @Test
  void shouldRegisterExtensionService() {
    this.extensionService.register(TestExtensionWithService.class);
    assertThat(this.extensionService.service(TestExtensionWithService.class)).isNotNull();
  }

  @Test
  void shouldFailToGetServiceIfExtensionWasNotRegistered() {
    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> this.extensionService.service(TestExtensionWithService.class));
    assertThat(ex).hasMessage("Extension <" + TestExtensionWithService.class + "> is not registered");
  }

  @Test
  void shouldFailToRegisterExtensionWithoutDefaultConstructor() {
    BdkExtensionException ex = assertThrows(BdkExtensionException.class,
        () -> this.extensionService.register(TestExtensionWithoutDefaultConstructor.class));
    assertThat(ex).hasMessage("Extension <" + TestExtensionWithoutDefaultConstructor.class + "> must have a default constructor");
  }

  @Test
  void shouldNotRegisterTwiceAnExtension() {
    this.extensionService.register(TestExtensionWithService.class);
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> this.extensionService.register(TestExtensionWithService.class));
    assertThat(ex).hasMessage("Extension <" + TestExtensionWithService.class + "> has already been registered");
  }

  @Test
  void shouldNotRegisterTwiceAnExtensionInstance() {
    this.extensionService.register(new TestExtensionWithService());
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> this.extensionService.register(new TestExtensionWithService()));
    assertThat(ex).hasMessage("Extension <" + TestExtensionWithService.class + "> has already been registered");
  }

  @Test
  void shouldRegisterConfigAwareExtension() {
    Map<String, Object> extensions = new LinkedHashMap<>();
    extensions.put("test", Map.of("value", "hello"));
    this.config.setExtensions(extensions);

    this.extensionService.register(TestExtensionConfigAware.class);

    TestExtensionConfigAware.Config cfg = this.extensionService.service(TestExtensionConfigAware.class).getConfig();
    assertThat(cfg).isNotNull();
    assertThat(cfg.getValue()).isEqualTo("hello");
  }

  @Test
  void shouldThrowOnMissingExtensionConfigKey() {
    BdkExtensionException ex = assertThrows(BdkExtensionException.class,
        () -> this.extensionService.register(TestExtensionConfigAware.class));
    assertThat(ex.getMessage()).contains("test");
    assertThat(ex.getMessage()).contains("bdk.extensions.test");
  }

  @Test
  void shouldThrowOnMalformedExtensionConfig() {
    Map<String, Object> extensions = new LinkedHashMap<>();
    extensions.put("test", "not-a-map");
    this.config.setExtensions(extensions);

    BdkExtensionException ex = assertThrows(BdkExtensionException.class,
        () -> this.extensionService.register(TestExtensionConfigAware.class));
    assertThat(ex.getMessage()).contains("test");
    assertThat(ex.getMessage()).contains(TestExtensionConfigAware.Config.class.getName());
  }

  @Test
  void shouldRegisterApiClientFactoryAwareExtension() {
    this.extensionService.register(TestExtensionApiClientFactoryAware.class);
    assertThat(this.extensionService.service(TestExtensionApiClientFactoryAware.class).getApiClientFactory())
        .isEqualTo(this.apiClientFactory);
  }

  @Test
  void shouldRegisterAuthenticationAwareExtension() {
    this.extensionService.register(TestExtensionAuthenticationAware.class);
    assertThat(this.extensionService.service(TestExtensionAuthenticationAware.class).getAuthSession())
        .isEqualTo(this.authSession);
  }

  @Test
  void shouldRegisterRetryBuilderAwareExtension() {
    this.extensionService.register(TestExtensionRetryBuilderAware.class);
    assertThat(this.extensionService.service(TestExtensionRetryBuilderAware.class).getRetryBuilder())
        .isEqualTo(this.retryBuilder);
  }

  // Task 9.1: lifecycle callbacks invoked in correct order
  @Test
  void shouldCallLifecycleCallbacksInOrder() {
    LifecycleTracker tracker = new LifecycleTracker();
    this.extensionService.register(tracker);

    SymphonyBdk bdk = mock(SymphonyBdk.class);
    this.extensionService.onBdkStarted(bdk);
    assertThat(tracker.startedCalled).isTrue();
    assertThat(tracker.stoppedCalled).isFalse();

    this.extensionService.onBdkStopped();
    assertThat(tracker.stoppedCalled).isTrue();
  }

  @Test
  void shouldInjectBdkBeforeLifecycleStart() {
    BdkAwareLifecycleExtension ext = new BdkAwareLifecycleExtension();
    this.extensionService.register(ext);

    SymphonyBdk bdk = mock(SymphonyBdk.class);
    this.extensionService.onBdkStarted(bdk);

    assertThat(ext.bdkAtStart).isSameAs(bdk);
  }

  // Task 9.3: findMessageSenderOverride returns first, warns on multiple
  @Test
  void shouldReturnFirstMessageSenderOverride() {
    MessageSenderOverride override = mock(MessageSenderOverride.class);
    BdkExtension ext = (BdkExtension & BdkMessageSenderOverrideProvider) () -> override;
    this.extensionService.register(ext);

    Optional<MessageSenderOverride> result = this.extensionService.findMessageSenderOverride();
    assertThat(result).isPresent().contains(override);
  }

  @Test
  void shouldReturnEmptyWhenNoMessageSenderOverrideProvider() {
    this.extensionService.register(TestExtensionWithService.class);
    assertThat(this.extensionService.findMessageSenderOverride()).isEmpty();
  }

  // Task 9.4: findDatafeedEventSource returns first provider
  @Test
  void shouldReturnFirstDatafeedEventSource() {
    DatafeedEventSource source = mock(DatafeedEventSource.class);
    BdkExtension ext = (BdkExtension & BdkDatafeedEventSourceProvider) () -> source;
    this.extensionService.register(ext);

    Optional<DatafeedEventSource> result = this.extensionService.findDatafeedEventSource();
    assertThat(result).isPresent().contains(source);
  }

  @Test
  void shouldReturnEmptyWhenNoDatafeedEventSourceProvider() {
    this.extensionService.register(TestExtensionWithService.class);
    assertThat(this.extensionService.findDatafeedEventSource()).isEmpty();
  }

  // Task 4.7: warn on post-construction capability registration
  @Test
  void shouldNotThrowWhenCapabilityExtensionRegisteredPostConstruction() {
    this.extensionService.markCapabilitiesExtracted();

    MessageSenderOverride override = mock(MessageSenderOverride.class);
    BdkExtension ext = (BdkExtension & BdkMessageSenderOverrideProvider) () -> override;
    // Should log a warning but not throw
    this.extensionService.register(ext);
  }

  // --- helper inner classes ---

  static class LifecycleTracker implements BdkExtension, BdkExtensionLifecycle {
    boolean startedCalled = false;
    boolean stoppedCalled = false;

    @Override public void onBdkStarted() { startedCalled = true; }
    @Override public void onBdkStopped() { stoppedCalled = true; }
  }

  static class BdkAwareLifecycleExtension implements BdkExtension, BdkAware, BdkExtensionLifecycle {
    SymphonyBdk bdkAtStart = null;
    private SymphonyBdk injectedBdk;

    @Override public void setBdk(SymphonyBdk bdk) { this.injectedBdk = bdk; }
    @Override public void onBdkStarted() { this.bdkAtStart = this.injectedBdk; }
  }

  static class ServiceHolder implements BdkExtensionService {
  }

  static class ServiceExtension implements BdkExtension, BdkExtensionServiceProvider<ServiceHolder> {
    private final ServiceHolder service = new ServiceHolder();
    @Override public ServiceHolder getService() { return service; }
  }
}
