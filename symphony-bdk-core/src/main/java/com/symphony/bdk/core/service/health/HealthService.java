package com.symphony.bdk.core.service.health;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.apiguardian.api.API;

/**
 * Service class for checking health of the Agent server.
 */
@API(status = API.Status.STABLE)
public class HealthService {

  private final SystemApi systemApi;
  private final SignalsApi signalsApi;
  private final AuthSession authSession;

  public HealthService(SystemApi systemApi, SignalsApi signalsApi, AuthSession authSession) {
    this.systemApi = systemApi;
    this.signalsApi = signalsApi;
    this.authSession = authSession;
  }

  /**
   * Returns the connectivity status of your Agent server.
   * If your Agent server is started and running properly, the status value will be UP.
   * Available on Agent 2.57.0 and above.
   *
   * @return {@link V3Health} the connectivity status of your Agent server.
   * @see <a href="https://developers.symphony.com/restapi/reference#health-check-v3">Health Check v3</a>
   */
  public V3Health healthCheck() {
    return execute(systemApi::v3Health);
  }

  /**
   * Returns the connectivity status of the Agent services as well as users connectivity.
   * Available on Agent 2.57.0 and above.
   *
   * @return {@link V3Health} the connectivity status of the Agent services as well as users connectivity.
   * @see <a href="https://developers.symphony.com/restapi/reference#health-check-extended-v3">Healt Check Extended v3</a>
   */
  public V3Health healthCheckExtended() {
    return execute(systemApi::v3ExtendedHealth);
  }

  /**
   * Gets information about the Agent.
   * Available on Agent 2.53.0 and above.
   *
   * @return {@link AgentInfo} information of  the agent server.
   * @see <a href="https://developers.symphony.com/restapi/reference#agent-info-v1">Agent Info v1</a>
   */
  public AgentInfo getAgentInfo() {
    return execute(signalsApi::v1InfoGet);
  }

  private <T> T execute(SupplierWithApiException<T> supplier) {
    try {
      return supplier.get();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

}
