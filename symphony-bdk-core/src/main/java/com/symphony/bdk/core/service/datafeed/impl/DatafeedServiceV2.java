package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.core.util.function.ConsumerWithThrowable;
import com.symphony.bdk.core.util.function.RetryWithRecovery;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5EventList;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * A class for implementing the datafeed v2 service.
 * <p>
 * This service will be started by calling {@link DatafeedServiceV2#start()}
 * <p>
 * At the beginning, the BDK bot will try to retrieve the list of datafeed to which it is listening. Since each bot
 * should only listening to just one datafeed, the first datafeed in the list will be used by the bot to be listened to.
 * If the retrieved list is empty, the BDK bot will create a new datafeed to listen.
 * <p>
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 * <p>
 * If this datafeed becomes stale or faulty, the BDK bot will create the new one for listening.
 * <p>
 * This service will be stopped by calling {@link DatafeedServiceV2#stop()}
 * <p>
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
public class DatafeedServiceV2 extends AbstractDatafeedService {

  private final AtomicBoolean started = new AtomicBoolean();
  private final AckId ackId;
  private V5Datafeed datafeed;
  private final RetryWithRecovery<V5Datafeed> retrieveDatafeedRetry;
  private final RetryWithRecovery<Void> deleteDatafeedRetry;
  private final RetryWithRecovery<V5Datafeed> createDatafeedRetry;
  private final RetryWithRecovery<Void> readDatafeedRetry;


  public DatafeedServiceV2(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    super(datafeedApi, authSession, config);
    this.ackId = new AckId().ackId("");

    this.retrieveDatafeedRetry =
        new RetryWithRecovery<>("Retrieve Datafeed V2", this.bdkConfig.getDatafeedRetryConfig(),
            this::tryRetrieveDatafeed, this::isNetworkOrServerOrUnauthorizedError, getSessionRefreshStrategy());

    this.deleteDatafeedRetry =
        new RetryWithRecovery<>("Delete Datafeed V2", this.bdkConfig.getDatafeedRetryConfig(),
            this::tryDeleteDatafeed, this::isNetworkOrServerOrUnauthorizedError, ApiException::isClientError, getSessionRefreshStrategy());

    this.createDatafeedRetry =
        new RetryWithRecovery<>("Create Datafeed V2", this.bdkConfig.getDatafeedRetryConfig(),
            this::tryCreateDatafeed, this::isNetworkOrServerOrUnauthorizedError, getSessionRefreshStrategy());

    Map<Predicate<ApiException>, ConsumerWithThrowable> readRecoveryStrategy = new HashMap<>(getSessionRefreshStrategy());
    readRecoveryStrategy.put(ApiException::isClientError, this::recreateDatafeed);

    this.readDatafeedRetry = new RetryWithRecovery<>("Read Datafeed V2", this.bdkConfig.getDatafeedRetryConfig(),
        this::readAndHandleEvents, this::isNetworkOrServerOrUnauthorizedOrClientError, readRecoveryStrategy);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() throws ApiException, AuthUnauthorizedException {
    if (this.started.get()) {
      throw new IllegalStateException("The datafeed service is already started");
    }
    try {
      this.datafeed = this.retrieveDatafeed();
      if (this.datafeed == null) {
        this.datafeed = this.createDatafeed();
      }
      log.debug("Start reading datafeed events");
      this.started.set(true);
      do {
        this.readDatafeed();
      } while (this.started.get());
    } catch (AuthUnauthorizedException | ApiException exception) {
      throw exception;
    } catch (Throwable throwable) {
      log.error("Unknown error", throwable);
    }
  }

  protected AckId getAckId() {
    return this.ackId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    this.started.set(false);
  }

  private V5Datafeed createDatafeed() throws Throwable {
    log.debug("Start creating datafeed from agent");
    return createDatafeedRetry.execute();
  }

  private V5Datafeed tryCreateDatafeed() throws ApiException {
    return this.datafeedApi.createDatafeed(authSession.getSessionToken(), authSession.getKeyManagerToken());
  }

  private V5Datafeed retrieveDatafeed() throws Throwable {
    log.debug("Start retrieving datafeed from agent");
    return retrieveDatafeedRetry.execute();
  }

  private V5Datafeed tryRetrieveDatafeed() throws ApiException {
    final List<V5Datafeed> datafeeds =
        this.datafeedApi.listDatafeed(authSession.getSessionToken(), authSession.getKeyManagerToken());

    if (!datafeeds.isEmpty()) {
      return datafeeds.get(0);
    }
    return null;
  }

  private void readDatafeed() throws Throwable {
    log.debug("Reading datafeed events from datafeed {}", datafeed.getId());
    this.readDatafeedRetry.execute();
  }

  private Void readAndHandleEvents() throws ApiException {
    V5EventList v5EventList = this.datafeedApi.readDatafeed(
        datafeed.getId(),
        authSession.getSessionToken(),
        authSession.getKeyManagerToken(),
        ackId);
    this.ackId.setAckId(v5EventList.getAckId());
    List<V4Event> events = v5EventList.getEvents();
    if (events != null && !events.isEmpty()) {
      this.handleV4EventList(events);
    }
    return null;
  }

  private void recreateDatafeed() {
    try {
      log.info("Try to delete the faulty datafeed");
      this.deleteDatafeed();
      log.info("Recreate a new datafeed and try again");
      this.datafeed = this.createDatafeed();
    } catch (Throwable throwable) {
      throw new NestedRetryException("Recreation of datafeed failed", throwable);
    }
  }

  private void deleteDatafeed() throws Throwable {
    log.debug("Start deleting a faulty datafeed");
    deleteDatafeedRetry.execute();
  }

  private Void tryDeleteDatafeed() throws ApiException {
    this.datafeedApi.deleteDatafeed(datafeed.getId(), authSession.getSessionToken(), authSession.getKeyManagerToken());
    this.datafeed = null;
    return null;
  }
}
