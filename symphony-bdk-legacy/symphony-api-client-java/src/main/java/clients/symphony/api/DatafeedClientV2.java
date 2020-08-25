package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import configuration.SymConfig;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.DatafeedEvent;
import model.datafeed.DatafeedV2;
import model.datafeed.DatafeedV2EventList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * DatafeedClientV2 class for handling the datafeed endpoints version 2.
 */
final class DatafeedClientV2 extends APIClient implements IDatafeedClient {

    private final Logger logger = LoggerFactory.getLogger(DatafeedClientV2.class);
    private SymBotClient botClient;
    private SymConfig config;
    private String ackId;

    public DatafeedClientV2(SymBotClient client) {
        this.botClient = client;
        this.config = client.getConfig();
        this.ackId = "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDatafeed() throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
                .target(config.getAgentUrl())
                .path(AgentConstants.CREATEDATAFEEDV2)
                .request(MediaType.APPLICATION_JSON)
                .header(AgentConstants.SESSIONTOKENHEADER, botClient.getSymAuth().getSessionToken())
                .header(AgentConstants.KEYMANAGERTOKENHEADER, botClient.getSymAuth().getKmToken());

        logger.info("Create datafeed for bot {}..", botClient.getBotUsername());
        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException e) {
                    return createDatafeed();
                }
                return null;
            } else {
                DatafeedV2 datafeedId = response.readEntity(DatafeedV2.class);
                logger.info("Created datafeed {} for bot {}", datafeedId.getId(), botClient.getBotUsername());
                return datafeedId.getId();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<DatafeedEvent> readDatafeed(String datafeedId, String... ackId) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
                .target(config.getAgentUrl())
                .path(AgentConstants.READDATAFEEDV2.replace("{id}", datafeedId))
                .request(MediaType.APPLICATION_JSON)
                .header(AgentConstants.SESSIONTOKENHEADER, botClient.getSymAuth().getSessionToken())
                .header(AgentConstants.KEYMANAGERTOKENHEADER, botClient.getSymAuth().getKmToken());

        HashMap<String, String> query = new HashMap<>();
        if (ackId.length == 0) {
            query.put("ackId", "");
        } else {
            query.put("ackId", ackId[0]);
        }
        try (Response response = builder.post(Entity.entity(query, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException e) {
                    return readDatafeed(datafeedId, ackId);
                }
                return null;
            } else {
                DatafeedV2EventList datafeedEventList = response.readEntity(DatafeedV2EventList.class);
                logger.debug("Read datafeed events from datafeed {} ...", datafeedId);
                this.ackId = datafeedEventList.getAckId();
                return datafeedEventList.getEvents();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DatafeedV2> listDatafeedId() {
        Invocation.Builder builder = botClient.getAgentClient()
                .target(config.getAgentUrl())
                .path(AgentConstants.LISTDATAFEEDV2)
                .request(MediaType.APPLICATION_JSON)
                .header(AgentConstants.SESSIONTOKENHEADER, botClient.getSymAuth().getSessionToken())
                .header(AgentConstants.KEYMANAGERTOKENHEADER, botClient.getSymAuth().getKmToken());

        logger.info("Retrieving datafeeds for bot {}..", botClient.getBotUsername());
        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException e) {
                    return listDatafeedId();
                }
                return null;
            } else {
                List<DatafeedV2> datafeeds = response.readEntity(new GenericType<List<DatafeedV2>>() {});
                if (datafeeds.isEmpty()) {
                    logger.info("No datafeed was retrieved for bot {}", botClient.getBotUsername());
                } else {
                    logger.info("Retrieved datafeed {} for bot {}", datafeeds.get(0).getId(), botClient.getBotUsername());
                }
                return datafeeds;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDatafeed(String id) {
        Invocation.Builder builder = botClient.getAgentClient()
                .target(config.getAgentUrl())
                .path(AgentConstants.DELETEDATAFEEDV2.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header(AgentConstants.SESSIONTOKENHEADER, botClient.getSymAuth().getSessionToken())
                .header(AgentConstants.KEYMANAGERTOKENHEADER, botClient.getSymAuth().getKmToken());

        logger.info("Deleting datafeed {} for bot {}..", id, botClient.getBotUsername());
        try (Response response = builder.delete()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException e) {
                    deleteDatafeed(id);
                }
            } else {
                logger.info("Delete datafeeds {} for bot {} successfully", id, botClient.getBotUsername());
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAckId() {
        return this.ackId;
    }

}
