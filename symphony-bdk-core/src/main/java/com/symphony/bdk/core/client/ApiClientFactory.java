package com.symphony.bdk.core.client;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientProvider;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.apiguardian.api.API;

/**
 * Factory responsible for creating {@link ApiClient} instances for each main Symphony's components :
 * <ul>
 *   <li>Agent</li>
 *   <li>KeyManager</li>
 *   <li>Pod</li>
 * </ul>
 */
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientFactory {

  private final BdkConfig config;
  private final ApiClientProvider apiClientProvider;

  /**
   *
   * @param config
   * @param apiClientProvider
   */
  public ApiClientFactory(final BdkConfig config, final ApiClientProvider apiClientProvider) {
    this.config = config;
    this.apiClientProvider = apiClientProvider;
  }

  /**
   *
   * @return
   */
  public ApiClient getLoginClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getPod().getBasePath() + "/login");
    return apiClient;
  }

  /**
   *
   * @return
   */
  public ApiClient getRelayClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getKeyManager().getBasePath() + "/relay");
    return apiClient;
  }

  /**
   *
   * @return
   */
  public ApiClient getAgentClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getAgent().getBasePath() + "/agent");
    return apiClient;
  }
}
