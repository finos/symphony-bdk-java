package com.symphony.bdk.core.client.lb;

import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

/**
 * The {@link LoadBalancingStrategy} corresponding to the
 * {@link com.symphony.bdk.core.config.model.BdkLoadBalancingMode#EXTERNAL} mode.
 */
@API(status = API.Status.INTERNAL)
public class ExternalLoadBalancingStrategy implements LoadBalancingStrategy {

  private SignalsApi signalsApi;

  public ExternalLoadBalancingStrategy(SignalsApi signalsApi) {
    this.signalsApi = signalsApi;
  }

  /**
   * Gets a new base path by calling <a href="https://developers.symphony.com/restapi/reference#agent-info-v1">GET agent/v1/info</a>
   * and fetching serverFqdn field.
   *
   * @return the newly retrieved base path.
   */
  @Override
  public String getNewBasePath() {
    try {
      return signalsApi.v1InfoGet().getServerFqdn();
    } catch (ApiException e) {
      // TODO Which retry strategy in case of error ?
      throw new ApiRuntimeException(e);
    }
  }
}
