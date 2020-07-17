package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.DatafeedVersion;
import configuration.SymConfig;
import exceptions.SymClientException;
import model.DatafeedEvent;
import model.datafeed.DatafeedV2;

import java.util.List;

/**
 * DatafeedClient class for choosing the datafeed client to use based on the datafeedVersion given in config.
 */
public final class DatafeedClient implements IDatafeedClient {

    private final IDatafeedClient datafeedClient;

    public DatafeedClient(SymBotClient client) {
        SymConfig config = client.getConfig();
        if (DatafeedVersion.V2 == DatafeedVersion.of(config.getDatafeedVersion())) {
            this.datafeedClient = new DatafeedClientV2(client);
        } else {
            this.datafeedClient = new DatafeedClientV1(client);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDatafeed() throws SymClientException {
        return this.datafeedClient.createDatafeed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DatafeedEvent> readDatafeed(String id, String... ackId) throws SymClientException {
        return this.datafeedClient.readDatafeed(id, ackId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DatafeedV2> listDatafeedId() throws SymClientException {
        return this.datafeedClient.listDatafeedId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDatafeed(String id) throws SymClientException {
        this.datafeedClient.deleteDatafeed(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAckId() {
        return this.datafeedClient.getAckId();
    }
}
