package com.symphony.bdk.core.client;

import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.client.loadbalancing.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.core.client.loadbalancing.RegularLoadBalancedApiClient;
import com.symphony.bdk.core.config.model.BdkAgentConfig;
import com.symphony.bdk.core.config.model.BdkAuthenticationConfig;
import com.symphony.bdk.core.config.model.BdkCertificateConfig;
import com.symphony.bdk.core.config.model.BdkClientConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkProxyConfig;
import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiClientBuilder;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

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

  private final static String LOGIN_CONTEXT_PATH = "/login";
  private final static String POD_CONTEXT_PATH = "/pod";
  private final static String AGENT_CONTEXT_PATH = "/agent";
  private final static String KEYMANAGER_CONTEXT_PATH = "/relay";
  private final static String SESSIONAUTH_CONTEXT_PATH = "/sessionauth";
  private final static String KEYAUTH_CONTEXT_PATH = "/keyauth";

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
   * Returns a fully initialized {@link ApiClient} for Login API.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getLoginClient() {
    return buildClient(LOGIN_CONTEXT_PATH, this.config.getPod());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Pod API.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getPodClient() {
    return buildClient(POD_CONTEXT_PATH, this.config.getPod());
  }

  // FIXME c pas bo
  @Deprecated
  public ApiClient getBaseClient(String contextPath) {
    return buildClient(contextPath, this.config.getPod());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for KeyManager API.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getRelayClient() {
    return buildClient(KEYMANAGER_CONTEXT_PATH, this.config.getKeyManager());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Agent API.
   * This may be a {@link RegularLoadBalancedApiClient} or a non load-balanced ApiClient based on the configuration.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getAgentClient() {
    if (config.getAgent().getLoadBalancing() != null) {
      return new RegularLoadBalancedApiClient(this.config, this);
    }
    return getRegularAgentClient();
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Agent API to be used by the datafeed services.
   * This may be a {@link DatafeedLoadBalancedApiClient} or a non load-balanced ApiClient based on the configuration.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getDatafeedAgentClient() {
    if (config.getAgent().getLoadBalancing() != null) {
      return new DatafeedLoadBalancedApiClient(this.config, this);
    }
    return getRegularAgentClient();
  }

  /**
   * Returns a fully initialized non-load-balanced {@link ApiClient} for Agent API.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getRegularAgentClient() {
    return getRegularAgentClient(this.config.getAgent().getBasePath());
  }

  /**
   * Returns a fully initialized non-load-balanced {@link ApiClient} for Agent API given an agent base path.
   *
   * @param agentBasePath the agent base URL to target.
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getRegularAgentClient(String agentBasePath) {
    return buildAgentClient(agentBasePath + AGENT_CONTEXT_PATH, this.config.getAgent());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for the SessionAuth API. This only works with a
   * certification configured.
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getSessionAuthClient() {
    return buildClientWithCertificate(this.config.getSessionAuth(), SESSIONAUTH_CONTEXT_PATH, this.config.getBot());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for the SessionAuth API using in Extension App Authentication.
   * This only works with a extension app authentication configured
   *
   * @return a new {@link ApiClient} instance.
   */
  public ApiClient getExtAppSessionAuthClient() {
    return buildClientWithCertificate(this.config.getSessionAuth(), SESSIONAUTH_CONTEXT_PATH, this.config.getApp());
  }

  /**
   * Returns a fully initialized {@link ApiClient} for the KeyAuth API. This only works with a
   * certification configured.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getKeyAuthClient() {
    return buildClientWithCertificate(this.config.getKeyManager(), KEYAUTH_CONTEXT_PATH, this.config.getBot());
  }

  protected ApiClient buildClient(String contextPath, BdkClientConfig clientConfig) {
    return getApiClientBuilder(clientConfig.getBasePath() + contextPath, clientConfig).build();
  }

  protected ApiClient buildAgentClient(String basePath, BdkAgentConfig agentConfig) {
    return getApiClientBuilder(basePath, agentConfig).build();
  }

  protected ApiClient buildClientWithCertificate(BdkClientConfig clientConfig, String contextPath, BdkAuthenticationConfig config) {
    if (!config.isCertificateAuthenticationConfigured()) {
      throw new ApiClientInitializationException("For certificate authentication, " +
          "certificatePath and certificatePassword must be set");
    }

    final BdkCertificateConfig certificateConfig = config.getCertificateConfig();
    ApiClient apiClient = null;
    try {
      apiClient = getApiClientBuilder(clientConfig.getBasePath() + contextPath, clientConfig)
          .withKeyStore(certificateConfig.getCertificateBytes(), certificateConfig.getPassword())
          .build();
    }
    catch (IllegalStateException e){
      String failedCertificateMessage = String.format("Failed while trying to parse the certificate at following path: %s."
          + " Check configuration is done properly and that certificate is in the correct format.", certificateConfig.getPath());
      log.error(failedCertificateMessage);
      throw new IllegalStateException(failedCertificateMessage, e);
    }
    return apiClient;
  }

  protected ApiClientBuilder getApiClientBuilder(String basePath, BdkClientConfig clientConfig) {
    ApiClientBuilder apiClientBuilder = this.apiClientBuilderProvider
        .newInstance()
        .withBasePath(basePath)
        .withReadTimeout(clientConfig.getReadTimeout())
        .withConnectionTimeout(clientConfig.getConnectionTimeout())
        .withConnectionPoolMax(clientConfig.getConnectionPoolMax())
        .withConnectionPoolPerRoute(clientConfig.getConnectionPoolPerRoute());

    if (clientConfig.getDefaultHeaders() != null) {
      clientConfig.getDefaultHeaders().forEach(apiClientBuilder::withDefaultHeader);
    }

    configureTruststore(apiClientBuilder);
    configureProxy(clientConfig.getProxy(), apiClientBuilder);

    return apiClientBuilder;
  }

  protected void configureTruststore(ApiClientBuilder apiClientBuilder) {
    final BdkCertificateConfig trustStoreConfig = this.config.getSsl().getCertificateConfig();

    if (trustStoreConfig.isConfigured()) {
      apiClientBuilder.withTrustStore(trustStoreConfig.getCertificateBytes(), trustStoreConfig.getPassword());
    }
  }

  protected void configureProxy(BdkProxyConfig proxyConfig, ApiClientBuilder apiClientBuilder) {
    if (proxyConfig != null) {
      apiClientBuilder
          .withProxy(proxyConfig.getHost(), proxyConfig.getPort())
          .withProxyCredentials(proxyConfig.getUsername(), proxyConfig.getPassword());
    }
  }

  @API(status = API.Status.INTERNAL)
  public enum ServiceEnum {
    AGENT,
    KEY_MANAGER,
    POD,
    SESSION_AUTH
  }

  public static ServiceEnum getServiceNameFromBasePath(String basePath) {
    if (basePath.contains(KEYMANAGER_CONTEXT_PATH) || basePath.contains(KEYAUTH_CONTEXT_PATH)) {
      return ServiceEnum.KEY_MANAGER;
    }
    if (basePath.contains(SESSIONAUTH_CONTEXT_PATH)) {
      return ServiceEnum.SESSION_AUTH;
    }
    if (basePath.contains(AGENT_CONTEXT_PATH)) {
      return ServiceEnum.AGENT;
    } else { return ServiceEnum.POD; }
  }

}
