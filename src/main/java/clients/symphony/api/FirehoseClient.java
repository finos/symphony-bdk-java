package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirehoseClient extends APIClient {
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;

    public FirehoseClient(SymBotClient client) {
        this.botClient = client;
    }


    public String createFirehose() throws SymClientException {


        Response response = null;

        try {
            response = botClient.getAgentClient().target(CommonConstants.HTTPS_PREFIX
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
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public List<DatafeedEvent> readFirehose(String id)
            throws SymClientException {
        List<DatafeedEvent> firehoseEvents = null;
        Response response = null;

        try {
            response = botClient.getAgentClient().target(
                CommonConstants.HTTPS_PREFIX
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
                if (response.getStatus() == CommonConstants.NO_CONTENT) {
                    firehoseEvents = new ArrayList<>();
                } else {
                    firehoseEvents = response.readEntity(DatafeedEventsList.class);
                }
            }


            return firehoseEvents;
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }


}
