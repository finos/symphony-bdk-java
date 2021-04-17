package com.symphony.bdk.spring.service;

import com.symphony.bdk.spring.model.TenantRequestContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BdkApiClientRegister<T> {
  /**
   * Request scoped Bean, use Proxy
   */
  private final TenantRequestContext requestingTenant;
  private Map<String, ConcurrentMap<String, T>> register = new HashMap<>();

  public void set(String apiName, T bdkApiClient) {
    ConcurrentMap<String, T> apiMap = register.getOrDefault(requestingTenant.getTenantId(), new ConcurrentHashMap());
    apiMap.put(apiName, bdkApiClient);
    register.put(requestingTenant.getTenantId(), apiMap);
  }

  public T get(String apiName) {

    ConcurrentMap<String, T> apiMap = register.get(requestingTenant.getTenantId());
    if(apiMap != null) {
      return apiMap.get(apiName);
    }
    return null;
  }
}
