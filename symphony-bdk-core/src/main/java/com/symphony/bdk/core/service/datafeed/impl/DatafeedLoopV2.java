package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class for implementing the datafeed v2 loop service.
 * <p>
 * This service will be started by calling {@link DatafeedLoopV2#start()}
 * <p>
 * At the beginning, the BDK bot will try to retrieve the list of datafeed to which it is listening. Since each bot
 * should only listening to just one datafeed, the first datafeed in the list will be used by the bot to be listened to.
 * If the retrieved list is empty, the BDK bot will create a new datafeed to listen.
 * <p>
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 * <p>
 * If this datafeed becomes stale or faulty, the BDK bot will create the new one for listening.
 * <p>
 * This service will be stopped by calling {@link DatafeedLoopV2#stop()}
 * <p>
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedLoopV2 extends AbstractDatafeedLoop {

  private final AtomicBoolean started = new AtomicBoolean();
  private final AckId ackId;
  private V5Datafeed datafeed;

  public DatafeedLoopV2(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    super(datafeedApi, authSession, config);
    this.ackId = new AckId().ackId("");
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
    } catch (AuthUnauthorizedException | ApiException | NestedRetryException exception) {
      throw exception;
    } catch (Throwable throwable) {
      if (throwable.getCause() instanceof ConnectException) {
        throw new RuntimeException(
            "Failed while trying to connect to the \"AGENT\" at the following address: " + apiClient.getBasePath(),
            throwable);
      }
      log.error("Unknown error occurred", throwable);
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

    final RetryWithRecovery<V5Datafeed> retry = RetryWithRecoveryBuilder.<V5Datafeed>from(retryWithRecoveryBuilder)
        .name("Create Datafeed V2")
        .supplier(this::tryCreateDatafeed)
        .build();

    return retry.execute();
  }

  private V5Datafeed tryCreateDatafeed() throws ApiException {
    return this.datafeedApi.createDatafeed(authSession.getSessionToken(), authSession.getKeyManagerToken());
  }

  private V5Datafeed retrieveDatafeed() throws Throwable {
    log.debug("Start retrieving datafeed from agent");

    final RetryWithRecovery<V5Datafeed> retry = RetryWithRecoveryBuilder.<V5Datafeed>from(retryWithRecoveryBuilder)
        .name("Retrieve Datafeed V2")
        .supplier(this::tryRetrieveDatafeed)
        .build();

    return retry.execute();
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

    final RetryWithRecovery<Void> retry = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Read Datafeed V2")
        .supplier(this::readAndHandleEvents)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkOrMinorErrorOrClientError)
        .recoveryStrategy(ApiException::isClientError, this::recreateDatafeed)
        .build();

    retry.execute();
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

    final RetryWithRecovery<Void> retry = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Delete Datafeed V2")
        .supplier(this::tryDeleteDatafeed)
        .ignoreException(ApiException::isClientError)
        .build();

    retry.execute();
  }

  private Void tryDeleteDatafeed() throws ApiException {
    this.datafeedApi.deleteDatafeed(datafeed.getId(), authSession.getSessionToken(), authSession.getKeyManagerToken());
    this.datafeed = null;
    return null;
  }
}
