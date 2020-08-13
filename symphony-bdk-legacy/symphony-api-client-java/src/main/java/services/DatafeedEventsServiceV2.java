package services;

import clients.SymBotClient;
import exceptions.APIClientErrorException;
import lombok.SneakyThrows;
import model.DatafeedEvent;
import model.datafeed.DatafeedV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DatafeedEventsServiceV2 providing services to bot for subscribing the datafeed version 2.
 */
class DatafeedEventsServiceV2 extends AbstractDatafeedEventsService {

    private static final Logger logger = LoggerFactory.getLogger(DatafeedEventsServiceV2.class);

    private static final int AWAIT_TERMINATION = 1000;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private ExecutorService threadPool;
    private final Sleeper sleeper;

    public DatafeedEventsServiceV2(SymBotClient client) {
        super(client);
        this.sleeper = new Sleeper();
        this.readDatafeed();
    }

    DatafeedEventsServiceV2(SymBotClient client, Sleeper sleeper) {
        super(client);
        this.sleeper = sleeper;
        this.readDatafeed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readDatafeed() {
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        threadPool = Executors.newSingleThreadExecutor();
        started.set(true);
        threadPool.submit(() -> {
            do {
                try {
                    readEventsFromDatafeed();
                } catch (Exception e) {
                    logger.error("Something went wrong while reading datafeed", e);
                    logger.info("Sleeping for {} seconds before retrying..", botClient.getConfig().getDatafeedEventsErrorTimeout());
                    sleeper.sleep(botClient.getConfig().getDatafeedEventsErrorTimeout());
                }
            } while (started.get());
        });
    }

    @SneakyThrows
    private void readEventsFromDatafeed() {
        String datafeedId;
        List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
        if (datafeedIds.isEmpty()) {
            datafeedId = this.createDatafeedId();
        } else {
            //Each bot should subscribe only one datafeed
            datafeedId = datafeedIds.get(0).getId();
        }
        //Read datafeed in loop
        logger.info("Start reading datafeed events from datafeed {}", datafeedId);
        do {
            try {
                List<DatafeedEvent> events = datafeedClient.readDatafeed(datafeedId, datafeedClient.getAckId());
                if (events != null && !events.isEmpty()) {
                    handleEvents(events);
                }
            } catch (APIClientErrorException e) {
                //Datafeed was stale
                logger.debug("Something went wrong with this datafeed: {}", datafeedId);
                logger.info(e.getMessage());
                datafeedClient.deleteDatafeed(datafeedId);
                break;
            }

        } while (started.get());
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void stopDatafeedService() {
        this.started.set(false);
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(AWAIT_TERMINATION, TimeUnit.MILLISECONDS)) {
                    threadPool.shutdownNow();
                    if (!threadPool.awaitTermination(AWAIT_TERMINATION, TimeUnit.MILLISECONDS))
                        logger.error("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restartDatafeedService() {
        this.started.set(false);
        this.readDatafeed();
    }

    private String createDatafeedId() {
        String datafeedId = null;
        do {
            try {
                datafeedId = datafeedClient.createDatafeed();
            } catch (Exception e) {
                logger.error("Unable to create feedId ({}), will retry in {} seconds.", e.getMessage(),
                        botClient.getConfig().getDatafeedEventsErrorTimeout());
                logger.trace("More details :", e);
                sleeper.sleep(botClient.getConfig().getDatafeedEventsErrorTimeout());
            }
        } while (datafeedId == null);
        return datafeedId;
    }

}
