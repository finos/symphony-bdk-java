package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class for implementing the datafeed v1 service.
 * <p>
 * This service will be started by calling {@link DatafeedServiceV1#start()}
 * <p>
 * At the beginning, a datafeed will be created and the BDK bot will listen to this datafeed to receive the real-time
 * events. With datafeed service v1, we don't have the api endpoint to retrieve the datafeed id that a service account
 * is listening, so, the id of the created datafeed must be persisted in the bot side.
 * <p>
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 * <p>
 * From the second time, the bot will firstly retrieve the datafeed that was persisted and try to read the real-time
 * events from this datafeed. If this datafeed is expired or faulty, the datafeed service will create the new one for
 * listening.
 * <p>
 * This service will be stopped by calling {@link DatafeedServiceV1#stop()}
 * <p>
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
public class DatafeedServiceV1 extends AbstractDatafeedService {

  private final AtomicBoolean started = new AtomicBoolean();
  private final DatafeedIdRepository datafeedRepository;
  private String datafeedId;

  public DatafeedServiceV1(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
    this(datafeedApi, authSession, config, new OnDiskDatafeedIdRepository(config));
  }

  public DatafeedServiceV1(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config,
      DatafeedIdRepository repository) {
    super(datafeedApi, authSession, config);
    this.started.set(false);
    this.datafeedId = null;
    this.datafeedRepository = repository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() throws AuthUnauthorizedException, ApiException {
    if (this.started.get()) {
      throw new IllegalStateException("The datafeed service is already started");
    }
    Optional<String> persistedDatafeedId = this.retrieveDatafeed();

    try {
      this.datafeedId = persistedDatafeedId.orElse(this.createDatafeed());
      log.debug("Start reading events from datafeed {}", datafeedId);
      this.started.set(true);
      do {
        readDatafeed();
      } while (this.started.get());
    } catch (AuthUnauthorizedException | ApiException exception) {
      throw exception;
    } catch (Throwable throwable) {
      log.error("Unknown error", throwable);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    log.info("Stop the datafeed service");
    this.started.set(false);
  }

  private Void readAndHandleEvents() throws ApiException {
    List<V4Event> events =
        datafeedApi.v4DatafeedIdReadGet(datafeedId, authSession.getSessionToken(), authSession.getKeyManagerToken(),
            null);
    if (events != null && !events.isEmpty()) {
      handleV4EventList(events);
    }
    return null;
  }

  private void readDatafeed() throws Throwable {
    final RetryWithRecovery<Void> retry = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Read Datafeed V1")
        .supplier(this::readAndHandleEvents)
        .recoveryStrategy(ApiException::isClientError, this::recreateDatafeed)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkOrMinorErrorOrClientError)
        .build();
    retry.execute();
  }

  private void recreateDatafeed() {
    log.info("Recreate a new datafeed and try again");
    try {
      datafeedId = createDatafeed();
    } catch (Throwable throwable) {
      throw new NestedRetryException("Recreation of datafeed failed", throwable);
    }
  }

  protected String createDatafeed() throws Throwable {
    log.debug("Start creating a new datafeed and persisting it");
    final RetryWithRecovery<String> retry = RetryWithRecoveryBuilder.<String>from(retryWithRecoveryBuilder)
        .name("Create Datafeed V1")
        .supplier(this::createDatafeedAndPersist)
        .build();
    return retry.execute();
  }

  private String createDatafeedAndPersist() throws ApiException {
    String id = datafeedApi.v4DatafeedCreatePost(authSession.getSessionToken(), authSession.getKeyManagerToken()).getId();
    datafeedRepository.write(id);
    log.debug("Datafeed: {} was created and persisted", id);
    return id;
  }

  protected Optional<String> retrieveDatafeed() {
    log.debug("Start retrieving datafeed id");
    return this.datafeedRepository.read();
  }
}
