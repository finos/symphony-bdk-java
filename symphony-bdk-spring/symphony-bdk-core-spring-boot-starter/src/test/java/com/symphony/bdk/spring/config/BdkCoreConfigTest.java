package com.symphony.bdk.spring.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.*;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthenticatorFactoryImpl;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkCommonJwtConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanInitializationException;

import java.util.Optional;

/**
 * Test class for the {@link BdkCoreConfig}. Mainly for coverage...
 */
class BdkCoreConfigTest {

  @Test
  void shouldFailToCreateBotSession() throws Exception {

    final BdkCoreConfig config = new BdkCoreConfig();
    final AuthenticatorFactory authFactory = mock(AuthenticatorFactoryImpl.class);
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
  void shouldCreatePodClient() {
    final BdkCoreConfig config = new BdkCoreConfig();
    final BdkConfig bdkConfig = new BdkConfig();
    BdkCommonJwtConfig bdkCommonJwtConfig = new BdkCommonJwtConfig();
    bdkCommonJwtConfig.setEnabled(true);
    bdkConfig.setCommonJwt(bdkCommonJwtConfig);
    final ApiClientFactory factory = mock(ApiClientFactory.class);
    final AuthSession authSession = mock(AuthSession.class);
    when(factory.getPodClient()).thenReturn(mock(ApiClient.class));
    assertNotNull(config.podApiClient(factory, Optional.ofNullable(authSession), bdkConfig));
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
    final BdkOboServiceConfig config = new BdkOboServiceConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactoryImpl.class);
    final ExtensionAppAuthenticator appAuthenticator = mock(ExtensionAppAuthenticator.class);

    when(authenticatorFactory.getExtensionAppAuthenticator()).thenReturn(appAuthenticator);
    assertNotNull(config.extensionAppAuthenticator(authenticatorFactory));
  }

  @Test
  void shouldFailCreateExtensionAppAuthenticator() throws AuthInitializationException {
    final BdkOboServiceConfig config = new BdkOboServiceConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactoryImpl.class);

    when(authenticatorFactory.getExtensionAppAuthenticator()).thenThrow(AuthInitializationException.class);
    assertThrows(BeanInitializationException.class, () -> config.extensionAppAuthenticator(authenticatorFactory));
  }

  @Test
  void shouldCreateOboAuthenticator() throws AuthInitializationException {
    final BdkOboServiceConfig config = new BdkOboServiceConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactoryImpl.class);
    final OboAuthenticator oboAuthenticator = mock(OboAuthenticator.class);

    when(authenticatorFactory.getOboAuthenticator()).thenReturn(oboAuthenticator);
    assertNotNull(config.oboAuthenticator(authenticatorFactory));
  }

  @Test
  void shouldFailCreateOboAuthenticator() throws AuthInitializationException {
    final BdkOboServiceConfig config = new BdkOboServiceConfig();
    final AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactoryImpl.class);

    when(authenticatorFactory.getOboAuthenticator()).thenThrow(AuthInitializationException.class);
    assertThrows(BeanInitializationException.class, () -> config.oboAuthenticator(authenticatorFactory));
  }
}
