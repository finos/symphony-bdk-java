package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.AgentConstants;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.UnauthorizedException;
import model.InboundConnectionRequest;
import model.InboundConnectionRequestList;
import model.InboundMessage;
import model.StringId;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.util.List;

public class ConnectionsClient extends APIClient {
    private SymBotClient botClient;

    public ConnectionsClient(SymBotClient client) {
        botClient = client;
    }

    public List<InboundConnectionRequest> getPendingConnections() throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
       return getConnections(null, null);
    }

    public List<InboundConnectionRequest> getInboundPendingConnections() throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        return getConnections("PENDING_INCOMING", null);
    }

    public List<InboundConnectionRequest> getAllConnections() throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        return getConnections("ALL", null);
    }

    public List<InboundConnectionRequest> getAcceptedConnections() throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        return getConnections("ACCEPTED", null);
    }

    public List<InboundConnectionRequest> getRejectedConnections() throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        return getConnections("REJECTED", null);
    }

    public List<InboundConnectionRequest> getConnections(String status, List<Long> userIds) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        boolean userList = false;
        StringBuilder userIdList= new StringBuilder();
        if(userIds!=null) {
            if (!userIds.isEmpty()) {
                userList = true;
                userIdList.append(userIds.get(0));
                for (int i = 1; i < userIds.size(); i++) {
                    userIdList.append("," + userIds.get(i));
                }
            }
        }
        Client client = ClientBuilder.newClient();
        WebTarget builder
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETCONNECTIONS);

        if(status!=null){
            builder.queryParam("status", status);
        }
        if(userList){
            builder.queryParam("userIds", userIdList.toString());
        }

        Response response = builder.request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        } else {
            return response.readEntity(InboundConnectionRequestList.class);
        }
    }

    public InboundConnectionRequest acceptConnectionRequest(Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        UserId userIdObject = new UserId();
        userIdObject.setUserId(userId);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ACCEPTCONNECTION)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .post(Entity.entity(userId, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        } else {
            return response.readEntity(InboundConnectionRequest.class);
        }
    }

    public InboundConnectionRequest rejectConnectionRequest(Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        UserId userIdObject = new UserId();
        userIdObject.setUserId(userId);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.REJECTCONNECTION)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .post(Entity.entity(userId, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        } else {
            return response.readEntity(InboundConnectionRequest.class);
        }
    }

    public InboundConnectionRequest sendConnectionRequest(Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        UserId userIdObject = new UserId();
        userIdObject.setUserId(userId);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.SENDCONNECTIONREQUEST)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .post(Entity.entity(userId, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        } else {
            return response.readEntity(InboundConnectionRequest.class);
        }
    }

    public InboundConnectionRequest getConnectionRequestStatus(Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETCONNECTIONSTATUS.replace("{userId}", Long.toString(userId)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        } else {
            return response.readEntity(InboundConnectionRequest.class);
        }
    }

    public void removeConnection(Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX +  botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.REMOVECONNECTION.replace("{userId}", Long.toString(userId)))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .post(null);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        }
    }

    private class UserId{
        Long userId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

}
