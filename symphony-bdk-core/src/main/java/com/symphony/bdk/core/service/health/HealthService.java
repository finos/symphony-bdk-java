package com.symphony.bdk.core.service.health;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.gen.api.model.V2HealthCheckResponse;
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
  public V3Health v3HealthCheck() {
    try {
      return systemApi.v3Health();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

  /**
   * Returns the connectivity status of the Agent services as well as users connectivity.
   * Available on Agent 2.57.0 and above.
   *
   * @return {@link V3Health} the connectivity status of the Agent services as well as users connectivity.
   * @see <a href="https://developers.symphony.com/restapi/reference#health-check-extended-v3">Healt Check Extended v3</a>
   */
  public V3Health v3ExtendedHealthCheck() {
    try {
      return systemApi.v3ExtendedHealth();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

  /**
   * Returns the connectivity status of your pod and key manager, as well as the Agent version.
   * Available on Agent 2.0.0 and above.
   *
   * @return {@link V2HealthCheckResponse} the connectivity status of your pod and key manager, as well as the Agent version.
   * @see <a href="https://developers.symphony.com/restapi/reference#health-check-v2">Health Check v2</a>
   */
  public V2HealthCheckResponse v2HeathCheck() {
    try {
      return systemApi.v2HealthCheckGet(true, true,
          true, true,
          true, true,
          true, true,
          authSession.getSessionToken(), authSession.getKeyManagerToken());
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

  /**
   * Gets information about the Agent.
   * Available on Agent 2.53.0 and above.
   *
   * @return {@link AgentInfo} information of  the agent server.
   * @see <a href="https://developers.symphony.com/restapi/reference#agent-info-v1">Agent Info v1</a>
   */
  public AgentInfo getAgentInfo() {
    try {
      return signalsApi.v1InfoGet();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    }
  }

}
