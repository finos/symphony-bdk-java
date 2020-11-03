package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.util.TypeReference;

import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link LoadBalancedApiClient} where {@link #rotate()} is executed when
 * {@link #invokeAPI(String, String, List, Object, Map, Map, Map, String, String, String[], TypeReference)} is called
 * and if {@link #loadBalancingConfig} has the field stickiness set to false.
 */
@API(status = API.Status.INTERNAL)
public class RegularLoadBalancedApiClient extends LoadBalancedApiClient {

  public RegularLoadBalancedApiClient(BdkLoadBalancingConfig loadBalancingConfig, ApiClientFactory apiClientFactory) {
    super(loadBalancingConfig, apiClientFactory);
  }

  /**
   * {@inheritDoc}
   **/
  @Override
  public <T> ApiResponse<T> invokeAPI(String path, String method, List<Pair> queryParams, Object body,
      Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept,
      String contentType, String[] authNames, TypeReference<T> returnType) throws ApiException {

    if (!loadBalancingConfig.isStickiness()) {
      rotate();
    }

    return apiClient.invokeAPI(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
        contentType, authNames, returnType);
  }
}
