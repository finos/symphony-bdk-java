package com.symphony.bdk.core.client;

import com.symphony.bdk.core.config.model.BdkAgentConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkCustomEnhancedAuthConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.auth.Authentication;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import jakarta.annotation.Nonnull;

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class EnhancedAuthApiClientFactory extends ApiClientFactory {

  private final Authentication enhancedAuthentication;
  private final BdkCustomEnhancedAuthConfig enhancedAuthConfig;

  public EnhancedAuthApiClientFactory(@Nonnull BdkConfig config, @Nonnull Authentication enhancedAuthentication) {
    super(config);
    this.enhancedAuthentication = enhancedAuthentication;
    this.enhancedAuthConfig = config.getEnhancedAuth();
  }

  @Override
  protected ApiClient buildAgentClient(String basePath, BdkAgentConfig agentConfig) {
    return addAuthScheme(super.getApiClientBuilder(basePath, agentConfig).build());
  }

  private ApiClient addAuthScheme(ApiClient apiClient) {
    apiClient.addEnforcedAuthenticationScheme(enhancedAuthConfig.getId());
    apiClient.getAuthentications().put(enhancedAuthConfig.getId(), enhancedAuthentication);
    return apiClient;
  }}
