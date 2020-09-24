package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

/**
 * Interface definition for a service to be used for handling the datafeed API.
 */
public interface DatafeedService {

    /**
     * Start the datafeed events service
     *
     */
    void start() throws AuthUnauthorizedException, ApiException;

    /**
     * Stop the datafeed events service.
     * The datafeed service will be stopped after a small delay to finish the last read datafeed call.
     *
     */
    void stop();

    /**
     * The bot subscribes to a {@link RealTimeEventListener}
     *
     * @param listener a Datafeed event listener to be subscribed
     */
    void subscribe(RealTimeEventListener listener);

    /**
     * The bot unsubscribes to a {@link RealTimeEventListener}
     *
     * @param listener a Datafeed event listener to be unsubscribed
     */
    void unsubscribe(RealTimeEventListener listener);
}
