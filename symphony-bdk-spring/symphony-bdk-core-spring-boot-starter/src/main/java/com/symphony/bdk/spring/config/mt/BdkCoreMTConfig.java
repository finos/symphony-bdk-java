package com.symphony.bdk.spring.config.mt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkBotConfig;
import com.symphony.bdk.core.config.model.BdkClientConfig;
import com.symphony.bdk.core.config.model.BdkRsaKeyConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.jersey2.ApiClientBuilderProviderJersey2;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.model.TenantRequestContext;
import com.symphony.bdk.spring.service.BdkApiClientRegister;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.freemarker.FreeMarkerEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * Configuration and injection of the main BDK/Core classes as beans within the Spring application context.
 */
@Slf4j
@ConditionalOnProperty(prefix = "bdk", name = "multi-tenant", havingValue = "true")
public class BdkCoreMTConfig {

  @Bean(name = "requestSymphonyBdkCoreProperties")
  @ConditionalOnMissingBean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public SymphonyBdkCoreProperties requestSymphonyBdkCoreProperties(
      SymphonyBdkCoreProperties properties, ObjectMapper objectMapper, TenantRequestContext tenantRequestContext )
      throws JsonProcessingException {
    // TODO find a better way to clone it
    String baseConfig = objectMapper.writeValueAsString(properties);
    SymphonyBdkCoreProperties bdkCoreProperties = objectMapper.readValue(baseConfig, SymphonyBdkCoreProperties.class);

    bdkCoreProperties.setHost(tenantRequestContext.getTenantHost());
    return bdkCoreProperties;
  }
  

  @Bean
  @ConditionalOnMissingBean
  public ApiClientFactory apiClientFactory(
      @Qualifier("requestSymphonyBdkCoreProperties") SymphonyBdkCoreProperties properties) {
    return new ApiClientFactory(properties,
        new ApiClientBuilderProviderJersey2()); // TODO create RestTemplate/or WebClient implementation
  }

  @Bean(name = "agentApiClient")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient agentApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient agentClient = bdkApiClientRegister.get("agentApiClient");
    if (agentClient == null) {
      agentClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("agentApiClient", agentClient);
    }
    return agentClient;
  }

  @Bean(name = "datafeedAgentApiClient")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient datafeedAgentApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient datafeedAgentApiClient = bdkApiClientRegister.get("datafeedAgentApiClient");
    if (datafeedAgentApiClient == null) {
      datafeedAgentApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("datafeedAgentApiClient", datafeedAgentApiClient);
    }
    return datafeedAgentApiClient;
  }

  @Bean(name = "podApiClient")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient podApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient podApiClient = bdkApiClientRegister.get("podApiClient");
    if (podApiClient == null) {
      podApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("podApiClient", podApiClient);
    }
    return podApiClient;
  }

  @Bean(name = "relayApiClient")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient relayApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient relayApiClient = bdkApiClientRegister.get("relayApiClient");
    if (relayApiClient == null) {
      relayApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("relayApiClient", relayApiClient);
    }
    return relayApiClient;
  }

  @Bean(name = "loginApiClient")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient loginApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient loginApiClient = bdkApiClientRegister.get("loginApiClient");
    if (loginApiClient == null) {
      loginApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("loginApiClient", loginApiClient);
    }
    return loginApiClient;
  }

  @Bean(name = "keyAuthApiClient")
  @ConditionalOnProperty("bdk.bot.certificatePath")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient keyAuthApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient keyAuthApiClient = bdkApiClientRegister.get("keyAuthApiClient");
    if (keyAuthApiClient == null) {
      keyAuthApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("keyAuthApiClient", keyAuthApiClient);
    }
    return keyAuthApiClient;
  }

  @Bean(name = "sessionAuthApiClient")
  @ConditionalOnProperty("bdk.bot.certificatePath")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ApiClient sessionAuthApiClient(BdkApiClientRegister<ApiClient> bdkApiClientRegister,
      ApiClientFactory apiClientFactory) {
    ApiClient sessionAuthApiClient = bdkApiClientRegister.get("sessionAuthApiClient");
    if (sessionAuthApiClient == null) {
      sessionAuthApiClient = apiClientFactory.getAgentClient();
      bdkApiClientRegister.set("sessionAuthApiClient", sessionAuthApiClient);
    }
    return sessionAuthApiClient;
  }

  @Bean
  @ConditionalOnMissingBean
  public AuthenticatorFactory authenticatorFactory(
      @Qualifier("requestSymphonyBdkCoreProperties") SymphonyBdkCoreProperties properties,
      ApiClientFactory apiClientFactory) {
    return new AuthenticatorFactory(properties, apiClientFactory);
  }

  @Bean
  @ConditionalOnMissingBean
  public TemplateEngine templateEngine() {
    return new FreeMarkerEngine();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty("bdk.bot.username")
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public AuthSession botSession(AuthenticatorFactory authenticatorFactory) {
    try {
      // TODO : cache
      return authenticatorFactory.getBotAuthenticator().authenticateBot();
    } catch (AuthUnauthorizedException | AuthInitializationException e) {
      throw new BeanInitializationException("Unable to authenticate bot", e);
    }
  }
}
