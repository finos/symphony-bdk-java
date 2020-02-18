package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
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
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

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
            .header("keyManagerToken", botClient.getSymAuth().getKmToken())
            .header("Cache-Control", "no-cache");

        try (Response response = builder.post(null)) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
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
            .header("keyManagerToken", botClient.getSymAuth().getKmToken())
            .header("Cache-Control", "no-cache");
        
        List<DatafeedEvent> firehoseEvents = null;

        try (Response response = builder.get()) {
            if (response.getStatusInfo().getFamily() != SUCCESSFUL) {
                handleError(response, botClient);
            } else {
                if (response.getStatus() == NO_CONTENT.getStatusCode()) {
                    firehoseEvents = new ArrayList<>();
                } else {
                    firehoseEvents = response.readEntity(DatafeedEventsList.class);
                }
            }
            return firehoseEvents;
        }
    }
}
