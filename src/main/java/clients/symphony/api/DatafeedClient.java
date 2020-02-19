package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

public final class DatafeedClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;
    private SymConfig config;

    public DatafeedClient(SymBotClient client) {
        this.botClient = client;
        this.config = client.getConfig();
    }

    public String createDatafeed() throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(config.getAgentUrl())
            .path(AgentConstants.CREATEDATAFEED)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        logger.info("Creating new datafeed for bot {}..", botClient.getBotUserInfo().getUsername());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                    return createDatafeed();
                } catch (UnauthorizedException ex) {
                    logger.error("createDatafeed error ", ex);
                    return null;
                }
            } else {
                StringId datafeedId = response.readEntity(StringId.class);
                logger.info("Created new datafeed {} for bot {}", datafeedId.getId(),
                    botClient.getBotUserInfo().getUsername());

                // if the reuseDatafeedID config isn't set (null), we assume its default value as true
                if (botClient.getConfig().getReuseDatafeedID() == null || botClient.getConfig().getReuseDatafeedID()) {
                    writeDatafeedIdToDisk(botClient.getConfig(), datafeedId.getId());
                }

                return datafeedId.getId();
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
        WebTarget webTarget = botClient.getAgentClient().target(config.getAgentUrl());
        Invocation.Builder builder = webTarget
            .path(AgentConstants.READDATAFEED.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        List<DatafeedEvent> datafeedEvents = null;

        logger.debug("Reading datafeed {}", id);
        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                logger.error("Datafeed read error for request: {}", webTarget.getUri());
                handleError(response, botClient);
            } else if (response.getStatusInfo().getFamily() == CLIENT_ERROR) {
                ((SymLoadBalancedConfig) config).rotateAgent();
            } else {
                if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                    datafeedEvents = new ArrayList<>();
                } else {
                    datafeedEvents = response.readEntity(DatafeedEventsList.class);
                }
            }
            return datafeedEvents;
        }
    }
}
