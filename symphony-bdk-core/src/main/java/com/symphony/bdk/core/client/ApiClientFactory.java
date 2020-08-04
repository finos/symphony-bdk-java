package com.symphony.bdk.core.client;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.config.BdkConfig;

import lombok.SneakyThrows;

/**
 * Factory responsible for creating {@link ApiClient} instances for each main Symphony's components :
 * <ul>
 *   <li>Agent</li>
 *   <li>KeyManager</li>
 *   <li>Pod</li>
 * </ul>
 */
public class ApiClientFactory {

  private final BdkConfig config;
  private final Class<? extends ApiClient> apiClientClz;

  /**
   *
   * @param config
   * @param apiClientClz
   */
  public ApiClientFactory(final BdkConfig config, final Class<? extends ApiClient> apiClientClz) {
    this.config = config;
    this.apiClientClz = apiClientClz;
  }

  /**
   *
   * @return
   */
  public ApiClient getLoginClient() {
    final ApiClient apiClient = this.newInstance();
    apiClient.setBasePath(this.config.getPodUrl() + "/login");
    return apiClient;
  }

  /**
   *
   * @return
   */
  public ApiClient getRelayClient() {
    final ApiClient apiClient = this.newInstance();
    apiClient.setBasePath(this.config.getPodUrl() + "/relay");
    return apiClient;
  }

  /**
   *
   * @return
   */
  public ApiClient getAgentClient() {
    final ApiClient apiClient = this.newInstance();
    apiClient.setBasePath(this.config.getAgentUrl() + "/agent");
    return apiClient;
  }

  @SneakyThrows
  private ApiClient newInstance() {
    return this.apiClientClz.newInstance();
  }
}
