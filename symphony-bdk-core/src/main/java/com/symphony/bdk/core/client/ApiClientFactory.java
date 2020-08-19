package com.symphony.bdk.core.client;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientProvider;
import com.symphony.bdk.core.config.model.BdkConfig;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Factory responsible for creating {@link ApiClient} instances for each main Symphony's components :
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
  private final ApiClientProvider apiClientProvider;

  public ApiClientFactory(@Nonnull BdkConfig config) {
    this.config = config;
    this.apiClientProvider = findApiClientProvider();
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Login API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getLoginClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getPod().getBasePath() + "/login");
    return apiClient;
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Pod API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getPodClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getPod().getBasePath() + "/pod");
    return apiClient;
  }

  /**
   * Returns a fully initialized {@link ApiClient} for KeyManager API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getRelayClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getKeyManager().getBasePath() + "/relay");
    return apiClient;
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Agent API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getAgentClient() {
    final ApiClient apiClient = this.apiClientProvider.newInstance();
    apiClient.setBasePath(this.config.getAgent().getBasePath() + "/agent");
    return apiClient;
  }

  /**
   * Load {@link ApiClient} implementation class using {@link ServiceLoader}.
   *
   * @return an {@link ApiClientProvider}.
   */
  @Generated // exclude from code coverage as it is very difficult to mock the ServiceLoader class (which is final)
  private static ApiClientProvider findApiClientProvider() {

    final ServiceLoader<ApiClientProvider> apiClientServiceLoader = ServiceLoader.load(ApiClientProvider.class);

    final List<ApiClientProvider> apiClientProviders = StreamSupport.stream(apiClientServiceLoader.spliterator(), false)
        .collect(Collectors.toList());

    if (apiClientProviders.isEmpty()) {
      throw new IllegalStateException("No ApiClientProvider implementation found in classpath.");
    } else if (apiClientProviders.size() > 1) {
      log.warn("More than 1 ApiClientProvider implementation found in classpath, will use : {}",
          apiClientProviders.stream().findFirst().get());
    }

    return apiClientProviders.stream().findFirst().get();
  }
}
