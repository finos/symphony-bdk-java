package clients.symphony.api;

import clients.ISymClient;
import clients.symphony.api.constants.CommonConstants;
import clients.symphony.api.constants.PodConstants;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import model.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamsClient extends APIClient {
    private ISymClient botClient;
    private String podBaseUri;

    public StreamsClient(ISymClient client) {
        botClient = client;
        podBaseUri = CommonConstants.HTTPS_PREFIX + botClient.getConfig().getPodHost()
            + ":" + botClient.getConfig().getPodPort();
    }

    public String getUserIMStreamId(Long userId) throws SymClientException {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        return getUserListIM(userIdList);
    }

    public String getUserListIM(List<Long> userIdList) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.GETIM)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(userIdList, MediaType.APPLICATION_JSON))) {

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserListIM(userIdList);
                }
                return null;
            }
            return response.readEntity(StringId.class).getId();
        }
    }

    public RoomInfo createRoom(Room room) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.CREATEROOM)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(room, MediaType.APPLICATION_JSON))) {

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return createRoom(room);
                }
                return null;
            }
            return response.readEntity(RoomInfo.class);
        }
    }

    public void addMemberToRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.ADDMEMBER.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    addMemberToRoom(streamId, userId);
                }
            }
        }
    }

    public void removeMemberFromRoom(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.REMOVEMEMBER.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    removeMemberFromRoom(streamId, userId);
                }
            }
        }
    }

    public RoomInfo getRoomInfo(String streamId) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.GETROOMINFO.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomInfo(streamId);
                }
                return null;
            }
            return response.readEntity(RoomInfo.class);
        }
    }

    public RoomInfo updateRoom(String streamId, Room room) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.UPDATEROOMINFO.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(room, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return updateRoom(streamId, room);
                }
                return null;
            } else {
                RoomInfo roomInfo = response.readEntity(RoomInfo.class);
                return roomInfo;
            }
        }
    }

    public StreamInfo getStreamInfo(String streamId) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.GETSTREAMINFO.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getStreamInfo(streamId);
                }
                return null;
            } else {
                return response.readEntity(StreamInfo.class);
            }
        }
    }

    public List<RoomMember> getRoomMembers(String streamId) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.GETROOMMEMBERS.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .get()) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getRoomMembers(streamId);
                }
                return null;
            }
            return response.readEntity(MemberList.class);
        }

    }

    public void activateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId, true);
    }

    public void deactivateRoom(String streamId) throws SymClientException {
        setActiveRoom(streamId, false);
    }

    private void setActiveRoom(String streamId, boolean active) throws SymClientException {
        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.SETACTIVE.replace("{id}", streamId))
            .queryParam("active", active)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(null)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    setActiveRoom(streamId, active);
                }
            }
        }
    }

    public void promoteUserToOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.PROMOTEOWNER.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    promoteUserToOwner(streamId, userId);
                }
            }
        }
    }

    public void demoteUserFromOwner(String streamId, Long userId) throws SymClientException {
        NumericId id = new NumericId(userId);

        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.DEMOTEOWNER.replace("{id}", streamId))
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(id, MediaType.APPLICATION_JSON))) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    demoteUserFromOwner(streamId, userId);
                }
            }
        }
    }

    public RoomSearchResult searchRooms(RoomSearchQuery query, int skip, int limit) throws SymClientException, NoContentException {
        RoomSearchResult result;
        WebTarget builder = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.SEARCHROOMS);

        if (skip > 0) {
            builder = builder.queryParam("skip", skip);
        }
        if (limit > 0) {
            builder = builder.queryParam("limit", limit);
        }
        if (query.getLabels() == null) {
            query.setLabels(new ArrayList<>());
        }

        try (Response response = builder.request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(query, MediaType.APPLICATION_JSON))) {

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return searchRooms(query, skip, limit);
                }
                return null;
            } else if (response.getStatus() == 204) {
                throw new NoContentException("No messages found");
            } else {
                result = response.readEntity(RoomSearchResult.class);
            }

            return result;
        }
    }

    public List<StreamListItem> getUserStreams(List<String> streamTypes, boolean includeInactiveStreams) throws SymClientException {
        List<Map> inputStreamTypes = new ArrayList<>();
        if (streamTypes != null) {
            for (String type : streamTypes) {
                Map<String, String> streamTypesMap = new HashMap<>();
                streamTypesMap.put("type", type);
                inputStreamTypes.add(streamTypesMap);
            }
        }
        Map<String, Object> input = new HashMap<>();
        input.put("streamTypes", inputStreamTypes);
        input.put("includeInactiveStreams", includeInactiveStreams);

        try (Response response = botClient.getPodClient().target(podBaseUri)
            .path(PodConstants.LISTUSERSTREAMS)
            .request(MediaType.APPLICATION_JSON)
            .header("sessionToken", botClient.getSymAuth().getSessionToken())
            .post(Entity.entity(input, MediaType.APPLICATION_JSON))) {

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                try {
                    handleError(response, botClient);
                } catch (UnauthorizedException ex) {
                    return getUserStreams(streamTypes, includeInactiveStreams);
                }
                return null;
            }

            return response.readEntity(StreamInfoList.class);
        }
    }

    public StreamListItem getUserWallStream() throws SymClientException {
        List<String> streamTypes = new ArrayList<>();
        streamTypes.add("POST");
        return getUserStreams(streamTypes, false).get(0);
    }
}
