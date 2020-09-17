package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;
import com.symphony.bdk.core.util.RetryWithRecovery;
import com.symphony.bdk.core.util.ConsumerWithThrowable;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import javax.ws.rs.ProcessingException;

/**
 * A class for implementing the datafeed v1 service.
 *
 * This service will be started by calling {@link DatafeedServiceV1#start()}
 *
 * At the beginning, a datafeed will be created and the BDK bot will listen to this datafeed to receive the real-time
 * events. With datafeed service v1, we don't have the api endpoint to retrieve the datafeed id that a service account
 * is listening, so, the id of the created datafeed must be persisted in the bot side.
 *
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 *
 * From the second time, the bot will firstly retrieve the datafeed that was persisted and try to read the real-time
 * events from this datafeed. If this datafeed is expired or faulty, the datafeed service will create the new one for
 * listening.
 *
 * This service will be stopped by calling {@link DatafeedServiceV1#stop()}
 *
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
public class DatafeedServiceV1 extends AbstractDatafeedService {

    private final AtomicBoolean started = new AtomicBoolean();
    private final DatafeedIdRepository datafeedRepository;
    private String datafeedId;
    private RetryWithRecovery<Void> readDatafeedRetry;
    private RetryWithRecovery<String> createDatafeedRetry;

    public DatafeedServiceV1(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
        this(datafeedApi, authSession, config, new OnDiskDatafeedIdRepository(config));
    }

    public DatafeedServiceV1(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config, DatafeedIdRepository repository) {
      super(datafeedApi, authSession, config);
      this.started.set(false);
      this.datafeedId = null;
      this.datafeedRepository = repository;

      Map<Predicate<ApiException>, ConsumerWithThrowable> readRecoveryStrategy = new HashMap<>();
      readRecoveryStrategy.put(ApiException::isUnauthorized, e -> refresh());
      readRecoveryStrategy.put(ApiException::isClientError, this::recreateDatafeed);

      this.readDatafeedRetry = new RetryWithRecovery<>("Read Datafeedv1", () -> {readAndHandleEvents(); return null;},
          this::retryReadOnException, this.bdkConfig.getDatafeedRetryConfig(), readRecoveryStrategy);
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
                this.readDatafeed();
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

    private boolean retryReadOnException(Throwable t) {
      if (t instanceof ApiException && t.getSuppressed().length == 0) {
        ApiException apiException = (ApiException) t;
        return apiException.isServerError() || apiException.isUnauthorized() || apiException.isClientError();
      }
      return t instanceof ProcessingException;
    }

    private void readAndHandleEvents() throws ApiException {
      log.debug("Read DFv1 with RetryWithRecovery");
      List<V4Event> events = datafeedApi.v4DatafeedIdReadGet(datafeedId, authSession.getSessionToken(), authSession.getKeyManagerToken(), null);
      if (events != null && !events.isEmpty()) {
        handleV4EventList(events);
      }
    }

    private void refresh() throws AuthUnauthorizedException {
      log.info("Re-authenticate and try again");
      authSession.refresh();
    }

    private void recreateDatafeed(ApiException e) {
      log.info("Recreate a new datafeed and try again");
      try {
        datafeedId = this.createDatafeed();
      } catch (Throwable throwable) {
        e.addSuppressed(throwable);
      }
    }

    private void readDatafeed() throws Throwable {
      log.debug("Read DFv1, calling RetryWithRecovery");
      this.readDatafeedRetry.execute();
    }

    protected String createDatafeed() throws Throwable {
        log.debug("Start creating a new datafeed and persisting it");
        Retry retry = this.getRetryInstance("Create Datafeed");
        return retry.executeCheckedSupplier(() -> {
            try {
                String id = this.datafeedApi.v4DatafeedCreatePost(authSession.getSessionToken(), authSession.getKeyManagerToken()).getId();
                this.datafeedRepository.write(id);
                log.debug("Datafeed: {} was created and persisted", id);
                return id;
            } catch (ApiException e) {
              //TODO
                if (e.isUnauthorized()) {
                    log.info("Re-authenticate and try again");
                    authSession.refresh();
                } else {
                    log.error("Error {}: {}", e.getCode(), e.getMessage());
                }
                throw e;
            }
        });
    }

    protected Optional<String> retrieveDatafeed() {
        log.debug("Start retrieving datafeed id");
        return this.datafeedRepository.read();
    }

}
