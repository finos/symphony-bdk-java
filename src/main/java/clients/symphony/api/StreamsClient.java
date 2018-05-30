package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.*;
import model.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class StreamsClient extends APIClient {
    private ISymClient botClient;
    public StreamsClient(ISymClient client) {
        botClient=client;
    }

    public String getUserIMStreamId(Long userId) throws SymClientException {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        return getUserListIM(userIdList);
    }

    public String getUserListIM(List<Long> userIdList) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETIM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(userIdList, MediaType.APPLICATION_JSON));
        String streamId = response.readEntity(StringId.class).getId();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
               return getUserListIM(userIdList);
            }
            return null;
        }
        return streamId;
    }

    public RoomInfo createRoom(Room room) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.CREATEROOM)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));
        RoomInfo roomInfo = response.readEntity(RoomInfo.class);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return createRoom(room);
            }
            return null;
        }
        return roomInfo;
    }

    public void addMemberToRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.ADDMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                addMemberToRoom(streamId,userId);
            }
        }
    }

    public void removeMemberFromRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.REMOVEMEMBER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                removeMemberFromRoom(streamId,userId);
            }
        }
    }

    public RoomInfo getRoomInfo(String streamId) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getRoomInfo(streamId);
            }
            return null;
        }
        return response.readEntity(RoomInfo.class);
    }

    public RoomInfo updateRoom(String streamId, Room room) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.UPDATEROOMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(room, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return updateRoom(streamId,room);
            }
            return null;
        }
        else {
            RoomInfo roomInfo = response.readEntity(RoomInfo.class);
            return roomInfo;
        }
    }

    //TODO: CHECK WHY 404
    public StreamInfo getStreamInfo(String streamId) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETSTREAMINFO.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                return getStreamInfo(streamId);
            }
            return null;
        }
        else {
            return response.readEntity(StreamInfo.class);
        }

    }

    public List<RoomMember> getRoomMembers(String streamId) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.GETROOMMEMBERS.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
               return getRoomMembers(streamId);
            }
            return null;
        }
        return response.readEntity(MemberList.class);

    }

    public void activateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId,true);
    }

    public void deactivateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId,false);
    }

    //TODO: CHECK WHY 403
    private void setActiveRoom(String streamId, boolean active) throws SymClientException {
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.SETACTIVE.replace("{id}", streamId))
                .queryParam("active", active)
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( null);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                setActiveRoom(streamId,active);
            }
        }
    }

    public void promoteUserToOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.PROMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                promoteUserToOwner(streamId,userId);
            }
        }
    }

    public void demoteUserFromOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);
        Response response
                = botClient.getPodClient().target(CommonConstants.HTTPSPREFIX + botClient.getConfig().getPodHost() + ":" + botClient.getConfig().getPodPort())
                .path(PodConstants.DEMOTEOWNER.replace("{id}", streamId))
                .request(MediaType.APPLICATION_JSON)
                .header("sessionToken",botClient.getSymAuth().getSessionToken())
                .post( Entity.entity(id, MediaType.APPLICATION_JSON));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            try {
                handleError(response, botClient);
            } catch (UnauthorizedException ex){
                demoteUserFromOwner(streamId,userId);
            }
        }
    }
}
