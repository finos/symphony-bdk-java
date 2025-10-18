package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.retry.RetryWithRecovery.networkIssueMessageError;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

/**
 * Base class for implementing the datafeed services. A datafeed services can help a bot subscribe or unsubscribe
 * a {@link RealTimeEventListener} and handle the received event by the subscribed listeners.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
abstract class AbstractDatafeedLoop implements DatafeedLoop {

  protected final AuthSession authSession;
  protected final BdkConfig bdkConfig;
  protected final UserV2 botInfo;
  protected final AtomicBoolean started = new AtomicBoolean();
  protected DatafeedApi datafeedApi;
  private long lastPullTimestamp;

  // access needs to be thread safe (DF loop is usually running on its own thread)
  private final List<RealTimeEventListener> listeners;

  public AbstractDatafeedLoop(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, UserV2 botInfo) {
    this.datafeedApi = datafeedApi;
    this.listeners = new ArrayList<>();
    this.authSession = authSession;
    this.bdkConfig = config;
    this.botInfo = botInfo;
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
   * {@inheritDoc}
   */
  @Override
  public void start() throws AuthUnauthorizedException, ApiException {
    if (!this.started.compareAndSet(false, true)) {
      throw new IllegalStateException("The datafeed service is already started");
    }

    if (!DistributedTracingContext.hasTraceId()) {
      DistributedTracingContext.setTraceId();
    }

    try {
      updateLastPullTimestamp();
      runLoop();
    } catch (AuthUnauthorizedException | ApiException | NestedRetryException exception) {
      throw exception;
    } catch (Throwable throwable) {
      log.error("{}\n{}", networkIssueMessageError(throwable, datafeedApi.getApiClient().getBasePath()), throwable);
    } finally {
      DistributedTracingContext.clear();
    }
  }

  protected abstract void runLoop() throws Throwable;

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    if (this.started.compareAndSet(true, false)) {
      log.info("Stopping the datafeed loop (will happen once the current read is finished)...");
    } else {
      log.warn("Datafeed loop already stopping...");
    }
  }

  private void updateLastPullTimestamp() {
    this.lastPullTimestamp = Instant.now().toEpochMilli();
  }

  /**
   * Handle a received listener by using the subscribed {@link RealTimeEventListener}.
   *
   * @param events List of Datafeed events to be handled
   * @throws RequeueEventException Raised if a listener fails and the developer wants to explicitly not update the ack id.
   */
  protected void handleV4EventList(@Nullable List<V4Event> events) throws RequeueEventException {
    updateLastPullTimestamp();
    if (events == null || events.isEmpty()) {
      return;
    }

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

            if (listener.isAcceptingEvent(event, this.botInfo)) {
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

  /**
   * {@inheritDoc}
   */
  @Override
  public long lastPullTimestamp() {
    return this.lastPullTimestamp;
  }
}
