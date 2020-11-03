package com.symphony.bdk.core.client;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.core.client.lb.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.core.client.lb.LoadBalancedApiClient;
import com.symphony.bdk.core.client.lb.RegularLoadBalancedApiClient;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;
import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.config.model.BdkAuthenticationConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkSslConfig;
import com.symphony.bdk.core.util.ServiceLookup;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.RegularApiClient;

import com.symphony.bdk.http.api.util.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Factory responsible for creating {@link ApiClient} instances for each main Symphony's components
 * :
 * <ul>
 *   <li>Agent</li>
 *   <li>KeyManager</li>
 *   <li>Pod</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientFactory {

  private final BdkConfig config;
  private final ApiClientBuilderProvider apiClientBuilderProvider;

  public ApiClientFactory(@Nonnull BdkConfig config) {
    this(config, ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class));
  }

  public ApiClientFactory(@Nonnull BdkConfig config, @Nonnull ApiClientBuilderProvider apiClientBuilderProvider) {
    this.config = config;
    this.apiClientBuilderProvider = apiClientBuilderProvider;
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for Login API.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getLoginClient() {
    return buildClient(this.config.getPod().getBasePath() + "/login");
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for Pod API.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getPodClient() {
    return buildClient(this.config.getPod().getBasePath() + "/pod");
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for KeyManager API.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getRelayClient() {
    return buildClient(this.config.getKeyManager().getBasePath() + "/relay");
  }

  public ApiClient getAgentClient() {
    if (config.getAgentLoadBalancing() != null) {
      return new RegularLoadBalancedApiClient(this.config.getAgentLoadBalancing(), this);
    }
    return getRegularAgentClient();
  }

  public ApiClient getDatafeedAgentClient() {
    if (config.getAgentLoadBalancing() != null) {
      return new DatafeedLoadBalancedApiClient(this.config.getAgentLoadBalancing(), this);
    }
    return getRegularAgentClient();
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for Agent API.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getRegularAgentClient() {
    return getRegularAgentClient(this.config.getAgent().getBasePath());
  }

  public RegularApiClient getRegularAgentClient(String agentBasePath) {
    return buildClient(agentBasePath + "/agent");
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for the SessionAuth API. This only works with a
   * certification configured.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getSessionAuthClient() {
    return buildClientWithCertificate(this.config.getSessionAuth().getBasePath() + "/sessionauth", this.config.getBot());
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for the SessionAuth API using in Extension App Authentication.
   * This only works with a extension app authentication configured
   *
   * @return a new {@link RegularApiClient} instance.
   */
  public RegularApiClient getExtAppSessionAuthClient() {
    return buildClientWithCertificate(this.config.getSessionAuth().getBasePath() + "/sessionauth", this.config.getApp());
  }

  /**
   * Returns a fully initialized {@link RegularApiClient} for the KayAuth API. This only works with a
   * certification configured.
   *
   * @return an new {@link RegularApiClient} instance.
   */
  public RegularApiClient getKeyAuthClient() {
    return buildClientWithCertificate(this.config.getKeyManager().getBasePath() + "/keyauth", this.config.getBot());
  }

  private RegularApiClient buildClient(String basePath) {
    return getApiClientBuilder(basePath).build();
  }

  private RegularApiClient buildClientWithCertificate(String basePath, BdkAuthenticationConfig config) {
    if (!config.isCertificateAuthenticationConfigured()) {
      throw new ApiClientInitializationException("For certificate authentication, " +
          "certificatePath and certificatePassword must be set");
    }

    byte[] certificateBytes;
    if (isNotEmpty(config.getCertificateContent())) {
      certificateBytes = config.getCertificateContent();
    } else {
      certificateBytes = getBytesFromFile(config.getCertificatePath());
    }

    return getApiClientBuilder(basePath)
        .withKeyStore(certificateBytes, config.getCertificatePassword())
        .build();
  }

  private ApiClientBuilder getApiClientBuilder(String basePath) {
    ApiClientBuilder apiClientBuilder = this.apiClientBuilderProvider
        .newInstance()
        .withBasePath(basePath);

    BdkSslConfig sslConfig = this.config.getSsl();

    if(isNotEmpty(sslConfig.getTrustStorePath())) {
      byte[] trustStoreBytes = getBytesFromFile(sslConfig.getTrustStorePath());
      apiClientBuilder.withTrustStore(trustStoreBytes, sslConfig.getTrustStorePassword());
    }

    return apiClientBuilder;
  }

  private byte[] getBytesFromFile(String filePath) {
    try {
      return Files.readAllBytes(new File(filePath).toPath());
    } catch (IOException e) {
      throw new ApiClientInitializationException("Could not read file " + filePath, e);
    }
  }
}
