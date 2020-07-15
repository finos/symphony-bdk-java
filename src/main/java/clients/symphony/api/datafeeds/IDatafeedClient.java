package clients.symphony.api.datafeeds;

import model.DatafeedEvent;
import model.datafeed.DatafeedV2;

import java.util.List;

/**
 * DatafeedClient interface for handling datafeed endpoints.
 */
interface IDatafeedClient {

    /**
     * Create a new datafeed for bot.
     *
     * @return          created datafeed id
     */
    String createDatafeed();

    /**
     * Read all the events in datafeed.
     *
     * @param   id      id of the datafeed to be read
     * @param   ackId   ackId given to delete all the events in datafeed
     * @return          list of events in the datafeed
     */
    List<DatafeedEvent> readDatafeed(String id, String... ackId);

    /**
     * List all the datafeed of a bot.
     * This feature is not supported in datafeed v1.
     *
     * @return          list of datafeeds that followed by the bot
     */
    List<DatafeedV2> listDatafeedId();

    /**
     * Delete the given datafeed.
     * This feature is not supported in datafeed v1.
     *
     * @param   id      id of the datafeed to be deleted
     */
    void deleteDatafeed(String id);

    /**
     * Get ackId for reading datafeedV2.
     * This feature is not supported in datafeed v1.
     *
     * @return          ackId for reading datafeedV2
     */
    String getAckId();
}
