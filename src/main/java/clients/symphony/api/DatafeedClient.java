package clients.symphony.api;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import configuration.SymConfig;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.UnauthorizedException;
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
    private SymBotAuth botAuth;
    private SymConfig config;

    public DatafeedClient(SymBotClient client){
        this.botClient = client;
        this.botAuth = client.getSymBotAuth();
        this.config = client.getConfig();
    }


    public String createDatafeed(){

        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + config.getAgentHost() + ":" + config.getAgentPort())
                .path(AgentConstants.CREATEDATAFEED)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botAuth.getSessionToken())
                .header("keyManagerToken", botAuth.getKmToken())
                .post(null);
        StringId datafeedId = response.readEntity(StringId.class);
        return datafeedId.getId();
    }

    public List<DatafeedEvent> readDatafeed(String id) throws Exception {
        List<DatafeedEvent> datafeedEvents = null;
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + config.getAgentHost() + ":" + config.getAgentPort())
                .path(AgentConstants.READDATAFEED.replace("{id}",id))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botAuth.getSessionToken())
                .header("keyManagerToken", botAuth.getKmToken())
                .get();
        if(response.getStatus() == 204){
            datafeedEvents = new ArrayList<DatafeedEvent>();
        } else if (response.getStatus() == 200) {
            datafeedEvents = response.readEntity(DatafeedEventsList.class);
        } else {
            handleError(response, botClient);

        }

        return datafeedEvents;

    }


}
