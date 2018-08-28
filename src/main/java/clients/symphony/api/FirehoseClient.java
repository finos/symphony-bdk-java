package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class FirehoseClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;

    public FirehoseClient(SymBotClient client) {
        this.botClient = client;
    }


    public String createFirehose() throws SymClientException {


        Response response
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX
                + botClient.getConfig().getAgentHost() + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.CREATEFIREHOSE)
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
                return createFirehose();
            }
            return null;
        } else {
            StringId firehoseId = response.readEntity(StringId.class);
            return firehoseId.getId();
        }
    }

    public List<DatafeedEvent> readFirehose(String id)
            throws SymClientException {
        List<DatafeedEvent> firehoseEvents = null;
        Response response
                = botClient.getAgentClient().target(
                CommonConstants.HTTPSPREFIX
                        + botClient.getConfig().getAgentHost()
                        + ":" + botClient.getConfig().getAgentPort())
                .path(AgentConstants.READFIREHOSE.replace("{id}", id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",
                        botClient.getSymAuth().getSessionToken())
                .header("keyManagerToken",
                        botClient.getSymAuth().getKmToken())
                .get();
        if (response.getStatusInfo().getFamily()
                != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        } else {
            if (response.getStatus() == CommonConstants.NOCONTENT) {
                firehoseEvents = new ArrayList<>();
            } else {
                firehoseEvents = response.readEntity(DatafeedEventsList.class);
            }
        }


        return firehoseEvents;

    }


}
