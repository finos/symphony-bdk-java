package clients.symphony.api;

import authentication.ISymBotAuth;
import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.*;
import model.ClientError;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.StringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DatafeedClient extends  APIClient{
    private final Logger logger = LoggerFactory.getLogger(DatafeedClient.class);
    private SymBotClient botClient;
    private ISymBotAuth botAuth;
    private SymConfig config;

    public DatafeedClient(SymBotClient client){
        this.botClient = client;
        this.botAuth = client.getSymBotAuth();
        this.config = client.getConfig();
    }


    public String createDatafeed() throws SymClientException {


        Response response
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + config.getAgentHost() + ":" + config.getAgentPort())
                .path(AgentConstants.CREATEDATAFEED)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botAuth.getSessionToken())
                .header("keyManagerToken", botAuth.getKmToken())
                .post(null);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return createDatafeed();
            }            return null;
        }
        else {
            StringId datafeedId = response.readEntity(StringId.class);
            return datafeedId.getId();
        }
    }

    public List<DatafeedEvent> readDatafeed(String id) throws SymClientException {
        List<DatafeedEvent> datafeedEvents = null;
        Response response
                = botClient.getAgentClient().target(CommonConstants.HTTPSPREFIX + config.getAgentHost() + ":" + config.getAgentPort())
                .path(AgentConstants.READDATAFEED.replace("{id}",id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botAuth.getSessionToken())
                .header("keyManagerToken", botAuth.getKmToken())
                .get();
        if(response.getStatus() == 204){
            datafeedEvents = new ArrayList<>();
        } else if (response.getStatus() == 200) {
            datafeedEvents = response.readEntity(DatafeedEventsList.class);
        } else {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return readDatafeed(createDatafeed());
            }

        }

        return datafeedEvents;

    }


}
