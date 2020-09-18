package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.util.function.ConsumerWithThrowable;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.ws.rs.ProcessingException;

/**
 * Base class for implementing the datafeed services. A datafeed services can help a bot subscribe or unsubscribe
 * a {@link RealTimeEventListener} and handle the received event by the subscribed listeners.
 */
@Slf4j
abstract class AbstractDatafeedService implements DatafeedService {

  protected final AuthSession authSession;
  protected final BdkConfig bdkConfig;
  protected final List<RealTimeEventListener> listeners;
  protected DatafeedApi datafeedApi;

  public AbstractDatafeedService(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    this.datafeedApi = datafeedApi;
    this.listeners = new ArrayList<>();
    this.authSession = authSession;
    this.bdkConfig = config;
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
    return event.getInitiator().getUser().getUsername().equals(this.bdkConfig.getBot().getUsername());
  }

  protected Map<Predicate<ApiException>, ConsumerWithThrowable> getSessionRefreshStrategy() {
    return Collections.singletonMap(ApiException::isUnauthorized, this::refresh);
  }

  protected boolean isNetworkOrServerOrUnauthorizedOrClientError(Throwable t) {
    if (t instanceof ApiException) {
      ApiException apiException = (ApiException) t;
      return apiException.isTemporaryServerError() || apiException.isUnauthorized() || apiException.isClientError();
    }
    return t instanceof ProcessingException;
  }

  protected boolean isNetworkOrServerOrUnauthorizedError(Throwable t) {
    if (t instanceof ApiException) {
      ApiException apiException = (ApiException) t;
      return apiException.isTemporaryServerError() || apiException.isUnauthorized();
    }
    return t instanceof ProcessingException;
  }

  protected void refresh() throws AuthUnauthorizedException {
    log.info("Re-authenticate and try again");
    authSession.refresh();
  }

  protected void setDatafeedApi(DatafeedApi datafeedApi) {
    this.datafeedApi = datafeedApi;
  }

}
