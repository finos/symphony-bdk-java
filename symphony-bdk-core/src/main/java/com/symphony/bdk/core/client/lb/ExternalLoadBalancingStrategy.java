package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class ExternalLoadBalancingStrategy implements LoadBalancingStrategy {

  SignalsApi signalsApi;

  public ExternalLoadBalancingStrategy(SignalsApi signalsApi) {
    this.signalsApi = signalsApi;
  }

  @Override
  public String getNewBasePath() {
    try {
      return signalsApi.v1InfoGet().getServerFqdn();
    } catch (ApiException e) {
      // Which retry strategy in case of error ?
      throw new ApiRuntimeException(e);
    }
  }
}
