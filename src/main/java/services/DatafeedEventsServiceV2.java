package services;

import clients.SymBotClient;
import model.DatafeedEvent;
import model.datafeed.DatafeedV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DatafeedEventsServiceV2 providing services to bot for subscribing the datafeed version 2.
 */
class DatafeedEventsServiceV2 extends AbstractDatafeedEventsService {

    private static final Logger logger = LoggerFactory.getLogger(DatafeedEventsServiceV2.class);

    private final AtomicBoolean started = new AtomicBoolean(false);

    public DatafeedEventsServiceV2(SymBotClient client) {
        super(client);
        this.readDatafeed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readDatafeed() {
        try {
            String datafeedId;
            List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();
            if (datafeedIds.isEmpty()) {
                datafeedId = this.createDatafeedId();
            } else {
                //Each bot should subscribe only one datafeed
                datafeedId = datafeedIds.get(0).getId();
            }

            //Read datafeed in loop
            started.set(true);
            List<DatafeedEvent> events = null;
            do {
                events = datafeedClient.readDatafeed(datafeedId, datafeedClient.getAckId());
                if (events != null && !events.isEmpty()) {
                    handleEvents(events);
                }
            } while (started.get());
        } catch (Exception e) {
            this.started.set(false);
            logger.error("Something went wrong while reading datafeed", e);
            logger.info("Sleeping for {} seconds before retrying..", botClient.getConfig().getDatafeedEventsErrorTimeout());
            sleep(botClient.getConfig().getDatafeedEventsErrorTimeout());
            readDatafeed();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopDatafeedService() {
        this.started.set(false);
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
                sleep(botClient.getConfig().getDatafeedEventsErrorTimeout());
            }
        } while (datafeedId == null);
        return datafeedId;
    }

    private static void sleep(int timeInSecs) {
        try {
            TimeUnit.SECONDS.sleep(timeInSecs);
        } catch (InterruptedException e) {
            logger.error("Thread sleep has failed.", e);
        }
    }

}
