package services;

import listeners.*;

/**
 * DatafeedEventsService Interface providing services to bot for subscribing the datafeed.
 */
interface IDatafeedEventsService {

    /**
     * Add listeners to a datafeed service.
     *
     * @param listeners     listeners to be added.
     */
    void addListeners(DatafeedListener... listeners);

    /**
     * Remove existing listeners from a datafeed service.
     *
     * @param listeners     listeners to be removed.
     */
    void removeListeners(DatafeedListener... listeners);

    /**
     * Add a room listener to a datafeed service.
     *
     * @param listener      room listener to be added.
     */
    void addRoomListener(RoomListener listener);

    /**
     *  Remove a room listener from a datafeed service.
     *
     * @param listener      room listener to be removed
     */
    void removeRoomListener(RoomListener listener);

    /**
     * Add a IM listener to a datafeed service.
     *
     * @param listener      IM listener to be added.
     */
    void addIMListener(IMListener listener);

    /**
     *  Remove a IM listener from a datafeed service.
     *
     * @param listener      IM listener to be removed
     */
    void removeIMListener(IMListener listener);

    /**
     * Add a Connections listener to a datafeed service.
     *
     * @param listener      Connections listener to be added.
     */
    void addConnectionsListener(ConnectionListener listener);

    /**
     *  Remove a Connections listener from a datafeed service.
     *
     * @param listener      Connections listener to be removed
     */
    void removeConnectionsListener(ConnectionListener listener);

    /**
     * Add a Elements listener to a datafeed service.
     *
     * @param listener      Elements listener to be added.
     */
    void addElementsListener(ElementsListener listener);

    /**
     *  Remove a Elements listener from a datafeed service.
     *
     * @param listener      Elements listener to be removed
     */
    void removeElementsListener(ElementsListener listener);

    /**
     * Start reading events from datafeed.
     */
    void readDatafeed();

    /**
     * Stop reading events from datafeed.
     */
    void stopDatafeedService();

    /**
     * Restart the datafeed service.
     */
    void restartDatafeedService();

}
