package com.symphony.bdk.spring.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * Test class for the {@link BdkCoreConfig}. Mainly for coverage...
 */
class BdkCoreConfigTest {

  @Test
  void shouldFailToCreateBotSession() throws Exception {

    final BdkCoreConfig config = new BdkCoreConfig();
    final AuthenticatorFactory authFactory = mock(AuthenticatorFactory.class);
    final BotAuthenticator botAuthenticator = mock(BotAuthenticator.class);

    when(botAuthenticator.authenticateBot()).thenThrow(AuthUnauthorizedException.class);
    when(authFactory.getBotAuthenticator()).thenReturn(botAuthenticator);

    assertThrows(BeanInitializationException.class, () -> config.botSession(authFactory));
  }

  @Test
  void shouldCreateApiClientFactory() {
    final BdkCoreConfig config = new BdkCoreConfig();
    final SymphonyBdkCoreProperties props = new SymphonyBdkCoreProperties();
    assertNotNull(config.apiClientFactory(props));
  }

  @Test
  void shouldCreateKeyAuthApiClient() {
    final BdkCoreConfig config = new BdkCoreConfig();
    final ApiClientFactory factory = mock(ApiClientFactory.class);
    when(factory.getKeyAuthClient()).thenReturn(mock(ApiClient.class));
    assertNotNull(config.keyAuthApiClient(factory));
  }

  @Test
  void shouldCreateSessionAuthApiClient() {
    final BdkCoreConfig config = new BdkCoreConfig();
    final ApiClientFactory factory = mock(ApiClientFactory.class);
    when(factory.getSessionAuthClient()).thenReturn(mock(ApiClient.class));
    assertNotNull(config.sessionAuthApiClient(factory));
  }

  @Test
  void shouldCreateExtensionAppAuthenticator() throws AuthInitializationException {
    final BdkCoreConfig config = new BdkCoreConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactory.class);
    final ExtensionAppAuthenticator appAuthenticator = mock(ExtensionAppAuthenticator.class);

    when(authenticatorFactory.getExtensionAppAuthenticator()).thenReturn(appAuthenticator);
    assertNotNull(config.extensionAppAuthenticator(authenticatorFactory));
  }

  @Test
  void shouldFailCreateExtensionAppAuthenticator() throws AuthInitializationException {
    final BdkCoreConfig config = new BdkCoreConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactory.class);

    when(authenticatorFactory.getExtensionAppAuthenticator()).thenThrow(AuthInitializationException.class);
    assertThrows(BeanInitializationException.class, () -> config.extensionAppAuthenticator(authenticatorFactory));
  }
}
