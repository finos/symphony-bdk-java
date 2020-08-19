package com.symphony.bdk.core.service.datafeed;

public interface DatafeedService {

    /**
     * Start the datafeed events service
     *
     */
    void start() throws Throwable;

    /**
     * Stop the datafeed events service
     *
     */
    void stop();

    /**
     * The bot subscribes to a {@link DatafeedEventListener}
     *
     * @param listener a Datafeed event listener to be subscribed
     */
    void subscribe(DatafeedEventListener listener);

    /**
     * The bot unsubscribes to a {@link DatafeedEventListener}
     *
     * @param listener a Datafeed event listener to be unsubscribed
     */
    void unsubscribe(DatafeedEventListener listener);
}
