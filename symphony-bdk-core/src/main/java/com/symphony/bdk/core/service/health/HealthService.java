package com.symphony.bdk.core.service.health;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.gen.api.model.V3Health;
import com.symphony.bdk.gen.api.model.V3HealthStatus;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.time.Instant;
import java.util.function.Supplier;

/**
 * Service class for checking health of the Agent server.
 */
@Slf4j
@API(status = API.Status.STABLE)
public class HealthService {

  private final SystemApi systemApi;
  private final SignalsApi signalsApi;
  private final AuthSession authSession;
  private DatafeedLoop datafeedLoop;

  public HealthService(SystemApi systemApi, SignalsApi signalsApi, AuthSession authSession) {
    this.systemApi = systemApi;
    this.signalsApi = signalsApi;
    this.authSession = authSession;
  }

  public HealthService(SystemApi systemApi, SignalsApi signalsApi, AuthSession authSession, DatafeedLoop datafeedLoop) {
    this.systemApi = systemApi;
    this.signalsApi = signalsApi;
    this.authSession = authSession;
    this.datafeedLoop = datafeedLoop;
  }

  public void setDatafeedLoop(DatafeedLoop datafeedLoop) {
    this.datafeedLoop = datafeedLoop;
  }

  /**
   * Returns the connectivity status of your Agent server.
   * If your Agent server is started and running properly, the status value will be UP.
   * Available on Agent 2.57.0 and above.
   *
   * @return {@link V3Health} the connectivity status of your Agent server.
   * @see <a href="https://developers.symphony.com/restapi/reference/health-check-v3">Health Check v3</a>
   */
  public V3Health healthCheck() {
    return execute(systemApi::v3Health);
  }

  /**
   * Returns the connectivity status of the Agent services as well as users connectivity.
   * Available on Agent 2.57.0 and above.
   *
   * @return {@link V3Health} the connectivity status of the Agent services as well as users connectivity.
   * @see <a href="https://developers.symphony.com/restapi/reference/health-check-extended-v3">Healt Check Extended v3</a>
   */
  public V3Health healthCheckExtended() {
    return execute(systemApi::v3ExtendedHealth);
  }

  /**
   * Return the connectivity status of the DataFeed long pulling connection.
   *
   * @return {@link V3HealthStatus} the connectivity status
   */
  public V3HealthStatus datafeedHealthCheck() {
    if (this.datafeedLoop != null) {
      return lastRunHealthStatus(this.datafeedLoop::lastPullTimestamp);
    }
    log.trace("datafeedloop is not enabled");
    return V3HealthStatus.DOWN;
  }

  /**
   * Gets information about the Agent.
   * Available on Agent 2.53.0 and above.
   *
   * @return {@link AgentInfo} information of  the agent server.
   * @see <a href="https://developers.symphony.com/restapi/reference/agent-info-v1">Agent Info v1</a>
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

  private V3HealthStatus lastRunHealthStatus(Supplier<Long> supplier) {
    long lastRun = supplier.get();
    // AWS SQS has long pulling timeout == 20 secs
    boolean after = Instant.now().isAfter(Instant.ofEpochMilli(lastRun).plusSeconds(22));
    return after ? V3HealthStatus.DOWN : V3HealthStatus.UP;
  }
}
