package services;

import clients.SymBotClient;
import configuration.LoadBalancingMethod;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import model.DatafeedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DatafeedEventsServiceV2 providing services to bot for subscribing the datafeed version 1.
 */
class DatafeedEventsServiceV1 extends AbstractDatafeedEventsService {

    private final Logger logger = LoggerFactory.getLogger(DatafeedEventsServiceV1.class);

    private static final int MAX_BACKOFF_TIME = 5 * 60; // five minutes

    private String datafeedId = null;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();
    private static int THREADPOOL_SIZE;
    private static int TIMEOUT_NO_OF_SECS;

    public DatafeedEventsServiceV1(SymBotClient client) {
        super(client);

        int threadPoolSize = client.getConfig().getDatafeedEventsThreadpoolSize();
        THREADPOOL_SIZE = threadPoolSize > 0 ? threadPoolSize : 5;
        resetTimeout();

        // if the reuseDatafeedID config isn't set (null), we assume its default value as true
        if (botClient.getConfig().getReuseDatafeedID() == null || this.botClient.getConfig().getReuseDatafeedID()) {
            try {
                File file = botClient.getDatafeedIdFile();
                Path datafeedIdPath = Paths.get(file.getPath());
                String[] persistedDatafeed = Files.readAllLines(datafeedIdPath).get(0).split("@");
                datafeedId = persistedDatafeed[0];

                if (client.getConfig() instanceof SymLoadBalancedConfig) {
                    SymLoadBalancedConfig lbConfig = (SymLoadBalancedConfig) client.getConfig();
                    String[] agentHostPort = persistedDatafeed[1].split(":");
                    if (lbConfig.getLoadBalancing().getMethod() == LoadBalancingMethod.external) {
                        lbConfig.setActualAgentHost(agentHostPort[0]);
                    } else {
                        int previousIndex = lbConfig.getAgentServers().indexOf(agentHostPort[0]);
                        lbConfig.setCurrentAgentIndex(previousIndex);
                    }
                }

                logger.info("Using previous datafeed id: {}", datafeedId);
            } catch (IOException e) {
                logger.info("No previous datafeed id file");
            }
        }

        while (datafeedId == null) {
            try {
                datafeedId = datafeedClient.createDatafeed();
                resetTimeout();
            } catch (Exception e) {
                handleError(e);
            }
        }
        readDatafeed();
        stop.set(false);
    }

    private void resetTimeout() {
        int errorTimeout = this.botClient.getConfig().getDatafeedEventsErrorTimeout();
        TIMEOUT_NO_OF_SECS = errorTimeout > 0 ? errorTimeout : 30;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readDatafeed() {
        if (pool != null) {
            pool.shutdown();
        }
        pool = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        CompletableFuture.supplyAsync(() -> {
            while (!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            List<DatafeedEvent> events = datafeedClient.readDatafeed(datafeedId);
                            resetTimeout();
                            return events;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, pool)
                    .exceptionally((ex) -> {
                        handleError(ex);
                        return Collections.emptyList();
                    })
                    .thenApply(events -> {
                        if (events != null && !events.isEmpty()) {
                            handleEvents(events);
                        }
                        return Collections.emptyList();
                    });

                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error trying to read datafeed", e);
                }
            }
            return Collections.emptyList();
        }, pool);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopDatafeedService() {
        if (!stop.get()) {
            stop.set(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restartDatafeedService() {
        if (stop.get()) {
            stop.set(false);
        }
        datafeedId = datafeedClient.createDatafeed();

        readDatafeed();
    }

    private void handleError(Throwable e) {
        String errMsg = e.getMessage();
        if (errMsg.endsWith("SocketTimeoutException: Read timed out")) {
            int connectionTimeoutSeconds = botClient.getConfig().getConnectionTimeout() / 1000;
            logger.error("Connection timed out after {} seconds", connectionTimeoutSeconds);
        } else if (errMsg.endsWith("Origin Error") || errMsg.endsWith("Service Unavailable") || errMsg.endsWith("Bad Gateway")) {
            logger.error("Pod is unavailable");
        } else if (errMsg.contains("Could not find a datafeed with the")) {
            logger.error(errMsg);
        } else {
            logger.error("An unknown error happened, type : " + e.getClass(), e);
        }

        sleep();

        try {
            SymConfig config = botClient.getConfig();
            if (config instanceof SymLoadBalancedConfig) {
                ((SymLoadBalancedConfig) config).rotateAgent();
            }
            datafeedId = datafeedClient.createDatafeed();
            resetTimeout();
        } catch (Exception e1) {
            sleep();
            handleError(e);
        }
    }

    private void sleep() {
        try {
            logger.info("Sleeping for {} seconds before retrying..", TIMEOUT_NO_OF_SECS);
            TimeUnit.SECONDS.sleep(TIMEOUT_NO_OF_SECS);

            // exponential backoff until we reach the MAX_BACKOFF_TIME (5 minutes)
            if (TIMEOUT_NO_OF_SECS * 2 <= MAX_BACKOFF_TIME) {
                TIMEOUT_NO_OF_SECS *= 2;
            } else {
                TIMEOUT_NO_OF_SECS = MAX_BACKOFF_TIME;
            }
        } catch (InterruptedException ie) {
            logger.error("Error trying to sleep ", ie);
        }
    }

}
