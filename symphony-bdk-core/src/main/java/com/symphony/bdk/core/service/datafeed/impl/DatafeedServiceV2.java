package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5EventList;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.ProcessingException;

/**
 * A class for implementing the datafeed v2 service.
 *
 * This service will be started by calling {@link DatafeedServiceV2#start()}
 *
 * At the beginning, the BDK bot will try to retrieve the list of datafeed to which it is listening. Since each bot
 * should only listening to just one datafeed, the first datafeed in the list will be used by the bot to be listened to.
 * If the retrieved list is empty, the BDK bot will create a new datafeed to listen.
 *
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 *
 * If this datafeed becomes stale or faulty, the BDK bot will create the new one for listening.
 *
 * This service will be stopped by calling {@link DatafeedServiceV2#stop()}
 *
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
public class DatafeedServiceV2 extends AbstractDatafeedService {

    private final AtomicBoolean started = new AtomicBoolean();
    private final AckId ackId;
    private V5Datafeed datafeed;

    public DatafeedServiceV2(DatafeedApi datafeedApi, AuthSession authSession, BdkConfig config) {
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
        Retry retry = this.getRetryInstance("Create V5Datafeed");
        return retry.executeCheckedSupplier(() -> {
            try {
                return this.datafeedApi.createDatafeed(authSession.getSessionToken(), authSession.getKeyManagerToken());
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

    private V5Datafeed retrieveDatafeed() throws Throwable {
        log.debug("Start retrieving datafeed from agent");
        Retry retry = this.getRetryInstance("Retrieve V5Datafeed");
        List<V5Datafeed> datafeeds = retry.executeCheckedSupplier(() -> {
            try {
                return this.datafeedApi.listDatafeed(authSession.getSessionToken(), authSession.getKeyManagerToken());
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
        if (!datafeeds.isEmpty()) {
            return datafeeds.get(0);
        }
        return null;
    }

    private void readDatafeed() throws Throwable {
        log.debug("Reading datafeed events from datafeed {}", datafeed.getId());
        RetryConfig config = RetryConfig.from(this.retryConfig)
                .retryOnException(e -> {
                  //TODO
                    if (e instanceof ApiException && e.getSuppressed().length == 0) {
                        ApiException apiException = (ApiException) e;
                        return apiException.isServerError() || apiException.isUnauthorized() || apiException.isClientError();
                    }
                    return e instanceof ProcessingException;
                }).build();
        Retry retry = this.getRetryInstance("Read Datafeed", config);
        retry.executeCheckedSupplier(() -> {
            try {
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
            } catch (ApiException e) {
              //TODO
                if (e.isUnauthorized()) {
                    log.info("Re-authenticate and try again");
                    authSession.refresh();
                } else {
                    log.error("Error {}: {}", e.getCode(), e.getMessage());
                    if (e.isClientError()) { // recreate DF if 400 BAD REQUEST
                        try {
                            log.info("Try to delete the faulty datafeed");
                            this.deleteDatafeed();
                            log.info("Recreate a new datafeed and try again");
                            this.datafeed = this.createDatafeed();
                        } catch (Throwable throwable) {
                            e.addSuppressed(throwable);
                        }
                    }
                }
                throw e;
            }
            return null;
        });
    }

    private void deleteDatafeed() throws Throwable {
        log.debug("Start deleting a faulty datafeed");
        Retry retry = this.getRetryInstance("Delete Datafeed");
        retry.executeCheckedSupplier(() -> {
            try {
                this.datafeedApi.deleteDatafeed(datafeed.getId(), authSession.getSessionToken(), authSession.getKeyManagerToken());
                this.datafeed = null;
            } catch (ApiException e) {
              //TODO
                if (e.isClientError()) {
                    log.debug("The datafeed doesn't exist or is already removed");
                } else {
                    if (e.isUnauthorized()) {
                        log.info("Re-authenticate and try again");
                        authSession.refresh();
                    } else {
                        log.error("Error {}: {}", e.getCode(), e.getMessage());
                    }
                    throw e;
                }
            }
            return null;
        });
    }

}
