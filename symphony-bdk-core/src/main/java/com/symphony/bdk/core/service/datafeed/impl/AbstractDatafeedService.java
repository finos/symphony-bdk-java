package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import javax.ws.rs.ProcessingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for implementing the datafeed services. A datafeed services can help a bot subscribe or unsubscribe
 * a {@link RealTimeEventListener} and handle the received event by the subscribed listeners.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
abstract class AbstractDatafeedService implements DatafeedService {

  protected final AuthSession authSession;
  protected final BdkConfig bdkConfig;
  protected final List<RealTimeEventListener> listeners;
  protected final RetryWithRecoveryBuilder retryWithRecoveryBuilder;
  protected DatafeedApi datafeedApi;
  protected ApiClient apiClient;

  public AbstractDatafeedService(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    this.datafeedApi = datafeedApi;
    this.listeners = new ArrayList<>();
    this.authSession = authSession;
    this.bdkConfig = config;
    this.apiClient = datafeedApi.getApiClient();
    this.retryWithRecoveryBuilder = new RetryWithRecoveryBuilder<>()
        .retryConfig(config.getDatafeedRetryConfig())
        .recoveryStrategy(Exception.class, e -> true, () -> this.apiClient.rotate())  //always rotate in case of any error
        .recoveryStrategy(ApiException::isUnauthorized, this::refresh);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void subscribe(RealTimeEventListener listener) {
    listeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unsubscribe(RealTimeEventListener listener) {
    listeners.remove(listener);
  }

  /**
   * Handle a received listener by using the subscribed {@link RealTimeEventListener}.
   *
   * @param events List of Datafeed events to be handled
   */
  protected void handleV4EventList(List<V4Event> events) {
    for (V4Event event : events) {
      if (event == null || event.getType() == null) {
        continue;
      }
      if (this.isSelfGeneratedEvent(event)) {
        continue;
      }
      try {
        RealTimeEventType eventType = RealTimeEventType.valueOf(event.getType());
        for (RealTimeEventListener listener : listeners) {
          eventType.dispatch(listener, event);
        }
      } catch (IllegalArgumentException e) {
        log.warn("Receive events with unknown type: {}", event.getType());
      }
    }
  }

  private boolean isSelfGeneratedEvent(V4Event event) {
    return event.getInitiator() != null && event.getInitiator().getUser() != null
        && event.getInitiator().getUser().getUsername() != null
        && event.getInitiator().getUser().getUsername().equals(this.bdkConfig.getBot().getUsername());
  }

  protected void refresh() throws AuthUnauthorizedException {
    log.info("Re-authenticate and try again");
    authSession.refresh();
  }

  protected void setDatafeedApi(DatafeedApi datafeedApi) {
    this.datafeedApi = datafeedApi;
  }
}
