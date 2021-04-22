package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base class for implementing the datafeed services. A datafeed services can help a bot subscribe or unsubscribe
 * a {@link RealTimeEventListener} and handle the received event by the subscribed listeners.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
abstract class AbstractDatafeedLoop implements DatafeedLoop {

  protected final AuthSession authSession;
  protected final BdkConfig bdkConfig;
  protected final RetryWithRecoveryBuilder retryWithRecoveryBuilder;
  @Setter
  protected DatafeedApi datafeedApi;
  protected ApiClient apiClient;

  // access needs to be thread safe (DF loop is usually running on its own thread)
  private final List<RealTimeEventListener> listeners;

  public AbstractDatafeedLoop(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    this.datafeedApi = datafeedApi;
    this.listeners = new ArrayList<>();
    this.authSession = authSession;
    this.bdkConfig = config;
    this.apiClient = datafeedApi.getApiClient();
    this.retryWithRecoveryBuilder = new RetryWithRecoveryBuilder<>()
        .retryConfig(config.getDatafeedRetryConfig())
        .recoveryStrategy(Exception.class, () -> this.apiClient.rotate())  //always rotate in case of any error
        .recoveryStrategy(ApiException::isUnauthorized, this::refresh);

    final BdkLoadBalancingConfig loadBalancing = config.getAgent().getLoadBalancing();
    if (loadBalancing != null && !loadBalancing.isStickiness()) {
      log.warn("DF used with agent load balancing configured with stickiness false. DF calls will still be sticky.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void subscribe(RealTimeEventListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unsubscribe(RealTimeEventListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  /**
   * Handle a received listener by using the subscribed {@link RealTimeEventListener}.
   *
   * @param events List of Datafeed events to be handled
   * @throws RequeueEventException Raised if a listener fails and the developer wants to explicitly not update the ack id.
   */
  protected void handleV4EventList(List<V4Event> events) throws RequeueEventException {
    for (V4Event event : events) {

      final Optional<RealTimeEventType> eventType = RealTimeEventType.fromV4Event(event);

      if (!eventType.isPresent()) {
        log.info("Unsupported event received: {}", event);
        continue;
      }

      // dispatch single event using event's ID as traceId. Tested for DatafeedLoopV2 as well, and working.
      DistributedTracingContext.doWithTraceId(event.getId(), () -> {

        synchronized (this.listeners) {
          for (RealTimeEventListener listener : this.listeners) {

            if (listener.isAcceptingEvent(event, this.bdkConfig.getBot().getUsername())) {
              try {
                log.debug("Before dispatching '{}' event to listener {}", event.getType(), listener);
                eventType.get().dispatch(listener, event);
                log.debug("'{}' event successfully dispatched to listener {}", event.getType(), listener);
              } catch (EventException e) {
                // rethrow this explicit exception to not update the ack id in the DFv2 loop
                throw new RequeueEventException(event, listener, e);
              } catch (Exception t) {
                log.debug("An uncaught exception has occurred while dispatching event {} to listener {}",
                    event.getType(), listener, t);
              }
            }
          }
        }
      });
    }
  }

  protected void refresh() throws AuthUnauthorizedException {
    log.info("Re-authenticate and try again");
    this.authSession.refresh();
  }
}
