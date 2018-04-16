package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.APIClientErrorException;
import exceptions.ForbiddenException;
import exceptions.ServerErrorException;
import exceptions.UnauthorizedException;
import model.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class StreamsClient extends APIClient {
    private SymBotClient botClient;
    public StreamsClient(SymBotClient client) {
        botClient=client;
    }

    public String getUserIMStreamId(Long userId) throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        return getUserListIM(userIdList);
    }

    public String getUserListIM(List<Long> userIdList) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETIM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(userIdList, MediaType.APPLICATION_JSON));
        String streamId = response.readEntity(StringId.class).getId();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        return streamId;
    }

    public RoomInfo createRoom(Room room) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.CREATEROOM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));
        RoomInfo roomInfo = response.readEntity(RoomInfo.class);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        return roomInfo;
    }

    public void addMemberToRoom(String streamId, Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        NumericId id = new NumericId(userId);
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADDMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        }
    }

    public void removeMemberFromRoom(String streamId, Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        NumericId id = new NumericId(userId);
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.REMOVEMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        }
    }

    public RoomInfo getRoomInfo(String streamId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        return response.readEntity(RoomInfo.class);
    }

    public RoomInfo updateRoom(String streamId, Room room) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.UPDATEROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        else {
            RoomInfo roomInfo = response.readEntity(RoomInfo.class);
            return roomInfo;
        }
    }

    //TODO: CHECK WHY 404
    public StreamInfo getStreamInfo(String streamId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETSTREAMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        else {
            return response.readEntity(StreamInfo.class);
        }

    }

    public List<RoomMember> getRoomMembers(String streamId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMMEMBERS.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
            return null;
        }
        return response.readEntity(MemberList.class);

    }

    public void activateRoom(String streamId) throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        setActiveRoom(streamId,true);
    }

    public void deactivateRoom(String streamId) throws APIClientErrorException, ForbiddenException, ServerErrorException, UnauthorizedException {
        setActiveRoom(streamId,false);
    }

    //TODO: CHECK WHY 403
    private void setActiveRoom(String streamId, boolean active) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.SETACTIVE.replace("{id}", streamId))
                .queryParam("active", active)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( null);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        }
    }

    public void promoteUserToOwner(String streamId, Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        NumericId id = new NumericId(userId);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.PROMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);

        }
    }

    public void demoteUserFromOwner(String streamId, Long userId) throws UnauthorizedException, ForbiddenException, ServerErrorException, APIClientErrorException {
        NumericId id = new NumericId(userId);
        Client client = ClientBuilder.newClient();
        Response response
                = client.target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.DEMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymBotAuth().getSessionToken())
                .header("keyManagerToken", botClient.getSymBotAuth().getKmToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response, botClient);
        }
    }
}
