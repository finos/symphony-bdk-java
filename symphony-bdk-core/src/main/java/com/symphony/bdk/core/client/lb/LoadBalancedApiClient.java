package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.RegularApiClient;

import org.apiguardian.api.API;

import java.util.List;

@API(status = API.Status.INTERNAL)
public abstract class LoadBalancedApiClient implements ApiClient {

  protected BdkLoadBalancingConfig loadBalancingConfig;
  protected ApiClientFactory apiClientFactory;
  protected RegularApiClient apiClient;
  private LoadBalancingStrategy loadBalancingStrategy;

  public LoadBalancedApiClient(BdkLoadBalancingConfig loadBalancingConfig, ApiClientFactory apiClientFactory) {
    this.loadBalancingConfig = loadBalancingConfig;
    this.apiClientFactory = apiClientFactory;
    this.loadBalancingStrategy = LoadBalancingStrategy.getInstance(loadBalancingConfig, apiClientFactory);

    rotate();
  }

  @Override
  public void rotate() {
    apiClient = apiClientFactory.getRegularAgentClient(loadBalancingStrategy.getNewBasePath());
  }

  @Override
  public String getBasePath() {
    return apiClient.getBasePath();
  }

  @Override
  public String parameterToString(Object param) {
    return apiClient.parameterToString(param);
  }

  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    return apiClient.parameterToPairs(collectionFormat, name, value);
  }

  @Override
  public String selectHeaderAccept(String... accepts) {
    return apiClient.selectHeaderAccept(accepts);
  }

  @Override
  public String selectHeaderContentType(String... contentTypes) {
    return apiClient.selectHeaderContentType(contentTypes);
  }

  @Override
  public String escapeString(String str) {
    return apiClient.escapeString(str);
  }
}
