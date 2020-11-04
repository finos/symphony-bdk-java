package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.RegularApiClient;

import org.apiguardian.api.API;

import java.util.List;

/**
 * An {@link ApiClient} implementation which load balances calls across several base URLs.
 * It contains a {@link RegularApiClient} (i.e. non load-balanced api client) in order to target a specific base URL.
 */
@API(status = API.Status.INTERNAL)
public abstract class LoadBalancedApiClient implements ApiClient {

  protected BdkLoadBalancingConfig loadBalancingConfig;
  protected ApiClientFactory apiClientFactory;
  protected RegularApiClient apiClient;
  private LoadBalancingStrategy loadBalancingStrategy;

  /**
   *
   * @param loadBalancingConfig the load balancing configuration to be used
   * @param apiClientFactory the api client factory used to instantiate {@link RegularApiClient} instances.
   */
  public LoadBalancedApiClient(BdkLoadBalancingConfig loadBalancingConfig, ApiClientFactory apiClientFactory) {
    this.loadBalancingConfig = loadBalancingConfig;
    this.apiClientFactory = apiClientFactory;
    this.loadBalancingStrategy = LoadBalancingStrategy.getInstance(loadBalancingConfig, apiClientFactory);

    rotate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getBasePath() {
    return apiClient.getBasePath();
  }

  /**
   * This makes the api client target the provided basePath.
   * It creates a new underlying RegularApiClient targeting the provided basePath.
   *
   * @param basePath the base URL to target.
   */
  public void setBasePath(String basePath) {
    apiClient = apiClientFactory.getRegularAgentClient(basePath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void rotate() {
    setBasePath(loadBalancingStrategy.getNewBasePath());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String parameterToString(Object param) {
    return apiClient.parameterToString(param);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    return apiClient.parameterToPairs(collectionFormat, name, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderAccept(String... accepts) {
    return apiClient.selectHeaderAccept(accepts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String selectHeaderContentType(String... contentTypes) {
    return apiClient.selectHeaderContentType(contentTypes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String escapeString(String str) {
    return apiClient.escapeString(str);
  }
}
