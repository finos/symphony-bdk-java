package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.V4Event;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class for implementing the datafeed v1 service.
 */
@Slf4j
public class DatafeedServiceV1 extends AbstractDatafeedService {

    private static final String DATAFEED_ID_FILE = "datafeed.id";

    private final AtomicBoolean started = new AtomicBoolean();
    private String datafeedId;

    public DatafeedServiceV1(ApiClient agentClient, ApiClient podClient, AuthSession authSession, BdkConfig config) {
        super(agentClient, podClient, authSession, config);
        this.started.set(false);
        this.datafeedId = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws AuthUnauthorizedException, ApiException {
        if (this.started.get()) {
            throw new IllegalStateException("The datafeed service is already started");
        }
        this.datafeedId = this.retrieveDatafeedIdFromDisk();

        try {
            if (this.datafeedId == null) {
                this.datafeedId = this.createDatafeedAndSaveToDisk();
            }

            log.debug("Start reading events from datafeed {}", datafeedId);
            do {
                this.started.set(true);
                this.readDatafeed();
            } while (this.started.get());
        } catch (Throwable e) {
            if (e instanceof ApiException) {
                throw (ApiException) e;
            } else if (e instanceof AuthUnauthorizedException) {
                throw (AuthUnauthorizedException) e;
            } else {
                e.printStackTrace();
            }
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

    private void readDatafeed() throws Throwable {
        RetryConfig config = RetryConfig.from(this.retryConfig).retryOnException(e -> {
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                return apiException.isServerError() || apiException.isUnauthorized() || apiException.isClientError();
            }
            return false;
        }).build();
        Retry retry = this.getRetryInstance("Read Datafeed", config);
        retry.executeCheckedSupplier(() -> {
            try {
                List<V4Event> events = datafeedApi.v4DatafeedIdReadGet(datafeedId, authSession.getSessionToken(), authSession.getKeyManagerToken(), null);
                if (events != null && !events.isEmpty()) {
                    handleV4EventList(events);
                }
            } catch (ApiException e) {
                if (e.isUnauthorized()) {
                    log.info("Re-authenticate and try again");
                    authSession.refresh();
                } else {
                    log.error("Error {}: {}", e.getCode(), e.getMessage());
                    if (e.isClientError()) {
                        log.info("Recreate a new datafeed and try again");
                        datafeedId = this.createDatafeedAndSaveToDisk();
                    }
                }
                throw e;
            }
            return null;
        });
    }

    protected String createDatafeedAndSaveToDisk() throws Throwable {
        log.debug("Start creating a new datafeed and save to disk");
        Retry retry = this.getRetryInstance("Create Datafeed");
        return retry.executeCheckedSupplier(() -> {
            try {
                String id = this.datafeedApi.v4DatafeedCreatePost(authSession.getSessionToken(), authSession.getKeyManagerToken()).getId();
                this.writeDatafeedIdToDisk(id);
                log.debug("Datafeed: {} was created and saved to disk", id);
                return id;
            } catch (ApiException e) {
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

    protected String retrieveDatafeedIdFromDisk() {
        log.debug("Start retrieving datafeed id from disk");
        String datafeedId = null;
        try {
            File file = this.getDatafeedIdFile();
            Path datafeedIdPath = Paths.get(file.getPath());
            List<String> lines = Files.readAllLines(datafeedIdPath);
            if (lines.isEmpty()) {
                return null;
            }
            String[] persistedDatafeed = lines.get(0).split("@");
            datafeedId = persistedDatafeed[0];
            log.info("Retrieve datafeed id from persisted file: {}", datafeedId);
        } catch (IOException e) {
            log.debug("No persisted datafeed id could be retrieved from the filesystem");
        }
        return datafeedId;
    }

    protected void writeDatafeedIdToDisk(String datafeedId) {
        String agentUrl = bdkConfig.getAgent().getHost() + ":" + bdkConfig.getAgent().getPort();
        try {
            FileUtils.writeStringToFile(this.getDatafeedIdFile(), datafeedId + "@" + agentUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private File getDatafeedIdFile() {
        String pathToDatafeedIdFile = bdkConfig.getDatafeed().getIdFilePath();

        File file = new File(pathToDatafeedIdFile);
        if (file.isDirectory()) {
            file = new File(pathToDatafeedIdFile + File.separator + DATAFEED_ID_FILE);
        }
        return file;
    }

}
