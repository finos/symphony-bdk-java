package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.CommonConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.FirehoseListResponse;
import model.FirehoseReadRequest;
import model.FirehoseReadResponse;
import model.StringId;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import static clients.symphony.api.constants.AgentConstants.*;

public class FirehoseV2Client extends APIClient {
    private SymBotClient botClient;
    private static String AGENT_BASE_URL;

    public FirehoseV2Client(SymBotClient client) {
        this.botClient = client;
        AGENT_BASE_URL = String.format("%s%s:%s",
            CommonConstants.HTTPS_PREFIX,
            botClient.getConfig().getAgentHost(),
            botClient.getConfig().getAgentPort()
        );
    }

    private Response getAgent(String path) {
        return buildRequest(path).get();
    }

    private Response postAgent(String path, Entity<?> payload) {
        return buildRequest(path).post(payload);
    }

    private Invocation.Builder buildRequest(String path) {
        return this.botClient.getAgentClient().target(AGENT_BASE_URL)
            .path(path)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .header("keyManagerToken", botClient.getSymAuth().getKmToken());
    }

    public String createFirehose() throws SymClientException {
        try (Response response = postAgent(CREATEFIREHOSEV5, null)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(StringId.class).getId();
            }
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex) {
                return createFirehose();
            }
            return null;
        }
    }

    public FirehoseReadResponse readFirehose(String id, String ackId) throws SymClientException {
        String path = READFIREHOSEV5.replace("{id}", id);
        Entity entity = (ackId == null) ? null :
            Entity.entity(new FirehoseReadRequest(ackId), MediaType.APPLICATION_JSON);

        try (Response response = postAgent(path, entity)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                if (response.getStatus() == CommonConstants.NO_CONTENT) {
                    return new FirehoseReadResponse();
                }
                return response.readEntity(FirehoseReadResponse.class);
            }
            handleError(response, botClient);
        }
        return null;
    }

    public void deleteFirehose(String id) throws SymClientException {
        String path = DELETEFIREHOSEV5.replace("{id}", id);
        try (Response response = postAgent(path, null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                handleError(response, botClient);
            }
        }
    }

    public List<FirehoseListResponse> listFirehose() throws SymClientException {
        try (Response response = getAgent(LISTFIREHOSEV5)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                if (response.getStatus() == CommonConstants.NO_CONTENT) {
                    return new ArrayList<>();
                }
                return response.readEntity(new GenericType<List<FirehoseListResponse>>() {});
            }
            handleError(response, botClient);
        }
        return null;
    }
}
