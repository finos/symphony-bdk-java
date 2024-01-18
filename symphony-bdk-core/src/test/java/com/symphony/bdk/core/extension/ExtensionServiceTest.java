package com.symphony.bdk.core.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.exception.BdkExtensionException;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtensionServiceTest {

  @Mock
  private BdkConfig config;
  @Mock
  private ApiClientFactory apiClientFactory;
  @Mock
  private BotAuthSession authSession;
  @Mock
  private RetryWithRecoveryBuilder<?> retryBuilder;

  @InjectMocks
  private ExtensionService extensionService;

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
    this.extensionService.register(TestExtensionConfigAware.class);
    assertThat(this.extensionService.service(TestExtensionConfigAware.class).getConfig()).isEqualTo(this.config);
  }

  @Test
  void shouldRegisterApiClientFactoryAwareExtension() {
    this.extensionService.register(TestExtensionApiClientFactoryAware.class);
    assertThat(this.extensionService.service(TestExtensionApiClientFactoryAware.class).getApiClientFactory()).isEqualTo(this.apiClientFactory);
  }

  @Test
  void shouldRegisterAuthenticationAwareExtension() {
    this.extensionService.register(TestExtensionAuthenticationAware.class);
    assertThat(this.extensionService.service(TestExtensionAuthenticationAware.class).getAuthSession()).isEqualTo(this.authSession);
  }

  @Test
  void shouldRegisterRetryBuilderAwareExtension() {
    this.extensionService.register(TestExtensionRetryBuilderAware.class);
    assertThat(this.extensionService.service(TestExtensionRetryBuilderAware.class).getRetryBuilder()).isEqualTo(this.retryBuilder);
  }
}
