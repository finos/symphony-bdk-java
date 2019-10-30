package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatafeedClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;
    private SymConfig config;

    public DatafeedClient(SymBotClient client) {
        this.botClient = client;
        this.config = client.getConfig();
    }

    private String getAgentTarget() {
        return CommonConstants.HTTPS_PREFIX + config.getAgentHost() + ":" + config.getAgentPort();
    }

    public String createDatafeed() throws SymClientException {
        Response response = null;
        StringId datafeedId = null;
        try {
            logger.info("Creating new datafeed for bot {}..", botClient.getBotUserInfo().getUsername());
            response = botClient.getAgentClient().target(getAgentTarget())
                .path(AgentConstants.CREATEDATAFEED)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .post(null);
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    logger.error("createDatafeed error ", ex);
                    return createDatafeed();
                }
            } else {
                datafeedId = response.readEntity(StringId.class);
                logger.info("Created new datafeed {} for bot {}", datafeedId.getId(),
                    botClient.getBotUserInfo().getUsername());

                writeDatafeedIdToDisk(botClient.getConfig(), datafeedId.getId());
            }
            return datafeedId.getId();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private void writeDatafeedIdToDisk(SymConfig config, String datafeedId) {
        String agentHostPort = config.getAgentHost() + ":" + config.getAgentPort();

        File file = new File("." + File.separator + "datafeed.id");
        if (file.isDirectory()) {
            file = new File("." + File.separator + "datafeed.id" + File.separator + "datafeed.id");
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(datafeedId + "@" + agentHostPort);
            fw.flush();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public List<DatafeedEvent> readDatafeed(String id) throws SymClientException {
        List<DatafeedEvent> datafeedEvents = null;
        Response response = null;
        logger.debug("Reading datafeed {}", id);
        try {
            WebTarget webTarget = botClient.getAgentClient().target(getAgentTarget());
            response = webTarget
                .path(AgentConstants.READDATAFEED.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken", botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymAuth().getKmToken())
                .get();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                logger.error("Datafeed read error for request " + webTarget.getUri());
                handleError(response, botClient);
            } else if (response.getStatusInfo().getFamily() == Response.Status.Family.CLIENT_ERROR) {
                ((SymLoadBalancedConfig) config).rotateAgent();
            } else {
                if (response.getStatus() == CommonConstants.NO_CONTENT) {
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
