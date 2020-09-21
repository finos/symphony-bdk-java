package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.util.BdkExponentialFunction;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
    protected final RetryConfig retryConfig;
    protected DatafeedApi datafeedApi;

    public AbstractDatafeedService(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
        this.datafeedApi = datafeedApi;
        this.listeners = new ArrayList<>();
        this.authSession = authSession;
        this.bdkConfig = config;
        BdkRetryConfig bdkRetryConfig = this.bdkConfig.getDatafeed().getRetry() == null ? this.bdkConfig.getRetry() : this.bdkConfig.getDatafeed().getRetry();
        this.retryConfig = RetryConfig.custom()
                .maxAttempts(bdkRetryConfig.getMaxAttempts())
                .intervalFunction(BdkExponentialFunction.ofExponentialBackoff(bdkRetryConfig))
                .retryOnException(e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        return apiException.isServerError() || apiException.isUnauthorized();
                    }
                    return e instanceof ProcessingException;
                })
                .build();
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
     *
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
                    try {
                      eventType.dispatch(listener, event);
                    } catch (Exception ex) {
                      log.debug("An error has occurred while dispatching event {}", event, ex);
                    }
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

    protected Retry getRetryInstance(String name, RetryConfig... config) {
        Retry retry = config.length == 0 ? Retry.of(name, this.retryConfig) : Retry.of(name, config[0]);
        retry.getEventPublisher().onRetry(event -> {
            long intervalInMillis = event.getWaitInterval().toMillis();
            double interval = intervalInMillis / 1000.0;
            if (event.getLastThrowable() != null) {
              log.debug("Datafeed service failed due to {}", event.getLastThrowable().getMessage());
            }
            log.info("Retry in {} secs...", interval);
        });
        return retry;
    }

    protected void setDatafeedApi(DatafeedApi datafeedApi) {
        this.datafeedApi = datafeedApi;
    }

}
