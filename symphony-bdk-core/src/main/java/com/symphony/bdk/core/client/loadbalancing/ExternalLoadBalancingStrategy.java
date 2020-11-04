package com.symphony.bdk.core.client.loadbalancing;

import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

import static com.symphony.bdk.core.retry.RetryWithRecovery.executeAndRetry;

/**
 * The {@link LoadBalancingStrategy} corresponding to the
 * {@link com.symphony.bdk.core.config.model.BdkLoadBalancingMode#EXTERNAL} mode.
 */
@API(status = API.Status.INTERNAL)
public class ExternalLoadBalancingStrategy implements LoadBalancingStrategy {

  private RetryWithRecoveryBuilder<String> retryBuilder;
  private SignalsApi signalsApi;

  public ExternalLoadBalancingStrategy(BdkRetryConfig retryConfig, SignalsApi signalsApi) {
    this.signalsApi = signalsApi;
    this.retryBuilder = new RetryWithRecoveryBuilder<String>()
        .retryConfig(retryConfig)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkOrMinorError);
  }

  /**
   * Gets a new base path by calling <a href="https://developers.symphony.com/restapi/reference#agent-info-v1">GET agent/v1/info</a>
   * and fetching serverFqdn field.
   *
   * @return the newly retrieved base path.
   */
  @Override
  public String getNewBasePath() {
    return executeAndRetry(retryBuilder, "agent-info", () -> signalsApi.v1InfoGet().getServerFqdn());
  }
}
