package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;

public class FirehoseClient extends APIClient {
    private SymBotClient botClient;

    public FirehoseClient(SymBotClient client) {
        this.botClient = client;
    }

    public String createFirehose() throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.CREATEFIREHOSE)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
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
    }

    public List<DatafeedEvent> readFirehose(String id) throws SymClientException {
        Invocation.Builder builder = botClient.getAgentClient()
            .target(botClient.getConfig().getAgentUrl())
            .path(AgentConstants.READFIREHOSE.replace("{id}", id))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());
        List<DatafeedEvent> firehoseEvents = null;

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                handleError(response, botClient);
            } else {
                if (response.getStatus() == CommonConstants.NO_CONTENT) {
                    firehoseEvents = new ArrayList<>();
                } else {
                    firehoseEvents = response.readEntity(DatafeedEventsList.class);
                }
            }
            return firehoseEvents;
        }
    }
}
