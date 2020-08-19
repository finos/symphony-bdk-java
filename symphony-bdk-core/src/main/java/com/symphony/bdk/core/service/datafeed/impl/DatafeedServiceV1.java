package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.V4Event;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void start() throws Throwable {
        this.datafeedId = this.retrieveDatafeedIdFromDisk();

        if (this.datafeedId == null) {
            this.datafeedId = this.createDatafeedAndSaveToDisk();
        }

        log.debug("Start reading events from datafeed {}", datafeedId);
        do {
            this.started.set(true);
            this.readDatafeed();
        } while (this.started.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.started.set(false);
        this.datafeedId = null;
    }

    private void readDatafeed() throws Throwable {
        this.retry.executeCheckedSupplier(() -> {
            try {
                List<V4Event> events = datafeedApi.v4DatafeedIdReadGet(datafeedId, authSession.getSessionToken(), authSession.getKeyManagerToken(), null);
                if (events != null && !events.isEmpty()) {
                    handleV4EventList(events);
                }
            } catch (ApiException e) {
                log.error("Error {}: {}", e.getCode(), e.getMessage());
                if (e.isUnauthorized()) {
                    log.info("Re-authenticate and try again");
                    authSession.refresh();
                }
                if (e.isClientError()) {
                    log.info("Recreate a new datafeed and try again");
                    datafeedId = this.createDatafeedAndSaveToDisk();
                }
                throw e;
            }
            return null;
        });
    }

    private String createDatafeedAndSaveToDisk() throws Throwable {
        log.debug("Start creating a new datafeed and save to disk");
        return this.retry.executeCheckedSupplier(() -> {
            try {
                String id = this.datafeedApi.v4DatafeedCreatePost(authSession.getSessionToken(), authSession.getKeyManagerToken()).getId();
                this.writeDatafeedIdToDisk(id);
                log.debug("Datafeed: {} was created and saved to disk", id);
                return id;
            } catch (ApiException e) {
                log.error("Error {}: {}", e.getCode(), e.getMessage());
                if (e.isUnauthorized()) {
                    log.info("Re-authenticate and try again");
                    authSession.refresh();
                }
                throw e;
            }
        });
    }

    private String retrieveDatafeedIdFromDisk() {
        log.debug("Start retrieving datafeed id from disk");
        String datafeedId = null;
        try {
            File file = this.getDatafeedIdFile();
            Path datafeedIdPath = Paths.get(file.getPath());
            String[] persistedDatafeed = Files.readAllLines(datafeedIdPath).get(0).split("@");
            datafeedId = persistedDatafeed[0];
            log.info("Retrieve datafeed id from persisted file: {}", datafeedId);
        } catch (IOException e) {
            log.debug(e.getMessage());
            log.info("No persisted datafeed id could be retrieved from the filesystem");
        }
        return datafeedId;
    }

    private void writeDatafeedIdToDisk(String datafeedId) {
        String agentUrl = bdkConfig.getAgent().getHost() + ":" + bdkConfig.getAgent().getPort();
        try (FileWriter fw = new FileWriter(this.getDatafeedIdFile())) {
            fw.write(datafeedId + "@" + agentUrl);
            fw.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private File getDatafeedIdFile() {
        String pathToDatafeedIdFile = bdkConfig.getDatafeed().getDatafeedIdFilePath() + DATAFEED_ID_FILE;

        File file = new File(pathToDatafeedIdFile);
        if (file.isDirectory()) {
            file = new File(pathToDatafeedIdFile + File.separator + DATAFEED_ID_FILE);
        }
        return file;
    }

}
