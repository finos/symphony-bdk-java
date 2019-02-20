package clients.symphony.api;


import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class DatafeedClient extends  APIClient {
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;
    private SymConfig config;

    public DatafeedClient(SymBotClient client) {
        this.botClient = client;
        this.config = client.getConfig();
    }

    public String createDatafeed() throws SymClientException {
        Response response = null;
        try {
            logger.info("Creating new datafeed for bot {} .....", botClient.getBotUserInfo().getUsername());
            response = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX
                + config.getAgentHost() + ":" + config.getAgentPort())
                .path(AgentConstants.CREATEDATAFEED)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(null);
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    logger.error("createDatafeed error ", ex);
                    return createDatafeed();
                }
                return null;
            } else {
                StringId datafeedId = response.readEntity(StringId.class);
                logger.info("Created new datafeed {} for bot {}", datafeedId, botClient.getBotUserInfo().getUsername());
                return datafeedId.getId();
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<DatafeedEvent> readDatafeed(String id)
            throws SymClientException {
        List<DatafeedEvent> datafeedEvents = null;
        Response response = null;
        logger.debug("Reading datafeed {}", id);
        try {
            WebTarget webTarget = botClient.getAgentClient().target(
                CommonConstants.HTTPSPREFIX
                    + config.getAgentHost()
                    + ":" + config.getAgentPort());
                response = webTarget.path(AgentConstants.READDATAFEED.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                    botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken",
                    botClient.getSymAuth().getKmToken()).get();
            if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
                logger.error("Datafeed read error for request " + webTarget.getUri());
                handleError(response, botClient);
            } else {
                if (response.getStatus() == CommonConstants.NOCONTENT) {
                    datafeedEvents = new ArrayList<>();
                } else {
                    datafeedEvents = response.readEntity(DatafeedEventsList.class);
                }
            }
            return datafeedEvents;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
