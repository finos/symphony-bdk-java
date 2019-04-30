package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import exceptions.SymClientException;
import listeners.FirehoseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirehoseService {
    private final Logger logger = LoggerFactory.getLogger(FirehoseService.class);
    private SymBotClient botClient;
    private FirehoseClient firehoseClient;
    private List<FirehoseListener> listeners;
    private String firehoseId;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();

    public FirehoseService(SymBotClient client) {
        this.botClient = client;
        listeners = new ArrayList<>();
        firehoseClient = this.botClient.getFirehoseClient();
        firehoseId = firehoseClient.createFirehose();

        readFirehose();
        stop.set(false);
    }

    public FirehoseService(SymBotClient client, String firehoseId) {
        this.botClient = client;
        listeners = new ArrayList<>();
        firehoseClient = this.botClient.getFirehoseClient();
        this.firehoseId = firehoseId;

        readFirehose();
        stop.set(false);
    }

    public void addListener(FirehoseListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FirehoseListener listener) {
        listeners.remove(listener);
    }


    public void readFirehose() {
        if (pool != null) {
            pool.shutdown();
        }
        pool = Executors.newFixedThreadPool(5);
        CompletableFuture.supplyAsync(() -> {
            while (!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return firehoseClient.readFirehose(firehoseId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, pool)
                    .exceptionally((ex) -> {
                        handleError(ex);
                        return null;
                    })
                    .thenApply(events -> {
                        if (events != null && !events.isEmpty()) {
                            FirehoseV2Service.handleEvents(events, listeners);
                        }
                        return null;
                    });
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error trying to read firehose ", e);
                }
            }
            return null;
        }, pool);
    }

    public void stopDatafeedService() {
        if (!stop.get()) stop.set(true);
    }

    public void restartDatafeedService() {
        if (stop.get()) stop.set(false);
        firehoseId = firehoseClient.createFirehose();

        readFirehose();
    }

    private void handleError(Throwable e) {
        logger.error(e.getMessage());
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ie) {
            logger.error("Error trying to sleep ", ie);
        }
        try {
            firehoseId = firehoseClient.createFirehose();
        } catch (SymClientException e1) {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException ie) {
                logger.error("Error trying to sleep ", ie);
            }
            handleError(e);
        }
    }
}
